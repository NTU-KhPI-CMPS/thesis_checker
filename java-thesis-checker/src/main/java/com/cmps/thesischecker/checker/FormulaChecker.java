package com.cmps.thesischecker.checker;

import com.cmps.thesischecker.model.ErrorCategory;
import com.cmps.thesischecker.model.FormatError;
import com.cmps.thesischecker.requirements.RequirementsHolder;
import com.cmps.thesischecker.utils.FormulaUtils;
import com.cmps.thesischecker.utils.MainContentUtils;
import com.cmps.thesischecker.utils.StyleUtils;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFStyles;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.FileInputStream;
import java.io.StringReader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Checks formula formatting per standard:
 * - one blank line before/after a formula, and after any "де..." notation block;
 * - formula paragraph is aligned CENTER or RIGHT (if numbered);
 * - only one formula per line;
 * - formula is built with Word's built-in Equation tool (has an m:oMath object);
 * - formula numbering follows the current Heading1 chapter;
 * - formulas typed manually (no OMath object) but ending with a "(N.N)"-style
 *   numbering are flagged, since they bypass the required Equation tool.
 */
public class FormulaChecker implements Checker {

    private static final Pattern HEADING_NUMBER_PATTERN = Pattern.compile("^\\s*(\\d+)");
    private static final Pattern NUMBER_ONLY_PATTERN = Pattern.compile("^\\d+\\.\\d+$");
    private static final Pattern PLAIN_FORMULA_NUMBER_PATTERN = Pattern.compile("\\(\\s*\\d+\\.\\d+\\s*\\)[.,;]?\\s*$");
    private static final Pattern TRAILING_LITERAL_PAREN_PATTERN = Pattern.compile("\\(([^()]*)\\)[.,;]?\\s*$");

    private final double expectedFontSize = Double.parseDouble(RequirementsHolder.getFontSize());

    /**
     * Returns the error category for this checker.
     *
     * @return the formula error category
     */
    @Override
    public ErrorCategory getErrorCategory() {
        return ErrorCategory.FORMULA;
    }

    /**
     * Checks a Word document for formula formatting issues.
     *
     * @param filePath path to the .docx file to check
     * @return all detected formula format errors, or an empty list if none are found
     */
    @Override
    public List<FormatError> check(String filePath) {
        List<FormatError> allErrors = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(filePath);
             XWPFDocument doc = new XWPFDocument(fis)) {

            List<XWPFParagraph> paragraphs = MainContentUtils.getMainContentParagraphs(doc);
            int[] currentChapter = {0};
            int[] expectedNumberInChapter = {1};

            for (int i = 0; i < paragraphs.size(); i++) {
                XWPFParagraph paragraph = paragraphs.get(i);

                if (StyleUtils.isHeading1(paragraph)) {
                    updateChapter(paragraph, currentChapter, expectedNumberInChapter);
                    continue;
                }

                if (!FormulaUtils.isFormulaOnlyParagraph(paragraph)) {
                    allErrors.addAll(
                            checkPlainTextFormulaCandidate(
                                    paragraph,
                                    currentChapter[0],
                                    expectedNumberInChapter
                            )
                    );
                    continue;
                }

                allErrors.addAll(
                        checkFormulaBlock(
                                paragraphs,
                                i,
                                currentChapter[0],
                                expectedNumberInChapter
                        )
                );
            }
        } catch (Exception e) {
            allErrors.add(buildException(e));
        }

