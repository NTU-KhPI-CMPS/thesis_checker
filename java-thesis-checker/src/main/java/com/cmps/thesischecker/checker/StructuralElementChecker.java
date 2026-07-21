package com.cmps.thesischecker.checker;

import com.cmps.thesischecker.model.DocumentHeader;
import com.cmps.thesischecker.model.ErrorCategory;
import com.cmps.thesischecker.model.FormatError;
import com.cmps.thesischecker.requirements.RequirementsHolder;
import com.cmps.thesischecker.utils.MainContentUtils;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Checks that the mandatory structural elements of the thesis
 * ("ЗМІСТ", "ВСТУП", "ВИСНОВКИ", "СПИСОК ДЖЕРЕЛ ІНФОРМАЦІЇ", ...) appear
 * in the expected order and that each of them starts on a new page.
 * <p>
 * Elements marked as not required in {@link RequirementsHolder#getOrderStructuredElements()}
 * (currently "РЕФЕРАТ", "ПЕРЕЛІК ПОЗНАК ТА СКОРОЧЕНЬ", "ДОДАТОК") are only checked
 * if they are actually present in the document.
 */
public class StructuralElementChecker implements Checker {

    private static final String APPENDIX_TITLE = "ДОДАТОК";

    /**
     * Returns the error category produced by this checker.
     *
     * @return {@link ErrorCategory#STRUCTURAL_ELEMENT_LOCATION} indicating that this error
     *         is related to the location of structural elements in the document
     */
    @Override
    public ErrorCategory getErrorCategory() {
        return ErrorCategory.STRUCTURAL_ELEMENT_LOCATION;
    }

    /**
     * Checks the document for structural element presence, order,
     * and page placement.
     *
     * @param filePath path to the document
     * @return list of detected format errors
     */
    @Override
    public List<FormatError> check(String filePath) {
        List<FormatError> allErrors = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(filePath);
             XWPFDocument doc = new XWPFDocument(fis)) {

            List<XWPFParagraph> paragraphs = MainContentUtils.getMainContentParagraphs(doc);
            List<DocumentHeader> expectedHeaders = RequirementsHolder.getOrderStructuredElements();

            List<FoundHeading> foundHeadings = collectHeadings(paragraphs, expectedHeaders);

            allErrors.addAll(checkMissingRequired(foundHeadings, expectedHeaders));
            allErrors.addAll(checkOrder(foundHeadings, expectedHeaders));
            allErrors.addAll(checkStartsOnNewPage(foundHeadings));
            allErrors.addAll(checkHeadingFormatting(paragraphs, foundHeadings));
        } catch (Exception e) {
            allErrors.add(buildException(e));
        }

        return allErrors;
    }

    /**
     * A structural heading found in the document, together with its position
     * and whether it starts on a new page.
     * @param header the matched structural header
     * @param paragraphText the text of the paragraph in which the header was found
     * @param index the index of the paragraph in the document
     * @param startsNewPage whether the heading starts on a new page
     */
    private record FoundHeading(DocumentHeader header, String paragraphText, int index, boolean startsNewPage) {
    }

    /**
     * Collects structural headings found in the document.
     *
     * @param paragraphs document paragraphs
     * @param expectedHeaders expected structural elements
     * @return list of found headings
     */
    private List<FoundHeading> collectHeadings(List<XWPFParagraph> paragraphs, List<DocumentHeader> expectedHeaders) {
        List<FoundHeading> found = new ArrayList<>();

        for (int i = 0; i < paragraphs.size(); i++) {
            XWPFParagraph paragraph = paragraphs.get(i);
            String text = paragraph.getText().trim();
            if (text.isEmpty()) {
                continue;
            }

            DocumentHeader matched = matchHeader(text, expectedHeaders);
            if (matched != null) {
                found.add(new FoundHeading(matched, text, i, startsNewPage(paragraphs, i)));
            }
        }

        return found;
    }

    /**
     * Matches paragraph text to a structural heading, stripping any trailing dots
     * or leading numbers.
     *
     * @param text paragraph text
     * @param expectedHeaders expected structural elements
     * @return matched header or {@code null}
     */
    private DocumentHeader matchHeader(String text, List<DocumentHeader> expectedHeaders) {
        String cleanText = text.trim();

        String textWithoutNumber = cleanText.replaceFirst("^\\s*\\d+([.\\s)]+\\d*)*[.\\s)]*", "").trim();
        String upperText = textWithoutNumber.toUpperCase(Locale.ROOT);

        if (upperText.endsWith(".")) {
            upperText = upperText.substring(0, upperText.length() - 1).trim();
        }

        for (DocumentHeader header : expectedHeaders) {
            String title = header.getTitle();
            if (title.equals(APPENDIX_TITLE)) {
                if (upperText.equals(title) || upperText.startsWith(title + " ")) {
                    return header;
                }
            } else if (upperText.equals(title)) {
                return header;
            }
        }

        return null;
    }

    /**
     * Checks detailed text formatting of the structural headings (uppercase, bold,
     * no trailing dot, no underline, and no numbering).
     *
     * @param paragraphs document paragraphs
     * @param found found structural headings
     * @return list of detected formatting errors
     */
    private List<FormatError> checkHeadingFormatting(List<XWPFParagraph> paragraphs, List<FoundHeading> found) {
        List<FormatError> errors = new ArrayList<>();

        for (FoundHeading heading : found) {
            XWPFParagraph paragraph = paragraphs.get(heading.index());
            String text = heading.paragraphText().trim();

            if (!text.equals(text.toUpperCase(Locale.ROOT))) {
                errors.add(buildUppercaseError(heading));
            }

            if (text.endsWith(".")) {
                errors.add(buildTrailingDotError(heading));
            }

            if (!isBold(paragraph)) {
                errors.add(buildBoldError(heading));
            }

            if (isUnderlined(paragraph)) {
                errors.add(buildUnderlineError(heading));
            }

            if (isItalic(paragraph)) {
                errors.add(buildItalicError(heading));
            }

            if (isNumbered(paragraph, text)) {
                errors.add(buildNumberedError(heading));
            }

            if(!isCentred(paragraph)){
                errors.add(buildCenteredError(heading));
            }
        }

        return errors;
    }

    /**
     * Checks if the paragraph text is set to bold either via direct run formatting
     * or inherited from paragraph styles.
     *
     * @param paragraph paragraph to inspect
     * @return {@code true} if bold
     */
    private boolean isBold(XWPFParagraph paragraph) {
        for (XWPFRun run : paragraph.getRuns()) {
            if (run.getText(0) != null && !run.getText(0).trim().isEmpty()) {
                if (run.isBold()) {
                    return true;
                }
            }
        }

        if (paragraph.getDocument() != null && paragraph.getStyleID() != null) {
            XWPFStyles styles = paragraph.getDocument().getStyles();
            if (styles != null) {
                String styleId = paragraph.getStyleID();
                while (styleId != null) {
                    XWPFStyle style = styles.getStyle(styleId);
                    if (style != null && style.getCTStyle() != null) {
                        CTRPr rPr = style.getCTStyle().getRPr();
                        if (rPr != null && !rPr.getBList().isEmpty()) {
                            CTOnOff b = rPr.getBList().getFirst();
                            return isOnOffEnabled(b);
                        }
                        if (style.getCTStyle().isSetBasedOn() && style.getCTStyle().getBasedOn() != null) {
                            styleId = style.getCTStyle().getBasedOn().getVal();
                        } else {
                            break;
                        }
                    } else {
                        break;
                    }
                }
            }
        }

        return false;
    }

    /**
     * Checks if the paragraph text is set to italic either via direct run formatting
     * or inherited from paragraph styles.
     *
     * @param paragraph paragraph to inspect
     * @return {@code true} if italic
     */
    private boolean isItalic(XWPFParagraph paragraph) {
        for (XWPFRun run : paragraph.getRuns()) {
            if (run.getText(0) != null && !run.getText(0).trim().isEmpty()) {
                if (run.isItalic()) {
                    return true;
                }
            }
        }

        if (paragraph.getDocument() != null && paragraph.getStyleID() != null) {
            XWPFStyles styles = paragraph.getDocument().getStyles();
            if (styles != null) {
                String styleId = paragraph.getStyleID();
                while (styleId != null) {
                    XWPFStyle style = styles.getStyle(styleId);
                    if (style != null && style.getCTStyle() != null) {
                        CTRPr rPr = style.getCTStyle().getRPr();
                        if (rPr != null && !rPr.getIList().isEmpty()) {
                            CTOnOff i = rPr.getIList().getFirst();
                            return isOnOffEnabled(i);
                        }
                        if (style.getCTStyle().isSetBasedOn() && style.getCTStyle().getBasedOn() != null) {
                            styleId = style.getCTStyle().getBasedOn().getVal();
                        } else {
                            break;
                        }
                    } else {
                        break;
                    }
                }
            }
        }

        return false;
    }

    /**
     * Checks if any run in the paragraph or its style is underlined.
     *
     * @param paragraph paragraph to inspect
     * @return {@code true} if underlined
     */
    private boolean isUnderlined(XWPFParagraph paragraph) {
        for (XWPFRun run : paragraph.getRuns()) {
            if (run.getUnderline() != null && run.getUnderline() != UnderlinePatterns.NONE) {
                return true;
            }
        }

        if (paragraph.getDocument() != null && paragraph.getStyleID() != null) {
            XWPFStyles styles = paragraph.getDocument().getStyles();
            if (styles != null) {
                String styleId = paragraph.getStyleID();
                while (styleId != null) {
                    XWPFStyle style = styles.getStyle(styleId);
                    if (style != null && style.getCTStyle() != null) {
                        CTRPr rPr = style.getCTStyle().getRPr();
                        if (rPr != null && !rPr.getUList().isEmpty()) {
                            var u = rPr.getUList().getFirst();
                            if (u.getVal() != null && u.getVal() != STUnderline.NONE) {
                                return true;
                            }
                        }
                        if (style.getCTStyle().isSetBasedOn() && style.getCTStyle().getBasedOn() != null) {
                            styleId = style.getCTStyle().getBasedOn().getVal();
                        } else {
                            break;
                        }
                    } else {
                        break;
                    }
                }
            }
        }

        return false;
    }

    /**
     * Checks whether the paragraph has list numbering attached (XML)
     * or manually typed number at the beginning (e.g. "1 ВСТУП", "1. ВСТУП").
     *
     * @param paragraph paragraph to inspect
     * @param text paragraph text
     * @return {@code true} if numbered
     */
    private boolean isNumbered(XWPFParagraph paragraph, String text) {
        if (paragraph.getNumID() != null) {
            return true;
        }
        return text != null && text.trim().matches("^\\s*\\d+([.\\s)]+\\d*)*[.\\s)].*");
    }

    /**
     * Checks whether the paragraph is centered, either via direct formatting or inherited from styles.
     *
     * @param paragraph paragraph to inspect
     * @return {@code true} if centered
     */
    private boolean isCentred(XWPFParagraph paragraph) {
        String alignment = AlignmentChecker.getAlignmentFromPPr(paragraph.getCTP().getPPr());

        if (alignment == null) {
            alignment = AlignmentChecker.getAlignmentFromStyles(paragraph);
        }

        return alignment != null && alignment.equalsIgnoreCase(RequirementsHolder.getHeadingAlignment());
    }

    /**
     * Checks that all required structural elements are present.
     *
     * @param found found structural headings
     * @param expectedHeaders expected structural elements
     * @return list of errors
     */
    private List<FormatError> checkMissingRequired(List<FoundHeading> found, List<DocumentHeader> expectedHeaders) {
        List<FormatError> errors = new ArrayList<>();

        Set<String> foundTitles = new LinkedHashSet<>();
        for (FoundHeading heading : found) {
            foundTitles.add(heading.header().getTitle());
        }

        for (DocumentHeader header : expectedHeaders) {
            if (header.isRequired() && !foundTitles.contains(header.getTitle())) {
                errors.add(buildMissingError(header));
            }
        }

        return errors;
    }

    /**
     * Checks the order of structural elements.
     *
     * @param found found structural headings
     * @param expectedHeaders expected structural elements
     * @return list of errors
     */
    private List<FormatError> checkOrder(List<FoundHeading> found, List<DocumentHeader> expectedHeaders) {
        List<String> expectedOrder = getExpectedOrder(found, expectedHeaders);

        // Actual order, collapsing consecutive repeats (e.g. several "ДОДАТОК Х" in a row
        // still count as one element for ordering purposes).
        List<String> actualOrder = new ArrayList<>();
        String previousTitle = null;
        for (FoundHeading heading : found) {
            String title = heading.header().getTitle();
            if (!title.equals(previousTitle)) {
                actualOrder.add(title);
            }
            previousTitle = title;
        }

        if (!expectedOrder.equals(actualOrder)) {
            return List.of(buildOrderError(expectedOrder, actualOrder));
        }

        return List.of();
    }

    /**
     * Builds the expected order for the elements present
     * in the document.
     *
     * @param found found structural headings
     * @param expectedHeaders expected structural elements
     * @return expected element order
     */
    private static List<String> getExpectedOrder(List<FoundHeading> found, List<DocumentHeader> expectedHeaders) {
        Set<String> foundTitles = new LinkedHashSet<>();
        for (FoundHeading heading : found) {
            foundTitles.add(heading.header().getTitle());
        }

        // Expected order, limited to elements that are actually present
        // (so a missing optional element doesn't trigger a false "wrong order").
        List<String> expectedOrder = new ArrayList<>();
        for (DocumentHeader header : expectedHeaders) {
            if (foundTitles.contains(header.getTitle())) {
                expectedOrder.add(header.getTitle());
            }
        }
        return expectedOrder;
    }

    /**
     * Checks that structural elements start on a new page.
     *
     * @param found found structural headings
     * @return list of warnings
     */
    private List<FormatError> checkStartsOnNewPage(List<FoundHeading> found) {
        List<FormatError> errors = new ArrayList<>();

        for (FoundHeading heading : found) {
            if (!heading.startsNewPage()) {
                errors.add(buildNewPageWarning(heading));
            }
        }

        return errors;
    }

    /**
     * Determines whether the specified heading starts on a new page.
     *
     * @param paragraphs document paragraphs
     * @param headingIndex index of the heading paragraph
     * @return {@code true} if the heading starts on a new page
     */
    private boolean startsNewPage(List<XWPFParagraph> paragraphs, int headingIndex) {
        if (headingIndex == 0) {
            return true;
        }

        XWPFParagraph heading = paragraphs.get(headingIndex);

        if (hasPageBreakBeforeProperty(heading)) {
            return true;
        }

        if (hasManualPageBreak(heading)) {
            return true;
        }

        final int maxBlankLookback = 3;
        int blanksSeen = 0;

        for (int i = headingIndex - 1; i >= 0 && blanksSeen <= maxBlankLookback; i--) {
            XWPFParagraph previous = paragraphs.get(i);

            if (hasManualPageBreak(previous)) {
                return true;
            }

            if (!previous.getText().trim().isEmpty()) {
                break;
            }

            blanksSeen++;
        }

        return false;
    }

    /**
     * Checks whether the paragraph has the pageBreakBefore property.
     *
     * @param paragraph paragraph to inspect
     * @return {@code true} if the property is enabled
     */
    private boolean hasPageBreakBeforeProperty(XWPFParagraph paragraph) {
        CTPPr pPr = paragraph.getCTP().getPPr();
        if (pPr == null || !pPr.isSetPageBreakBefore()) {
            return false;
        }

        return isOnOffEnabled(pPr.getPageBreakBefore());
    }

    /**
     * Determines whether an OOXML on/off value is enabled.
     *
     * @param onOff OOXML on/off value
     * @return {@code true} if enabled
     */
    private boolean isOnOffEnabled(CTOnOff onOff) {
        if (onOff == null) {
            return false;
        }
        // Per OOXML, the mere presence of the element without an explicit "val"
        // means "enabled".
        if (!onOff.isSetVal()) {
            return true;
        }

        // ST_OnOff is a union type (true/false/1/0/on/off), so depending on the
        // POI version getVal() may come back as Boolean or as some other object -
        // comparing the string form covers all of them reliably.
        Object val = onOff.getVal();
        if (val instanceof Boolean bool) {
            return bool;
        }

        String normalized = val.toString().trim().toLowerCase(Locale.ROOT);
        return normalized.equals("true") || normalized.equals("1") || normalized.equals("on");
    }

    /**
     * Checks whether the paragraph contains a manual page break.
     *
     * @param paragraph paragraph to inspect
     * @return {@code true} if a page break is found
     */
    private boolean hasManualPageBreak(XWPFParagraph paragraph) {
        for (XWPFRun run : paragraph.getRuns()) {
            for (CTBr br : run.getCTR().getBrList()) {
                if (br.getType() == STBrType.PAGE) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Creates an error for a missing required structural element.
     *
     * @param header missing structural element
     * @return format error
     */
    private FormatError buildMissingError(DocumentHeader header) {
        FormatError error = new FormatError();
        error.setId("err_structural_element_missing");
        error.setCategory(ErrorCategory.STRUCTURAL_ELEMENT_LOCATION);
        error.setSeverity("error");
        error.setTitle("Відсутній обов'язковий структурний елемент \"" + header.getTitle() + "\"");
        error.setParagraphText("");
        error.setExpected(header.getTitle());
        return error;
    }

    /**
     * Creates an error for an invalid structural element order.
     *
     * @param expectedOrder expected element order
     * @param actualOrder actual element order
     * @return format error
     */
    private FormatError buildOrderError(List<String> expectedOrder, List<String> actualOrder) {
        FormatError error = new FormatError();
        error.setId("err_structural_element_order");
        error.setCategory(ErrorCategory.STRUCTURAL_ELEMENT_LOCATION);
        error.setSeverity("error");
        error.setTitle("Порушено порядок структурних елементів документа");
        error.setParagraphText("");
        error.setFound(new LinkedHashSet<>(actualOrder));
        error.setExpected(String.join(" -> ", expectedOrder));
        return error;
    }

    /**
     * Creates a warning for a structural element that does not
     * start on a new page.
     *
     * @param heading structural heading
     * @return format warning
     */
    private FormatError buildNewPageWarning(FoundHeading heading) {
        FormatError error = new FormatError();
        error.setId("warn_structural_element_new_page");
        error.setCategory(ErrorCategory.STRUCTURAL_ELEMENT_LOCATION);
        error.setSeverity("warning");
        error.setTitle("Структурний елемент \"" + heading.header().getTitle() + "\" не починається з нової сторінки");
        error.setParagraphText(heading.paragraphText());
        error.setExpected("Розрив сторінки перед елементом");
        return error;
    }

    /**
     * Creates an error when a structural element is not in uppercase.
     *
     * @param heading structural heading
     * @return format error
     */
    private FormatError buildUppercaseError(FoundHeading heading) {
        FormatError error = new FormatError();
        error.setId("err_structural_element_uppercase");
        error.setCategory(ErrorCategory.STRUCTURAL_ELEMENT_LOCATION);
        error.setSeverity("error");
        error.setTitle("Заголовок структурного елемента має бути великими літерами");
        error.setParagraphText(heading.paragraphText());
        error.setExpected(heading.paragraphText().toUpperCase(Locale.ROOT));
        return error;
    }

    /**
     * Creates an error when a structural element ends with a dot.
     *
     * @param heading structural heading
     * @return format error
     */
    private FormatError buildTrailingDotError(FoundHeading heading) {
        FormatError error = new FormatError();
        error.setId("err_structural_element_trailing_dot");
        error.setCategory(ErrorCategory.STRUCTURAL_ELEMENT_LOCATION);
        error.setSeverity("error");
        error.setTitle("Заголовок структурного елемента не повинен містити крапку в кінці");
        error.setParagraphText(heading.paragraphText());
        error.setExpected(heading.paragraphText().substring(0, heading.paragraphText().length() - 1));
        return error;
    }

    /**
     * Creates an error when a structural element is not bold.
     *
     * @param heading structural heading
     * @return format error
     */
    private FormatError buildBoldError(FoundHeading heading) {
        FormatError error = new FormatError();
        error.setId("err_structural_element_bold");
        error.setCategory(ErrorCategory.STRUCTURAL_ELEMENT_LOCATION);
        error.setSeverity("error");
        error.setTitle("Заголовок структурного елемента має бути напівжирним");
        error.setParagraphText(heading.paragraphText());
        error.setExpected("Напівжирний шрифт");
        return error;
    }

    /**
     * Creates an error when a structural element is underlined.
     *
     * @param heading structural heading
     * @return format error
     */
    private FormatError buildUnderlineError(FoundHeading heading) {
        FormatError error = new FormatError();
        error.setId("err_structural_element_underline");
        error.setCategory(ErrorCategory.STRUCTURAL_ELEMENT_LOCATION);
        error.setSeverity("error");
        error.setTitle("Заголовок структурного елемента не повинен бути підкресленим");
        error.setParagraphText(heading.paragraphText());
        error.setExpected("Без підкреслення");
        return error;
    }

    /**
     * Creates an error when a structural element is numbered.
     *
     * @param heading structural heading
     * @return format error
     */
    private FormatError buildNumberedError(FoundHeading heading) {
        FormatError error = new FormatError();
        error.setId("err_structural_element_numbered");
        error.setCategory(ErrorCategory.STRUCTURAL_ELEMENT_LOCATION);
        error.setSeverity("error");
        error.setTitle("Заголовок структурного елемента не повинен бути нумерованим");
        error.setParagraphText(heading.paragraphText());
        error.setExpected("Без нумерації");
        return error;
    }

    /**
     * Creates an error when a structural element is italicized.
     *
     * @param heading structural heading
     * @return format error
     */
    private FormatError buildItalicError(FoundHeading heading) {
        FormatError error = new FormatError();
        error.setId("err_structural_element_italic");
        error.setCategory(ErrorCategory.STRUCTURAL_ELEMENT_LOCATION);
        error.setSeverity("error");
        error.setTitle("Заголовок структурного елемента не повинен бути курсивом");
        error.setParagraphText(heading.paragraphText());
        error.setExpected("Без курсиву");
        return error;
    }

    private FormatError buildCenteredError(FoundHeading heading) {
        FormatError error = new FormatError();
        error.setId("err_structural_element_not_centered");
        error.setCategory(ErrorCategory.STRUCTURAL_ELEMENT_LOCATION);
        error.setSeverity("error");
        error.setTitle("Заголовок структурного елемента повинен бути вирівняний по центру");
        error.setParagraphText(heading.paragraphText());
        error.setExpected("Вирівнювання по лівому краю");
        return error;
    }

    /**
     * Creates an error describing a file processing exception.
     *
     * @param e thrown exception
     * @return format error
     */
    private FormatError buildException(Exception e) {
        FormatError error = new FormatError();
        error.setId("err_000");
        error.setCategory(ErrorCategory.FILE);
        error.setSeverity("error");
        error.setTitle("Помилка відкриття файлу: " + e.getMessage());
        error.setParagraphText("");
        return error;
    }
}
