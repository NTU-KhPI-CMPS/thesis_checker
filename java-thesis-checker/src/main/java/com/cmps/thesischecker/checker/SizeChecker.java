package com.cmps.thesischecker.checker;

import com.cmps.thesischecker.model.ErrorCategory;
import com.cmps.thesischecker.model.FormatError;
import com.cmps.thesischecker.requirements.RequirementsHolder;
import org.apache.poi.xwpf.usermodel.*;
import com.cmps.thesischecker.utils.MainContentUtils;
import com.cmps.thesischecker.utils.StyleUtils;

import java.io.FileInputStream;
import java.util.*;

public class SizeChecker implements Checker {

    private final double expectedSize;

    public SizeChecker() {
        this.expectedSize = Double.parseDouble(RequirementsHolder.getFontSize());
    }

    /**
     * Returns the error category for this instance.
     *
     * @return {@link ErrorCategory#FONT_SIZE} indicating that this error
     *         is related to font size issues
     */
    @Override
    public ErrorCategory getErrorCategory() {
        return ErrorCategory.FONT_SIZE;
    }

    @Override
    public List<FormatError> check(String filePath) {
        List<FormatError> allErrors = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(filePath);
             XWPFDocument doc = new XWPFDocument(fis)) {
            
            for (XWPFParagraph paragraph : MainContentUtils.getMainContentParagraphs(doc)) {
                String paragraphText = paragraph.getText().trim();
                if (paragraphText.isEmpty()) continue;
                Set<String> incorrectSizes = validate(paragraph);

                if (!incorrectSizes.isEmpty()) {
                    allErrors.add(buildFontSizeError("err_font_size", "Неправильний розмір шрифта у параграфі", 
                                                      paragraphText, incorrectSizes, expectedSize));
                }
            }

            for (XWPFHeader header : doc.getHeaderList()) {
                for (XWPFParagraph paragraph : header.getParagraphs()) {
                    String paragraphText = paragraph.getText().trim();
                    if (paragraphText.isEmpty()) continue;
                    Set<String> incorrectSizes = validate(paragraph);
                    if (!incorrectSizes.isEmpty()) {
                        allErrors.add(buildFontSizeError("err_font_size_header", 
                                                          "Неправильний розмір шрифта в верхньому колонтитулі",
                                                          paragraphText, incorrectSizes, expectedSize));
                    }
                }
            }

            for (XWPFFooter footer : doc.getFooterList()) {
                for (XWPFParagraph paragraph : footer.getParagraphs()) {
                    String paragraphText = paragraph.getText().trim();
                    if (paragraphText.isEmpty()) continue;
                    Set<String> incorrectSizes = validate(paragraph);
                    if (!incorrectSizes.isEmpty()) {
                        allErrors.add(buildFontSizeError("err_font_size_footer", 
                                                          "Неправильний розмір шрифта в нижньому колонтитулі",
                                                          paragraphText, incorrectSizes, expectedSize));
                    }
                }
            }

            for (XWPFTable table : MainContentUtils.getMainContentTables(doc)) {
                for (XWPFTableRow row : table.getRows()) {
                    for (XWPFTableCell cell : row.getTableCells()) {
                        for (XWPFParagraph paragraph : cell.getParagraphs()) {
                            String paragraphText = paragraph.getText().trim();
                            if (paragraphText.isEmpty()) continue;
                            Set<String> incorrectSizes = validate(paragraph);
                            if (!incorrectSizes.isEmpty()) {
                                allErrors.add(buildFontSizeError("err_font_size_table", 
                                                                  "Неправильний розмір шрифта у таблиці",
                                                                  paragraphText, incorrectSizes, expectedSize));
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            FormatError err = new FormatError();
            err.setId("err_000");
            err.setCategory(ErrorCategory.FILE);
            err.setSeverity("error");
            err.setTitle("Помилка відкриття файлу: " + e.getMessage());
            err.setParagraphText("");
            allErrors.add(err);
        }
        return allErrors;
    }

    private static FormatError buildFontSizeError(String id, String title, String text, Set<String> incorrectSizes, double expectedSize) {
        FormatError err = new FormatError();
        err.setId(id);
        err.setCategory(ErrorCategory.FONT_SIZE);
        err.setSeverity("error");
        err.setTitle(title);
        err.setParagraphText(text);
        err.setFound(incorrectSizes);
        err.setExpected(expectedSize + "pt");
        return err;
    }

    private Set<String> validate(XWPFParagraph paragraph) {
        Set<String> incorrectSizes = new HashSet<>();
        XWPFDocument document = paragraph.getDocument();
        List<XWPFRun> runs = paragraph.getRuns();
        if (runs.isEmpty()) return incorrectSizes;

        for (XWPFRun run : runs) {
            String fragmentText = run.getText(0);
            if (fragmentText == null) continue;

            Double effectiveSize = getEffectiveFontSize(run, document);
            Double expectedSizeDouble = Double.parseDouble(RequirementsHolder.getFontSize());

            if (!effectiveSize.equals(expectedSizeDouble))
                incorrectSizes.add(String.format("%.1fpt", effectiveSize));
            }

        return incorrectSizes;
    }

    private Double getEffectiveFontSize(XWPFRun run, XWPFDocument document) {
        Double size = run.getFontSizeAsDouble();
        if (size != null && size > 0) {
            return size;
        }

        Object parent = run.getParent();
        if (!(parent instanceof XWPFParagraph paragraphFromParent)) {
            return 12.0; // Default size if the parent is not a paragraph
        }

        String styleId = paragraphFromParent.getStyleID();
        if (styleId == null) {
            return StyleUtils.getFontSizeFromParagraphStyle(document, StyleUtils.getNormalStyleId(document.getStyles()));
        }

        return StyleUtils.getFontSizeFromParagraphStyle(document, styleId);
    }
}
