package com.cmps.thesischecker.checker;

import com.cmps.thesischecker.model.ErrorCategory;
import com.cmps.thesischecker.model.FormatError;
import com.cmps.thesischecker.requirements.RequirementsHolder;
import com.cmps.thesischecker.utils.MathUtils;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.FileInputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Checks formula formatting per standard:
 * - one blank line before/after a formula, and after any "де..." notation block;
 * - formula paragraph is aligned CENTER or RIGHT (if numbered);
 * - only one formula per line;
 * - formula font size matches the document's standard size;
 * - formula numbering follows the current Heading1 chapter.
 */
public class FormulaChecker implements Checker {

    private static final Pattern HEADING_NUMBER_PATTERN = Pattern.compile("^\\s*(\\d+)");
    private final int expectedFontSize;

    public FormulaChecker() {
        this.expectedFontSize = Integer.parseInt(RequirementsHolder.getFontSize());
    }

    /**
     * Returns the error category for this checker.
     *
     * @return {@link ErrorCategory#FORMULA}
     */
    @Override
    public ErrorCategory getErrorCategory() {
        return ErrorCategory.FORMULA;
    }

    /**
     * Checks a Word document for formula formatting issues.
     *
     * @param filePath path to the .docx file to check
     * @return list of found format errors, empty if none
     */
    @Override
    public List<FormatError> check(String filePath) {
        List<FormatError> allErrors = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(filePath);
             XWPFDocument doc = new XWPFDocument(fis)) {

            List<XWPFParagraph> paragraphs = doc.getParagraphs();
            int[] currentChapter = {0};
            int[] expectedNumberInChapter = {1};

            for (int i = 0; i < paragraphs.size(); i++) {
                XWPFParagraph paragraph = paragraphs.get(i);

                if (isHeading1(paragraph)) {
                    updateChapter(paragraph, currentChapter, expectedNumberInChapter);
                    continue;
                }

                if (!MathUtils.isFormulaOnlyParagraph(paragraph)) {
                    continue;
                }

                validateFormulaBlock(paragraphs, i, currentChapter[0], expectedNumberInChapter, allErrors);
            }
        } catch (Exception e) {
            allErrors.add(buildException(e));
        }
        return allErrors;
    }

    /**
     * Validates a single formula block: surrounding spacing, alignment, one-per-line,
     * font size, numbering, and the notation ("де ...") block that may follow it.
     *
     * @param paragraphs               all paragraphs of the document
     * @param formulaIndex             index of the formula paragraph
     * @param currentChapter           the current chapter number, from the last Heading1
     * @param expectedNumberInChapter  mutable holder for the expected next formula number
     * @param allErrors                accumulator for found errors
     */
    private void validateFormulaBlock(List<XWPFParagraph> paragraphs, int formulaIndex, int currentChapter,
                                       int[] expectedNumberInChapter, List<FormatError> allErrors) {
        XWPFParagraph formulaParagraph = paragraphs.get(formulaIndex);
        String formulaText = displayText(formulaParagraph);

        // 1. Порожній рядок ПЕРЕД формулою
        if (formulaIndex == 0 || !MathUtils.isBlankParagraph(paragraphs.get(formulaIndex - 1))) {
            allErrors.add(buildSpacingError("err_formula_spacing_before",
                    "Формула без порожнього рядка перед нею",
                    formulaText,
                    formulaIndex > 0 ? displayText(paragraphs.get(formulaIndex - 1)) : "Початок документу"));
        }

        String alignment = new AlignmentChecker().getAlignment(formulaParagraph);
        if (!"CENTER".equalsIgnoreCase(alignment) && !"RIGHT".equalsIgnoreCase(alignment)) {
            allErrors.add(buildAlignmentError(formulaText, alignment));
        }

        List<String> formulaXmls = MathUtils.getFormulaXmls(formulaParagraph);
        int[] markerCount = {0};
        for (String formulaXml : formulaXmls) {
            checkFormulaXml(formulaXml, formulaText, currentChapter, expectedNumberInChapter, markerCount, allErrors);
        }

        if (formulaXmls.size() > 1 || markerCount[0] > 1) {
            allErrors.add(buildOneFormulaPerLineError(formulaText));
        }

        int nextIndex = formulaIndex + 1;
        if (nextIndex >= paragraphs.size()) {
            allErrors.add(buildSpacingError("err_formula_spacing_after",
                    "Формула без порожнього рядка після неї",
                    formulaText,
                    "Кінець документу"));
            return;
        }

        int afterFormulaIndex = nextIndex;

        if (MathUtils.isBlankParagraph(paragraphs.get(nextIndex))) {
            int cursor = nextIndex;
            while (cursor < paragraphs.size() && MathUtils.isBlankParagraph(paragraphs.get(cursor))) {
                cursor++;
            }

            if (cursor >= paragraphs.size() || !MathUtils.isNotationParagraph(paragraphs.get(cursor))) {
                return;
            }

            allErrors.add(buildSpacingError("err_formula_spacing_after",
                    "Формула без порожнього рядка після неї",
                    formulaText,
                    displayText(paragraphs.get(nextIndex))));
            afterFormulaIndex = cursor;
        }

        if (MathUtils.isNotationParagraph(paragraphs.get(afterFormulaIndex))) {
            int blockEnd = afterFormulaIndex + 1;
            while (blockEnd < paragraphs.size() && MathUtils.isNotationContinuationLine(paragraphs.get(blockEnd))) {
                blockEnd++;
            }

            if (blockEnd >= paragraphs.size() || !MathUtils.isBlankParagraph(paragraphs.get(blockEnd))) {
                allErrors.add(buildSpacingError("err_formula_notation_spacing",
                        "Формула з неправильним відступом після пояснення де",
                        formulaText,
                        blockEnd < paragraphs.size() ? displayText(paragraphs.get(blockEnd)) : "Кінець документу"));
            }
            return;
        }

        if (afterFormulaIndex == nextIndex) {
            allErrors.add(buildSpacingError("err_formula_spacing_after",
                    "Формула без порожнього рядка після неї",
                    formulaText,
                    displayText(paragraphs.get(nextIndex))));
        }
    }

    private boolean isHeading1(XWPFParagraph paragraph) {
        String styleId = paragraph.getStyle();
        return styleId != null && (styleId.equalsIgnoreCase("Heading1") || styleId.equalsIgnoreCase("1"));
    }

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
     * Parses a single formula's XML, checks the font size of every fragment, finds every
     * numbering marker ("#", possibly fused with adjacent text like "=m#") and validates
     * the number that follows it.
     *
     * @param formulaXml               raw XML of the OMath element
     * @param paragraphText            text of the paragraph containing the formula
     * @param currentChapter           the current chapter number, from the last Heading1
     * @param expectedNumberInChapter  mutable holder for the expected next formula number
     * @param markerCount              mutable counter of numbering markers found, used to
     *                                 detect multiple formulas sharing one paragraph
     * @param allErrors                accumulator for found errors
     */
    private void checkFormulaXml(String formulaXml, String paragraphText, int currentChapter,
                                  int[] expectedNumberInChapter, int[] markerCount, List<FormatError> allErrors) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(false);
            Document document = factory.newDocumentBuilder()
                    .parse(new InputSource(new StringReader(formulaXml)));

            NodeList runs = document.getElementsByTagName("m:r");
            for (int i = 0; i < runs.getLength(); i++) {
                Element run = (Element) runs.item(i);
                String text = getRunText(run);
                if (text.isBlank()) continue;
                checkRunSize(run, text, paragraphText, allErrors);
            }

            NodeList texts = document.getElementsByTagName("m:t");
            for (int i = 0; i < texts.getLength(); i++) {
                String current = texts.item(i).getTextContent().trim();
                if (!current.endsWith("#")) continue;

                markerCount[0]++;

                String numberText = getNumberAfterMarker(texts, i);
                if (numberText.isEmpty()) continue;

                validateFormulaNumber(numberText, paragraphText, currentChapter, expectedNumberInChapter, allErrors);
            }
        } catch (Exception e) {
            allErrors.add(buildException(e));
        }
    }

    private void checkRunSize(Element run, String text, String paragraphText, List<FormatError> allErrors) {
        NodeList sizes = run.getElementsByTagName("w:sz");

        double sizePt = 12.0;

        if (sizes.getLength() > 0) {
            Element sz = (Element) sizes.item(0);
            int raw = Integer.parseInt(sz.getAttribute("w:val"));
            sizePt = raw / 2.0;
        }

        if (sizePt != expectedFontSize) {
            allErrors.add(buildFontSizeError(paragraphText, text, sizePt));
        }
    }

    /**
     * Returns the raw text of the first non-empty m:t node following a numbering marker,
     * whatever that text is (valid number or not) — format validation happens afterwards
     * in {@link #validateFormulaNumber}, so a malformed value here still produces a useful
     * "wrong format" error instead of being silently skipped.
     *
     * @param texts       all m:t nodes of the formula
     * @param markerIndex index of the marker node
     * @return the next node's raw trimmed text, or empty string if none follows
     */
    private String getNumberAfterMarker(NodeList texts, int markerIndex) {
        for (int j = markerIndex + 1; j < texts.getLength(); j++) {
            String part = texts.item(j).getTextContent().trim();
            if (!part.isEmpty()) {
                return part;
            }
        }
        return "";
    }

    private void validateFormulaNumber(String numberText, String paragraphText, int currentChapter,
                                        int[] expectedNumberInChapter, List<FormatError> allErrors) {
        String[] parts = numberText.split("\\.");
        if (parts.length != 2) {
            allErrors.add(buildFormatError(paragraphText, numberText, currentChapter + "." + expectedNumberInChapter[0]));
            return;
        }

        int chapterInFormula;
        int numberInChapter;
        try {
            chapterInFormula = Integer.parseInt(parts[0]);
            numberInChapter = Integer.parseInt(parts[1]);
        } catch (NumberFormatException e) {
            allErrors.add(buildFormatError(paragraphText, numberText, currentChapter + "." + expectedNumberInChapter[0]));
            return;
        }

        if (chapterInFormula != currentChapter) {
            allErrors.add(buildChapterMismatchError(paragraphText, numberText, currentChapter));
        } else if (numberInChapter != expectedNumberInChapter[0]) {
            allErrors.add(buildSequenceError(paragraphText, numberText, currentChapter + "." + expectedNumberInChapter[0]));
        }

        expectedNumberInChapter[0] = numberInChapter + 1;
    }

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

    private static FormatError buildSpacingError(String id, String title, String paragraphText, String found) {
        FormatError error = new FormatError();
        error.setId(id);
        error.setCategory(ErrorCategory.FORMULA);
        error.setSeverity("error");
        error.setTitle(title);
        error.setParagraphText(paragraphText);
        error.setFound(Set.of(found));
        error.setExpected(RequirementsHolder.getFormulaSpacing());
        return error;
    }

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

    private FormatError buildFontSizeError(String paragraphText, String fragment, double foundSize) {
        FormatError error = new FormatError();
        error.setId("err_formula_font_size");
        error.setCategory(ErrorCategory.FORMULA);
        error.setSeverity("error");
        error.setTitle("Неправильний розмір шрифту у формулі (на фрагменті: \"" + fragment + "\")");
        error.setParagraphText(paragraphText);
        error.setFound(Set.of(foundSize + "pt"));
        error.setExpected(expectedFontSize + "pt");
        return error;
    }

    private static FormatError buildChapterMismatchError(String paragraphText, String found, int currentChapter) {
        FormatError error = new FormatError();
        error.setId("err_formula_chapter_mismatch");
        error.setCategory(ErrorCategory.FORMULA);
        error.setSeverity("error");
        error.setTitle("Формула з неправильним розділом (наприклад 2.1 замість 1.1)");
        error.setParagraphText(paragraphText);
        error.setFound(Set.of("(" + found + ")"));
        error.setExpected("(" + currentChapter + ".x)");
        return error;
    }

    private static FormatError buildSequenceError(String paragraphText, String found, String expected) {
        FormatError error = new FormatError();
        error.setId("err_formula_sequence");
        error.setCategory(ErrorCategory.FORMULA);
        error.setSeverity("error");
        error.setTitle("Формула з порушеною послідовністю (наприклад 1.3 замість 1.2)");
        error.setParagraphText(paragraphText);
        error.setFound(Set.of("(" + found + ")"));
        error.setExpected("(" + expected + ")");
        return error;
    }

    private static FormatError buildFormatError(String paragraphText, String found, String expected) {
        FormatError error = new FormatError();
        error.setId("err_formula_format");
        error.setCategory(ErrorCategory.FORMULA);
        error.setSeverity("error");
        error.setTitle("Формула з неправильним форматом номера (наприклад 1-1)");
        error.setParagraphText(paragraphText);
        error.setFound(Set.of("(" + found + ")"));
        error.setExpected("(" + expected + ")");
        return error;
    }

    private static FormatError buildException(Exception e) {
        FormatError error = new FormatError();
        error.setId("err_000");
        error.setCategory(ErrorCategory.FILE);
        error.setSeverity("error");
        error.setTitle("Помилка читання файлу: " + e.getMessage());
        error.setParagraphText("");
        return error;
    }

    private static String displayText(XWPFParagraph paragraph) {
        String text = paragraph.getText();
        return (text == null || text.trim().isEmpty()) ? "[Порожній рядок]" : text.trim();
    }
}