        return allErrors;
    }

    /**
     * Detects paragraphs that end with formula-style numbering and have no OMath object.
     *
     * @param paragraph the non-OMath paragraph to inspect
     * @return the detected formula-related errors, or an empty list if the paragraph is clean
     */
    private List<FormatError> checkPlainTextFormulaCandidate(XWPFParagraph paragraph, int currentChapter, int[] expectedNumberInChapter) {
        List<FormatError> errorList = new ArrayList<>();

        String text = paragraph.getText();
        if (text == null) {
            return errorList;
        }

        String trimmed = text.trim();
        if (trimmed.isEmpty()) {
            return errorList;
        }

        if (!PLAIN_FORMULA_NUMBER_PATTERN.matcher(trimmed).find()) {
            return errorList;
        }

        errorList.add(buildFormulaToolWarning(trimmed));

        Matcher literalParen = TRAILING_LITERAL_PAREN_PATTERN.matcher(trimmed);
        if (literalParen.find()) {
            String candidate = literalParen.group(1).trim();
            if (!candidate.isEmpty()) {
                errorList.addAll(
                        validateFormulaNumber(
                                candidate,
                                trimmed,
                                currentChapter,
                                expectedNumberInChapter
                        )
                );
            }
        }

        return errorList;
    }

    /**
     * Validates a single formula block: surrounding spacing, alignment, one-per-line,
     * numbering, and the notation ("де ...") block that may follow it.
     *
     * @param paragraphs               all paragraphs of the document
     * @param formulaIndex             index of the formula paragraph
     * @param currentChapter           the current chapter number, from the last Heading1
     * @param expectedNumberInChapter  mutable holder for the expected next formula number
     * @return the detected formula-related errors for the block, or an empty list if none are found
     */
    private List<FormatError> checkFormulaBlock(List<XWPFParagraph> paragraphs, int formulaIndex, int currentChapter,
                                      int[] expectedNumberInChapter) {
        List<FormatError> errorList = new ArrayList<>();
        XWPFParagraph formulaParagraph = paragraphs.get(formulaIndex);
        String formulaText = displayText(formulaParagraph);

        if (formulaIndex == 0 || !FormulaUtils.isBlankParagraph(paragraphs.get(formulaIndex - 1))) {
            errorList.add(buildSpacingError("err_formula_spacing_before",
                    "Формула без порожнього рядка перед неї",
                    formulaText,
                    formulaIndex > 0 ? displayText(paragraphs.get(formulaIndex - 1)) : "Початок документу",
                    "Порожній рядок перед формулою"));
        }

        String alignment = new AlignmentChecker().getAlignment(formulaParagraph);
        if (!"CENTER".equalsIgnoreCase(alignment) && !"RIGHT".equalsIgnoreCase(alignment)) {
            errorList.add(buildAlignmentError(formulaText, alignment));
        }

        List<String> formulaXmls = FormulaUtils.getFormulaXmls(formulaParagraph);
        int[] numberCount = {0};
        for (String formulaXml : formulaXmls) {
            errorList.addAll(checkFormulaXml(
                    formulaXml,
                    formulaText,
                    currentChapter,
                    formulaParagraph,
                    expectedNumberInChapter,
                    numberCount
            ));
        }

        if (formulaXmls.size() > 1 || numberCount[0] > 1) {
            errorList.add(buildOneFormulaPerLineError(formulaText));
        }

        int nextIndex = formulaIndex + 1;
        if (nextIndex >= paragraphs.size()) {
            errorList.add(buildSpacingError("err_formula_spacing_after",
                    "Формула без порожнього рядка після неї",
                    formulaText,
                    formulaText,
                    "Порожній рядок після формули"));
            return errorList;
        }

        int afterFormulaIndex = nextIndex;

        if (FormulaUtils.isBlankParagraph(paragraphs.get(nextIndex))) {
            int cursor = nextIndex;
            while (cursor < paragraphs.size() && FormulaUtils.isBlankParagraph(paragraphs.get(cursor))) {
                cursor++;
            }

            if (cursor >= paragraphs.size() || !FormulaUtils.isNotationParagraph(paragraphs.get(cursor))) {
                return errorList;
            }

            errorList.add(buildSpacingError("err_formula_spacing_before_notation",
                    "Помилковий порожній рядок перед поясненням «де»",
                    formulaText,
                    formulaText,
                    "Пояснення «де» безпосередньо під формулою (без відступу)"));
            afterFormulaIndex = cursor;
        }

        if (FormulaUtils.isNotationParagraph(paragraphs.get(afterFormulaIndex))) {
            int blockEnd = afterFormulaIndex + 1;
            while (blockEnd < paragraphs.size() && FormulaUtils.isNotationContinuationLine(paragraphs.get(blockEnd))) {
                blockEnd++;
            }

            if (blockEnd >= paragraphs.size() || !FormulaUtils.isBlankParagraph(paragraphs.get(blockEnd))) {
                errorList.add(buildSpacingError("err_formula_notation_spacing",
                        "Відсутній порожній рядок після пояснення «де»",
                        formulaText,
                        formulaText,
                        "Порожній рядок після блоку пояснень"));
            }
            return errorList;
        }

        if (afterFormulaIndex == nextIndex) {
            errorList.add(buildSpacingError("err_formula_spacing_after",
                    "Формула без порожнього рядка після неї",
                    formulaText,
                    formulaText,
                    "Порожній рядок після формули"));
        }

        return errorList;
    }

    /**
     * Extracts the chapter number from the heading paragraph and resets the formula counter.
     *
     * @param heading                 the heading paragraph containing the chapter number
     * @param currentChapter          the mutable active chapter number tracker
     * @param expectedNumberInChapter the mutable active formula number tracker
     */
    private void updateChapter(XWPFParagraph heading, int[] currentChapter, int[] expectedNumberInChapter) {
        String text = heading.getText();
        if (text == null) return;

        Matcher matcher = HEADING_NUMBER_PATTERN.matcher(text.trim());
        if (matcher.find()) {
            currentChapter[0] = Integer.parseInt(matcher.group(1));
            expectedNumberInChapter[0] = 1;
        }
    }

    /**
     * Parses one formula XML block, extracts numbering, and checks the formula text.
     *
     * @param formulaXml               raw XML of the OMath element
     * @param paragraphText            text of the paragraph containing the formula
     * @param currentChapter           the current chapter number, from the last Heading1
     * @param expectedNumberInChapter  mutable holder for the expected next formula number
     * @param numberCount              mutable counter of formula numbers found
     * @return the detected formula-related errors for this XML block
     */
    private List<FormatError> checkFormulaXml(String formulaXml, String paragraphText, int currentChapter, XWPFParagraph formulaParagraph,
                                  int[] expectedNumberInChapter, int[] numberCount
    ) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(false);
            Document document = factory.newDocumentBuilder()
                    .parse(new InputSource(new StringReader(formulaXml)));

            Map<String, Double> fontIssues = new LinkedHashMap<>();
            List<FormatError> errorList = new ArrayList<>();

            NodeList runs = document.getElementsByTagName("m:r");
            for (int i = 0; i < runs.getLength(); i++) {
                Element run = (Element) runs.item(i);
                String text = getRunText(run);
                if (text.isBlank()) continue;
                double foundSize = checkRunSize(run, formulaParagraph);

                if (foundSize != Double.parseDouble(RequirementsHolder.getFontSize())) {
                    fontIssues.put(text, foundSize);
                }
            }

            if (!fontIssues.isEmpty()) {
                errorList.add(buildFontSizeError(paragraphText, fontIssues));
            }

            NodeList delimiters = document.getElementsByTagName("m:d");
            boolean foundStructuralNumber = false;
            for (int i = 0; i < delimiters.getLength(); i++) {
                Element delimiter = (Element) delimiters.item(i);
                String inner = flattenElementText(delimiter).trim();

                if (!NUMBER_ONLY_PATTERN.matcher(inner).matches()) {
                    continue;
                }

                foundStructuralNumber = true;
                numberCount[0]++;
                errorList.addAll(
                        validateFormulaNumber(
                                inner,
                                paragraphText,
                                currentChapter,
                                expectedNumberInChapter
                        )
                );
            }

            // Fallback to trailing literal "(...)" text when no structural number is present.
            if (!foundStructuralNumber) {
                String flatText = flattenElementText(document.getDocumentElement());
                Matcher literalParen = TRAILING_LITERAL_PAREN_PATTERN.matcher(flatText.trim());
                if (literalParen.find()) {
                    String candidate = literalParen.group(1).trim();
                    if (!candidate.isEmpty()) {
                        numberCount[0]++;
                        errorList.addAll(
                                validateFormulaNumber(
                                        candidate,
                                        paragraphText,
                                        currentChapter,
                                        expectedNumberInChapter
                                )
                        );
                    }
                }
            }

            return errorList;
        } catch (Exception e) {
            return List.of(buildException(e));
        }
    }

    /**
     * Extracts text content from a math run element.
     *
     * @param run the run element
     * @return the first text node value from the run, or an empty string if none exists
     */
    private String getRunText(Element run) {
        NodeList texts = run.getElementsByTagName("m:t");
        if (texts.getLength() == 0) {
            texts = run.getElementsByTagName("w:t");
        }
        if (texts.getLength() > 0) {
            return texts.item(0).getTextContent();
        }
        return "";
    }

    /**
     * Resolves the run size inside a formula and returns it in points.
     *
     * @param run              the XML Element containing run properties
     * @param formulaParagraph the paragraph that contains the formula
     * @return the resolved run size in points
     */
    private double checkRunSize(Element run, XWPFParagraph formulaParagraph) {
        NodeList sizes = run.getElementsByTagName("w:sz");

        double sizePt;
        if (sizes.getLength() > 0) {
            Element sz = (Element) sizes.item(0);
            int raw = Integer.parseInt(sz.getAttribute("w:val"));
            sizePt = raw / 2.0;
        } else {
            sizePt = getEffectiveParagraphFontSize(formulaParagraph);
        }

        return sizePt;
    }

    /**
     * Resolves the font size that applies to a paragraph.
     *
     * @param paragraph the paragraph to resolve the effective font size for
     * @return the effective font size in points, or 12.0 if no size is defined
     */
    private double getEffectiveParagraphFontSize(XWPFParagraph paragraph) {
        XWPFDocument document = paragraph.getDocument();
        XWPFStyles styles = document.getStyles();
        String styleId = paragraph.getStyleID();

        if (styleId == null) {
            styleId = StyleUtils.getNormalStyleId(styles);
        }

        return StyleUtils.getFontSizeFromParagraphStyle(document, styleId);
    }

    /**
     * Concatenates the text of every m:t descendant of the given element.
     *
     * @param element the element whose descendant m:t text should be collected
     * @return the combined text from all descendant math text nodes
     */
    private String flattenElementText(Element element) {
        NodeList texts = element.getElementsByTagName("m:t");
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < texts.getLength(); i++) {
            Node node = texts.item(i);
            String t = node.getTextContent();
            if (t != null) builder.append(t);
        }
        return builder.toString();
    }

    /**
     * Validates a formula number against the current chapter and expected sequence.
     *
     * @param numberText              the extracted formula number string
     * @param paragraphText           the text of the entire paragraph
     * @param currentChapter           the current chapter number
     * @param expectedNumberInChapter the expected next formula number
     * @return the detected formula-number validation errors, or an empty list if the number is valid
     */
    private List<FormatError> validateFormulaNumber(String numberText, String paragraphText, int currentChapter,
                                        int[] expectedNumberInChapter) {

        List<FormatError> errorList = new ArrayList<>();

        String[] parts = numberText.split("\\.");
        if (parts.length != 2) {
            errorList.add(buildFormatError(paragraphText, numberText, currentChapter + "." + expectedNumberInChapter[0]));
            return errorList;
        }

        int chapterInFormula;
        int numberInChapter;
        try {
            chapterInFormula = Integer.parseInt(parts[0]);
            numberInChapter = Integer.parseInt(parts[1]);
        } catch (NumberFormatException e) {
            errorList.add(buildFormatError(paragraphText, numberText, currentChapter + "." + expectedNumberInChapter[0]));
            return errorList;
        }

        if (chapterInFormula != currentChapter) {
            errorList.add(buildChapterMismatchError(paragraphText, numberText, currentChapter));
            return errorList;
        }

        if (numberInChapter != expectedNumberInChapter[0]) {
            errorList.add(buildSequenceError(paragraphText, numberText, currentChapter + "." + expectedNumberInChapter[0]));
        }

        expectedNumberInChapter[0] = numberInChapter + 1;

        return errorList;
    }

    /**
     * Creates a spacing-related FormatError.
     *
     * @param id            the unique error identifier
     * @param title         the detailed error title
     * @param paragraphText the text content of the paragraph
     * @param found         the incorrect format found in the document
     * @param expected      the expected spacing requirements description
     * @return a FormatError describing a spacing problem
     */
    private static FormatError buildSpacingError(String id, String title, String paragraphText, String found, String expected) {
        FormatError error = new FormatError();
        error.setId(id);
        error.setCategory(ErrorCategory.FORMULA);
        error.setSeverity("error");
        error.setTitle(title);
        error.setParagraphText(paragraphText);
        error.setFound(Set.of(found));
        error.setExpected(expected);
        return error;
    }

    /**
     * Creates an alignment-related FormatError.
     *
     * @param paragraphText the text content of the paragraph
     * @param found         the incorrect alignment layout found in the document
     * @return a FormatError describing an alignment problem
     */
    private static FormatError buildAlignmentError(String paragraphText, String found) {
        FormatError error = new FormatError();
        error.setId("err_formula_alignment");
        error.setCategory(ErrorCategory.FORMULA);
        error.setSeverity("error");
        error.setTitle("Формула вирівняна по лівому краю");
        error.setParagraphText(paragraphText);
        error.setFound(Set.of(found));
        error.setExpected(RequirementsHolder.getFormulaAlignment());
        return error;
    }

    /**
     * Creates a FormatError for multiple formulas in one paragraph.
     *
     * @param paragraphText the text content of the paragraph
     * @return a FormatError describing multiple formulas in one line
     */
    private static FormatError buildOneFormulaPerLineError(String paragraphText) {
        FormatError error = new FormatError();
        error.setId("err_formula_multiple_per_line");
        error.setCategory(ErrorCategory.FORMULA);
        error.setSeverity("error");
        error.setTitle("Кілька формул в одному рядку");
        error.setParagraphText(paragraphText);
        error.setExpected(RequirementsHolder.getFormulaMultiplePerLine());
        return error;
    }

    /**
     * Creates a warning for a formula-like paragraph without an OMath object.
     *
     * @param paragraphText the text content of the paragraph
     * @return a warning FormatError for a formula-like paragraph without OMath
     */
    private static FormatError buildFormulaToolWarning(String paragraphText) {
        FormatError error = new FormatError();
        error.setId("err_formula_not_tool");
        error.setCategory(ErrorCategory.FORMULA);
        error.setSeverity("warning");
        error.setTitle("Формула набрана вручну, а не через інструмент \"Формула\"");
        error.setParagraphText(paragraphText);
        error.setExpected("Формула має бути вставлена через Вставлення → Формула");
        return error;
    }

    /**
     * Creates a FormatError for a formula number that points to the wrong chapter.
     *
     * @param paragraphText  the text content of the paragraph
     * @param found          the erroneous formula numbering found
     * @param currentChapter the current active chapter number
     * @return a FormatError describing a chapter mismatch in formula numbering
     */
    private static FormatError buildChapterMismatchError(String paragraphText, String found, int currentChapter) {
        FormatError error = new FormatError();
        error.setId("err_formula_chapter_mismatch");
        error.setCategory(ErrorCategory.FORMULA);
        error.setSeverity("error");
        error.setTitle("Формула з неправильним розділом (наприклад 2.1 замість 1.1)");
        error.setParagraphText(paragraphText);
        error.setFound(Set.of(withParens(found)));
        error.setExpected("(" + currentChapter + ".x)");
        return error;
    }

    /**
     * Creates a FormatError for a formula number that breaks the sequence.
     *
     * @param paragraphText the text content of the paragraph
     * @param found         the sequence gap numbering found
     * @param expected      the perfect next sequence expected
     * @return a FormatError describing a sequence mismatch in formula numbering
     */
    private static FormatError buildSequenceError(String paragraphText, String found, String expected) {
        FormatError error = new FormatError();
        error.setId("err_formula_sequence");
        error.setCategory(ErrorCategory.FORMULA);
        error.setSeverity("error");
        error.setTitle("Формула з порушеною послідовністю (наприклад 1.3 замість 1.2)");
        error.setParagraphText(paragraphText);
        error.setFound(Set.of(withParens(found)));
        error.setExpected("(" + expected + ")");
        return error;
    }

    /**
     * Creates a FormatError for a malformed formula number.
     *
     * @param paragraphText the parent paragraph text
     * @param found         the broken numbering format found
     * @param expected      the expected standard format layout
     * @return a FormatError describing a malformed formula number
     */
    private static FormatError buildFormatError(String paragraphText, String found, String expected) {
        FormatError error = new FormatError();
        error.setId("err_formula_format");
        error.setCategory(ErrorCategory.FORMULA);
        error.setSeverity("error");
        error.setTitle("Формула з неправильним форматом номера (наприклад 1-1)");
        error.setParagraphText(paragraphText);
        error.setFound(Set.of(withParens(found)));
        error.setExpected("(" + expected + ")");
        return error;
    }

    /**
     * Creates a FormatError for formula text with incorrect font sizes.
     *
     * @param paragraphText the text content of the paragraph
     * @param fontIssues    a map of text fragments to their detected font sizes
     * @return a FormatError describing incorrect formula font sizes
     */
    private FormatError buildFontSizeError(String paragraphText, Map<String, Double> fontIssues) {
        FormatError error = new FormatError();
        error.setId("err_formula_font_size");
        error.setCategory(ErrorCategory.FORMULA);
        error.setSeverity("error");

        Set<String> foundSizes = fontIssues.values().stream()
                .map(size -> size + "pt")
                .collect(Collectors.toCollection(LinkedHashSet::new));

        String fragmentsList = String.join(", ", fontIssues.keySet());

        error.setTitle("Неправильний розмір шрифту у формулі \"" + paragraphText);
        error.setParagraphText("Фрагменти з неправильним розміром шрифту: " + fragmentsList);
        error.setFound(foundSizes);
        error.setExpected(expectedFontSize + "pt");
        return error;
    }

    /**
     * Wraps a number in parentheses unless it is already wrapped.
     *
     * @param text the raw number text
     * @return the text wrapped in one pair of parentheses, or the original text if already wrapped
     */
    private static String withParens(String text) {
        String trimmed = text.trim();
        if (trimmed.startsWith("(") && trimmed.endsWith(")")) {
            return trimmed;
        }
        return "(" + trimmed + ")";
    }

    /**
     * Creates a file-level error from a processing exception.
     *
     * @param e the captured Exception
     * @return a file-level FormatError created from the exception
     */
    private static FormatError buildException(Exception e) {
        FormatError error = new FormatError();
        error.setId("err_000");
        error.setCategory(ErrorCategory.FILE);
        error.setSeverity("error");
        error.setTitle("Помилка читання файлу: " + e.getMessage());
        error.setParagraphText("");
        return error;
    }

    /**
     * Extracts visible text from a paragraph or its formula XML.
     *
     * @param paragraph the source paragraph
     * @return the visible paragraph text, extracted formula text, or a placeholder if both are blank
     */
    private static String displayText(XWPFParagraph paragraph) {
        String text = paragraph.getText();
        if (text != null && !text.trim().isEmpty()) {
            return text.trim();
        }

        List<String> formulaXmls = FormulaUtils.getFormulaXmls(paragraph);
        if (!formulaXmls.isEmpty()) {
            StringBuilder formula = new StringBuilder();

            try {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                factory.setNamespaceAware(false);

                for (String xml : formulaXmls) {
                    Document document = factory.newDocumentBuilder()
                            .parse(new InputSource(new StringReader(xml)));

                    NodeList texts = document.getElementsByTagName("m:t");
                    for (int i = 0; i < texts.getLength(); i++) {
                        formula.append(texts.item(i).getTextContent());
                    }
                }

                if (!formula.toString().isBlank()) {
                    return formula.toString();
                }
            } catch (Exception ignored) {
            }

            return "[Формула]";
        }

        return "[Порожній рядок]";
    }
}
