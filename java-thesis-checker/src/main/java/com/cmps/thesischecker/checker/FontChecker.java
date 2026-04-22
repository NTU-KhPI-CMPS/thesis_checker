package com.cmps.thesischecker.checker;

import com.cmps.thesischecker.model.ErrorCategory;
import com.cmps.thesischecker.model.FormatError;
import com.cmps.thesischecker.requirements.RequirementsHolder;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FontChecker implements Checker {

    public FontChecker() {
    }

    @Override
    public List<FormatError> check(String filePath) {
        List<FormatError> allErrors = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(filePath);
             XWPFDocument doc = new XWPFDocument(fis)) {
            for (XWPFParagraph paragraph : doc.getParagraphs()) {
                String paragraphText = paragraph.getText().trim();
                if (paragraphText.isEmpty()) continue;
                Set<String> incorrectFonts = validate(paragraph);
                if (!incorrectFonts.isEmpty()) {
                    allErrors.add(buildFontNameFormatError("err_font", "Невірні шрифти у параграфі", paragraphText, incorrectFonts));
                }
            }

            for (XWPFHeader header : doc.getHeaderList()) {
                for (XWPFParagraph paragraph : header.getParagraphs()) {
                    String paragraphText = paragraph.getText().trim();
                    if (paragraphText.isEmpty()) continue;
                    Set<String> incorrectFonts = validate(paragraph);
                    if (!incorrectFonts.isEmpty()) {
                        allErrors.add(buildFontNameFormatError("err_font_header", "Невірні шрифти у заголовку", paragraphText, incorrectFonts));
                    }
                }
            }

            for (XWPFFooter footer : doc.getFooterList()) {
                for (XWPFParagraph paragraph : footer.getParagraphs()) {
                    String paragraphText = paragraph.getText().trim();
                    if (paragraphText.isEmpty()) continue;
                    Set<String> incorrectFonts = validate(paragraph);
                    if (!incorrectFonts.isEmpty()) {
                        allErrors.add(buildFontNameFormatError("err_font_footer", "Невірні шрифти у підвалі", paragraphText, incorrectFonts));
                    }
                }
            }

            for (XWPFTable table : doc.getTables()) {
                for (XWPFTableRow row : table.getRows()) {
                    for (XWPFTableCell cell : row.getTableCells()) {
                        for (XWPFParagraph paragraph : cell.getParagraphs()) {
                            String paragraphText = paragraph.getText().trim();
                            if (paragraphText.isEmpty()) continue;
                            Set<String> incorrectFonts = validate(paragraph);
                            if (!incorrectFonts.isEmpty()) {
                                allErrors.add(buildFontNameFormatError("err_font_table", "Невірні шрифти у таблиці", paragraphText, incorrectFonts));
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

    private static FormatError buildFontNameFormatError(String id, String title, String text, Set<String> incorrectFonts) {
        FormatError err = new FormatError();
        err.setId(id);
        err.setCategory(ErrorCategory.FONT_NAME);
        err.setSeverity("error");
        err.setTitle(title);
        err.setParagraphText(text);
        err.setFound(incorrectFonts);
        err.setExpected(RequirementsHolder.getFont());

        return err;
    }

    private Set<String> validate(XWPFParagraph paragraph) {
        Set<String> incorrectFonts = new HashSet<>();
        String expectedFont = RequirementsHolder.getFont();
        XWPFDocument document = paragraph.getDocument();
        List<XWPFRun> runs = paragraph.getRuns();
        if (runs.isEmpty()) return incorrectFonts;

        for (XWPFRun run : runs) {
            String fragmentText = run.getText(0);
            if (fragmentText == null) continue;

            String effectiveFont = getEffectiveFont(run, paragraph, document);
            if (effectiveFont != null && !effectiveFont.equalsIgnoreCase(expectedFont)) {
                incorrectFonts.add(effectiveFont);
            }
        }
        return incorrectFonts;
    }

    private String getEffectiveFont(XWPFRun run, XWPFParagraph paragraph, XWPFDocument document) {
        String font = run.getFontFamily();

        if (font != null) {
            return font;
        }

        Object parent = run.getParent();
        if (!(parent instanceof XWPFParagraph)) {
            return getFontFromParagraphStyle(document, "Normal");
        }
        XWPFParagraph paragraphFromParent = (XWPFParagraph) parent;

        String styleId = paragraphFromParent.getStyleID();

        if (styleId == null) {
            return getFontFromParagraphStyle(document, "Normal");
        }
        return getFontFromParagraphStyle(document, styleId);
    }

    private String getFontFromParagraphStyle(XWPFDocument document, String styleId) {
        XWPFStyle style = document.getStyles().getStyle(styleId);

        if (style == null) {
            return RequirementsHolder.getFont();
        }

        var ctStyle = style.getCTStyle();
        var rPr = ctStyle.getRPr();

        if (rPr == null) {
            return RequirementsHolder.getFont();
        }

        var rFontsList = rPr.getRFontsList();
        if (!rFontsList.isEmpty()) {
            var rFonts = rFontsList.getFirst();
            String fontName = rFonts.getAscii();

            if (fontName != null) {
                return fontName;
            }
        }

        return RequirementsHolder.getFont();
    }
}
