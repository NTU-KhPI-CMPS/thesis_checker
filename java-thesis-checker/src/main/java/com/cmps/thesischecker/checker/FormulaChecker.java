package com.cmps.thesischecker.checker;

import com.cmps.thesischecker.model.ErrorCategory;
import com.cmps.thesischecker.model.FormatError;
import com.cmps.thesischecker.requirements.RequirementsHolder;
import com.cmps.thesischecker.utils.FormulaUtils;
import com.cmps.thesischecker.utils.MainContentUtils;
import com.cmps.thesischecker.utils.StyleUtils;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFStyle;
import org.apache.poi.xwpf.usermodel.XWPFStyles;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRPr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.FileInputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
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
    private static final Pattern LEADING_NUMBER_PATTERN = Pattern.compile("^[0-9.]+");
    private static final Pattern FIRST_TOKEN_PATTERN = Pattern.compile("^\\S+");
    private final int expectedFontSize;

    /**
     * Constructs a FormulaChecker and initializes the expected font size parameter.
     */
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

            List<XWPFParagraph> paragraphs = MainContentUtils.getMainContentParagraphs(doc);
            int[] currentChapter = {0};
            int[] expectedNumberInChapter = {1};

            for (int i = 0; i < paragraphs.size(); i++) {
                XWPFParagraph paragraph = paragraphs.get(i);

                if (isHeading1(paragraph)) {
                    updateChapter(paragraph, currentChapter, expectedNumberInChapter);
                    continue;
                }

                if (!FormulaUtils.isFormulaOnlyParagraph(paragraph)) {
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
     * Checks if the specified paragraph uses the Heading1 style.
     *
     * @param paragraph the paragraph to check
     * @return true if the paragraph is a Heading1, false otherwise
     */
    private boolean isHeading1(XWPFParagraph paragraph) {
        String styleId = paragraph.getStyle();
        return styleId != null && (styleId.equalsIgnoreCase("Heading1") || styleId.equalsIgnoreCase("1"));
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

        if (formulaIndex == 0 || !FormulaUtils.isBlankParagraph(paragraphs.get(formulaIndex - 1))) {
            allErrors.add(buildSpacingError("err_formula_spacing_before",
                    "Формула без порожнього рядка перед неї",
                    formulaText,
                    formulaIndex > 0 ? displayText(paragraphs.get(formulaIndex - 1)) : "Початок документу",
                    "Порожній рядок перед формулою"));
        }

        String alignment = new AlignmentChecker().getAlignment(formulaParagraph);
        if (!"CENTER".equalsIgnoreCase(alignment) && !"RIGHT".equalsIgnoreCase(alignment)) {
            allErrors.add(buildAlignmentError(formulaText, alignment));
        }

        List<String> formulaXmls = FormulaUtils.getFormulaXmls(formulaParagraph);
        int[] markerCount = {0};
        for (String formulaXml : formulaXmls) {
            checkFormulaXml(formulaXml, formulaText, formulaParagraph, currentChapter, expectedNumberInChapter,
                    markerCount, allErrors);
        }

        if (formulaXmls.size() > 1 || markerCount[0] > 1) {
            allErrors.add(buildOneFormulaPerLineError(formulaText));
        }

        int nextIndex = formulaIndex + 1;
        if (nextIndex >= paragraphs.size()) {
            allErrors.add(buildSpacingError("err_formula_spacing_after",
                    "Формула без порожнього рядка після неї",
                    formulaText,
                    formulaText,
                    "Порожній рядок після формули"));
            return;
        }

        int afterFormulaIndex = nextIndex;

        if (FormulaUtils.isBlankParagraph(paragraphs.get(nextIndex))) {
            int cursor = nextIndex;
            while (cursor < paragraphs.size() && FormulaUtils.isBlankParagraph(paragraphs.get(cursor))) {
                cursor++;
            }

            if (cursor >= paragraphs.size() || !FormulaUtils.isNotationParagraph(paragraphs.get(cursor))) {
                return;
            }

            allErrors.add(buildSpacingError("err_formula_spacing_before_notation",
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
                allErrors.add(buildSpacingError("err_formula_notation_spacing",
                        "Відсутній порожній рядок після пояснення «де»",
                        formulaText,
                        formulaText,
                        "Порожній рядок після блоку пояснень"));
            }
            return;
        }

        if (afterFormulaIndex == nextIndex) {
            allErrors.add(buildSpacingError("err_formula_spacing_after",
                    "Формула без порожнього рядка після неї",
                    formulaText,
                    formulaText,
                    "Порожній рядок після формули"));
        }
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
     * Parses a single formula's XML, checks the font size of every fragment, finds every
     * numbering marker ("#", possibly fused with adjacent text like "=m#") and validates
     * the number that follows it.
     *
     * @param formulaXml               raw XML of the OMath element
     * @param paragraphText            text of the paragraph containing the formula
     * @param formulaParagraph         the paragraph that contains the formula, used to resolve
     *                                 the effective font size (via its style) when a run doesn't
     *                                 declare its own size
     * @param currentChapter           the current chapter number, from the last Heading1
     * @param expectedNumberInChapter  mutable holder for the expected next formula number
     * @param markerCount              mutable counter of numbering markers found, used to
     *                                 detect multiple formulas sharing one paragraph
     * @param allErrors                accumulator for found errors
     */
    private void checkFormulaXml(String formulaXml, String paragraphText, XWPFParagraph formulaParagraph,
                                 int currentChapter, int[] expectedNumberInChapter, int[] markerCount,
                                 List<FormatError> allErrors) {
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
                checkRunSize(run, text, paragraphText, formulaParagraph, allErrors);
            }

            NodeList texts = document.getElementsByTagName("m:t");
            StringBuilder flatBuilder = new StringBuilder();
            for (int i = 0; i < texts.getLength(); i++) {
                String t = texts.item(i).getTextContent();
                if (t != null) flatBuilder.append(t);
            }
            String flatText = flatBuilder.toString();

            int searchFrom = 0;
            while (true) {
                int hashPos = flatText.indexOf('#', searchFrom);
                if (hashPos < 0) break;

                markerCount[0]++;

                int nextHashPos = flatText.indexOf('#', hashPos + 1);
                int segmentEnd = nextHashPos >= 0 ? nextHashPos : flatText.length();
                String segment = flatText.substring(hashPos + 1, segmentEnd).replaceFirst("^\\s+", "");

                String numberText = extractFormulaNumberCandidate(segment);
                if (!numberText.isEmpty()) {
                    validateFormulaNumber(numberText, paragraphText, currentChapter, expectedNumberInChapter, allErrors);
                }

                searchFrom = hashPos + 1;
            }
        } catch (Exception e) {
            allErrors.add(buildException(e));
        }
    }

    /**
     * Checks if the run size inside the formula matches the expected document font size.
     * <p>
     * The size is resolved in steps: first the run's own explicit "w:sz" property is used;
     * if the run doesn't declare one, the size falls back to the formula paragraph's applied
     * style, and if that style doesn't declare a size either, recursively up the style's
     * "basedOn" inheritance chain. If no size is found anywhere, Word's standard 12pt default
     * is used.
     *
     * @param run              the XML Element containing run properties
     * @param text             the text fragment inside the run
     * @param paragraphText    the text of the entire paragraph
     * @param formulaParagraph the paragraph that contains the formula, used to resolve the
     *                         font size from its style when the run doesn't set one
     * @param allErrors        accumulator for found errors
     */
    private void checkRunSize(Element run, String text, String paragraphText, XWPFParagraph formulaParagraph,
                              List<FormatError> allErrors) {
        NodeList sizes = run.getElementsByTagName("w:sz");

        double sizePt;

        if (sizes.getLength() > 0) {
            Element sz = (Element) sizes.item(0);
            int raw = Integer.parseInt(sz.getAttribute("w:val"));
            sizePt = raw / 2.0;
        } else {
            sizePt = getEffectiveParagraphFontSize(formulaParagraph);
        }

        if (sizePt != expectedFontSize) {
            allErrors.add(buildFontSizeError(paragraphText, text, sizePt));
        }
    }

    /**
     * Resolves the font size that applies to a paragraph when a formula run doesn't declare
     * its own size: first the paragraph's own style is checked, then - if that style doesn't
     * declare a size either - the lookup walks recursively up the style's "basedOn" chain.
     *
     * @param paragraph the paragraph to resolve the effective font size for
     * @return the resolved font size in points, or 12.0 (Word's default) if none is found
     */
    private double getEffectiveParagraphFontSize(XWPFParagraph paragraph) {
        XWPFDocument document = paragraph.getDocument();
        XWPFStyles styles = document.getStyles();
        String styleId = paragraph.getStyleID();

        if (styleId == null) {
            styleId = StyleUtils.getNormalStyleId(styles);
        }

        return getFontSizeFromParagraphStyle(document, styleId);
    }

    /**
     * Looks up the font size (in points) declared directly on a style, recursively walking
     * up the "basedOn" style inheritance chain when the style itself doesn't declare a size.
     *
     * @param document the document the style belongs to
     * @param styleId  the ID of the style to inspect
     * @return the resolved font size in points, or 12.0 (Word's default) if none is found
     *         anywhere in the chain
     */
    private double getFontSizeFromParagraphStyle(XWPFDocument document, String styleId) {
        XWPFStyle style = document.getStyles().getStyle(styleId);

        if (style == null) {
            return 12.0;
        }

        var ctStyle = style.getCTStyle();
        var sizeList = Optional
                .ofNullable(ctStyle.getRPr())
                .map(CTRPr::getSzList)
                .orElse(Collections.emptyList());

        if (!sizeList.isEmpty()) {
            var sz = sizeList.getFirst();
            if (sz.getVal() != null) {
                int size = Integer.parseInt(sz.getVal().toString());
                return size / 2.0;
            }
        }

        if (ctStyle.isSetBasedOn() && ctStyle.getBasedOn() != null) {
            String baseStyle = ctStyle.getBasedOn().getVal();
            return getFontSizeFromParagraphStyle(document, baseStyle);
        }

        return 12.0;
    }

    /**
     * Extracts a number candidate from the text right after a "#" marker (already bounded
     * to not cross into the next marker). Prefers the leading run of digits/dots (handles
     * the case where legitimate formula content, e.g. the next formula's variables in an
     * m:eqArr, follows right after with no separator). If the segment doesn't start with a
     * digit at all (a clearly malformed number, e.g. "(1]3)"), falls back to the first
     * whitespace-delimited token so the error is still reported instead of silently skipped.
     *
     * @param segment the text between a marker and the next marker (or end of formula)
     * @return the best-effort number candidate, or empty string if the segment is empty
     */
    private String extractFormulaNumberCandidate(String segment) {
        if (segment.isEmpty()) {
            return "";
        }

        Matcher numeric = LEADING_NUMBER_PATTERN.matcher(segment);
        if (numeric.lookingAt()) {
            return numeric.group();
        }

        Matcher token = FIRST_TOKEN_PATTERN.matcher(segment);
        return token.find() ? token.group() : "";
    }

    /**
     * Validates the extracted formula number against the current chapter and expected sequence.
     *
     * @param numberText              the extracted formula number string
     * @param paragraphText           the text of the entire paragraph
     * @param currentChapter           the current chapter number
     * @param expectedNumberInChapter the expected next formula number
     * @param allErrors                accumulator for found errors
     */
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
            return;
        }

        if (numberInChapter != expectedNumberInChapter[0]) {
            allErrors.add(buildSequenceError(paragraphText, numberText, currentChapter + "." + expectedNumberInChapter[0]));
        }

        expectedNumberInChapter[0] = numberInChapter + 1;
    }

    /**
     * Extracts text content from a math run element.
     *
     * @param run the run element
     * @return the text content of the run
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
     * Builds a FormatError object related to spacing issues.
     *
     * @param id            the unique error identifier
     * @param title         the detailed error title
     * @param paragraphText the text content of the paragraph
     * @param found         the incorrect format found in the document
     * @param expected      the expected spacing requirements description
     * @return a constructed FormatError instance
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
     * Builds a FormatError object related to alignment issues.
     *
     * @param paragraphText the text content of the paragraph
     * @param found         the incorrect alignment layout found in the document
     * @return a constructed FormatError instance
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
     * Builds a FormatError object related to multi-formula placement violations.
     *
     * @param paragraphText the text content of the paragraph
     * @return a constructed FormatError instance
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
     * Builds a FormatError object related to font size issues.
     *
     * @param paragraphText the text content of the paragraph
     * @param formula      the specific formula text that has the font size issue
     * @param foundSize     the size measured inside the run
     * @return a constructed FormatError instance
     */
    private FormatError buildFontSizeError(String paragraphText, String formula, double foundSize) {
        FormatError error = new FormatError();
        error.setId("err_formula_font_size");
        error.setCategory(ErrorCategory.FORMULA);
        error.setSeverity("error");
        error.setTitle("Неправильний розмір шрифту у формулі \"" + formula + "\"");
        error.setParagraphText(paragraphText);
        error.setFound(Set.of(foundSize + "pt"));
        error.setExpected(expectedFontSize + "pt");
        return error;
    }

    /**
     * Builds a FormatError object related to chapter mismatch in formula numbering.
     *
     * @param paragraphText  the text content of the paragraph
     * @param found          the erroneous formula numbering found
     * @param currentChapter the current active chapter number
     * @return a constructed FormatError instance
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
     * Builds a FormatError object related to a sequence mismatch.
     *
     * @param paragraphText the text content of the paragraph
     * @param found         the sequence gap numbering found
     * @param expected      the perfect next sequence expected
     * @return a constructed FormatError instance
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
     * Builds a FormatError object related to structurally malformed numbers.
     *
     * @param paragraphText the parent paragraph text
     * @param found         the broken numbering format found
     * @param expected      the expected standard format layout
     * @return a constructed FormatError instance
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
     * Wraps a number in parentheses for display, unless it's already wrapped
     * (a malformed source number can already contain literal "(" / ")").
     *
     * @param text the raw number text
     * @return the text wrapped in a single pair of parentheses
     */
    private static String withParens(String text) {
        String trimmed = text.trim();
        if (trimmed.startsWith("(") && trimmed.endsWith(")")) {
            return trimmed;
        }
        return "(" + trimmed + ")";
    }

    /**
     * Builds a file-level error from processing exceptions.
     *
     * @param e the captured Exception
     * @return a constructed FormatError instance
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
     * Displays formula text for error reporting.
     *
     * @param paragraph the source paragraph
     * @return the extracted string, or a placeholder literal if blank
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
