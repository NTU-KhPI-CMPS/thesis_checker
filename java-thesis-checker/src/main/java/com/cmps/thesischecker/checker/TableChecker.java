package com.cmps.thesischecker.checker;

import com.cmps.thesischecker.model.ErrorCategory;
import com.cmps.thesischecker.model.FormatError;
import com.cmps.thesischecker.requirements.RequirementsHolder;
import com.cmps.thesischecker.utils.MainContentUtils;
import com.cmps.thesischecker.utils.ParagraphUtils;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;

import java.io.FileInputStream;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TableChecker implements Checker {

    private static final Set<Double> ALLOWED_SIZES = Set.of(12.0, 14.0);

    private static class TableSpacing {
        Double before;
        Double after;
        boolean beforeAuto;
        boolean afterAuto;
        boolean present;
    }

    @Override
    public ErrorCategory getErrorCategory() {
        return ErrorCategory.FONT_NAME;
    }

    @Override
    public List<FormatError> check(String filePath) {
        List<FormatError> allErrors = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(filePath);
             XWPFDocument doc = new XWPFDocument(fis)) {

            for (XWPFTable table : MainContentUtils.getMainContentTables(doc)) {
                for (XWPFTableRow row : table.getRows()) {
                    for (XWPFTableCell cell : row.getTableCells()) {
                        for (XWPFParagraph paragraph : cell.getParagraphs()) {
                            String paragraphText = ParagraphUtils.getTrimmedText(paragraph);
                            if (paragraphText.isEmpty()) {
                                continue;
                            }
                            validateTableParagraph(paragraph, table, doc, allErrors);
                        }
                    }
                }
            }
        } catch (Exception e) {
            allErrors.add(buildException(e));
        }
        return allErrors;
    }

    private void validateTableParagraph(XWPFParagraph paragraph, XWPFTable table, XWPFDocument document,
                                        List<FormatError> errors) {
        Set<String> incorrectFonts = new HashSet<>();
        Set<String> incorrectSizes = new HashSet<>();

        List<XWPFRun> runs = paragraph.getRuns();
        if (runs.isEmpty()) {
            return;
        }

        for (XWPFRun run : runs) {
            String fragment = run.getText(0);
            if (fragment == null) {
                continue;
            }

            String font = getEffectiveFont(run, paragraph, table, document);
            if (font != null && !font.equalsIgnoreCase(RequirementsHolder.getFont())) {
                incorrectFonts.add(font);
            }

            Double size = getEffectiveFontSize(run, paragraph, table, document);
            if (size != null && !ALLOWED_SIZES.contains(size)) {
                incorrectSizes.add(String.format("%.1fpt", size));
            }
        }

        if (!incorrectFonts.isEmpty()) {
            errors.add(buildTableFontNameError(paragraph.getText().trim(), incorrectFonts));
        }
        if (!incorrectSizes.isEmpty()) {
            errors.add(buildTableFontSizeError(paragraph.getText().trim(), incorrectSizes));
        }

        TableSpacing spacing = resolveTableSpacing(paragraph, table, document);
        if (spacing != null && spacing.present) {
            if ((spacing.before != null && spacing.before > 0) || spacing.beforeAuto) {
                errors.add(buildTableSpacingBeforeError(paragraph.getText().trim(), spacing));
            }
            if ((spacing.after != null && spacing.after > 0) || spacing.afterAuto) {
                errors.add(buildTableSpacingAfterError(paragraph.getText().trim(), spacing));
            }
        }
    }

    private String getEffectiveFont(XWPFRun run, XWPFParagraph paragraph, XWPFTable table, XWPFDocument document) {
        String font = run.getFontFamily();
        if (font != null && !font.isEmpty()) {
            return font;
        }

        XWPFStyles styles = document.getStyles();

        String styleId = paragraph.getStyleID();
        if (styleId != null && styles != null) {
            font = getFontFromStyleChain(styles, styleId);
            if (font != null) {
                return font;
            }
        }

        String tableStyleId = table != null ? table.getStyleID() : null;
        if (tableStyleId != null && !tableStyleId.isEmpty() && styles != null) {
            font = getFontFromStyleChain(styles, tableStyleId);
            if (font != null) {
                return font;
            }
        }

        if (styles != null) {
            font = getFontFromDocDefaults(styles);
            if (font != null) {
                return font;
            }
        }

        return null;
    }

    private Double getEffectiveFontSize(XWPFRun run, XWPFParagraph paragraph, XWPFTable table,
                                        XWPFDocument document) {
        Double size = run.getFontSizeAsDouble();
        if (size != null && size > 0) {
            return size;
        }

        XWPFStyles styles = document.getStyles();

        String styleId = paragraph.getStyleID();
        if (styleId != null && styles != null) {
            size = getSizeFromStyleChain(styles, styleId);
            if (size != null) {
                return size;
            }
        }

        String tableStyleId = table != null ? table.getStyleID() : null;
        if (tableStyleId != null && !tableStyleId.isEmpty() && styles != null) {
            size = getSizeFromStyleChain(styles, tableStyleId);
            if (size != null) {
                return size;
            }
        }

        if (styles != null) {
            size = getSizeFromDocDefaults(styles);
            if (size != null) {
                return size;
            }
        }

        return null;
    }

    private TableSpacing resolveTableSpacing(XWPFParagraph paragraph, XWPFTable table, XWPFDocument document) {
        XWPFStyles styles = document.getStyles();

        CTSpacing spacing = null;
        if (paragraph.getCTP() != null && paragraph.getCTP().getPPr() != null) {
            spacing = paragraph.getCTP().getPPr().getSpacing();
        }

        if (spacing == null && styles != null) {
            String styleId = paragraph.getStyleID();
            if (styleId != null) {
                spacing = getSpacingFromStyleChain(styles, styleId);
            }
        }

        if (spacing == null && styles != null) {
            String tableStyleId = table != null ? table.getStyleID() : null;
            if (tableStyleId != null && !tableStyleId.isEmpty()) {
                spacing = getSpacingFromStyleChain(styles, tableStyleId);
            }
        }

        if (spacing == null && styles != null) {
            spacing = getSpacingFromDocDefaults(styles);
        }

        if (spacing == null) {
            return null;
        }

        TableSpacing ts = new TableSpacing();
        ts.present = true;
        ts.before = spacing.getBefore() != null
                ? ((BigInteger) spacing.getBefore()).doubleValue() / 20.0 : null;
        ts.after = spacing.getAfter() != null
                ? ((BigInteger) spacing.getAfter()).doubleValue() / 20.0 : null;
        ts.beforeAuto = isAutospacingOn(spacing.getBeforeAutospacing());
        ts.afterAuto = isAutospacingOn(spacing.getAfterAutospacing());
        return ts;
    }

    private String getFontFromStyleChain(XWPFStyles styles, String styleId) {
        Set<String> visited = new HashSet<>();
        String current = styleId;
        while (current != null && visited.add(current)) {
            XWPFStyle style = styles.getStyle(current);
            if (style == null || style.getCTStyle() == null) {
                break;
            }
            CTStyle ctStyle = style.getCTStyle();
            var rPr = ctStyle.getRPr();
            if (rPr != null) {
                List<CTFonts> fontsList = rPr.getRFontsList();
                if (fontsList != null && !fontsList.isEmpty()) {
                    String ascii = fontsList.getFirst().getAscii();
                    if (ascii != null && !ascii.isEmpty()) {
                        return ascii;
                    }
                }
            }
            if (ctStyle.isSetBasedOn() && ctStyle.getBasedOn() != null) {
                current = ctStyle.getBasedOn().getVal();
            } else {
                break;
            }
        }
        return null;
    }

    private Double getSizeFromStyleChain(XWPFStyles styles, String styleId) {
        Set<String> visited = new HashSet<>();
        String current = styleId;
        while (current != null && visited.add(current)) {
            XWPFStyle style = styles.getStyle(current);
            if (style == null || style.getCTStyle() == null) {
                break;
            }
            CTStyle ctStyle = style.getCTStyle();
            var rPr = ctStyle.getRPr();
            if (rPr != null) {
                List<CTHpsMeasure> szList = rPr.getSzList();
                if (szList != null && !szList.isEmpty()) {
                    var val = szList.getFirst().getVal();
                    if (val != null) {
                        return Integer.parseInt(val.toString()) / 2.0;
                    }
                }
            }
            if (ctStyle.isSetBasedOn() && ctStyle.getBasedOn() != null) {
                current = ctStyle.getBasedOn().getVal();
            } else {
                break;
            }
        }
        return null;
    }

    private CTSpacing getSpacingFromStyleChain(XWPFStyles styles, String styleId) {
        Set<String> visited = new HashSet<>();
        String current = styleId;
        while (current != null && visited.add(current)) {
            XWPFStyle style = styles.getStyle(current);
            if (style == null || style.getCTStyle() == null) {
                break;
            }
            CTStyle ctStyle = style.getCTStyle();
            CTPPrGeneral pPr = ctStyle.getPPr();
            CTSpacing spacing = pPr != null ? pPr.getSpacing() : null;
            if (spacing != null) {
                return spacing;
            }
            if (ctStyle.isSetBasedOn() && ctStyle.getBasedOn() != null) {
                current = ctStyle.getBasedOn().getVal();
            } else {
                break;
            }
        }
        return null;
    }

    private String getFontFromDocDefaults(XWPFStyles styles) {
        try {
            XWPFDefaultRunStyle def = styles.getDefaultRunStyle();
            if (def == null) {
                return null;
            }
            Method m = XWPFDefaultRunStyle.class.getDeclaredMethod("getRPr");
            m.setAccessible(true);
            CTRPr rPr = (CTRPr) m.invoke(def);
            if (rPr == null) {
                return null;
            }
            List<CTFonts> fontsList = rPr.getRFontsList();
            if (fontsList != null && !fontsList.isEmpty()) {
                return fontsList.getFirst().getAscii();
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    private Double getSizeFromDocDefaults(XWPFStyles styles) {
        try {
            XWPFDefaultRunStyle def = styles.getDefaultRunStyle();
            if (def == null) {
                return null;
            }
            Method m = XWPFDefaultRunStyle.class.getDeclaredMethod("getRPr");
            m.setAccessible(true);
            CTRPr rPr = (CTRPr) m.invoke(def);
            if (rPr == null) {
                return null;
            }
            List<CTHpsMeasure> szList = rPr.getSzList();
            if (szList != null && !szList.isEmpty()) {
                var val = szList.getFirst().getVal();
                if (val != null) {
                    return Integer.parseInt(val.toString()) / 2.0;
                }
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    private CTSpacing getSpacingFromDocDefaults(XWPFStyles styles) {
        try {
            XWPFDefaultParagraphStyle def = styles.getDefaultParagraphStyle();
            if (def == null) {
                return null;
            }
            Method m = XWPFDefaultParagraphStyle.class.getDeclaredMethod("getPPr");
            m.setAccessible(true);
            CTPPrGeneral pPr = (CTPPrGeneral) m.invoke(def);
            if (pPr == null) {
                return null;
            }
            return pPr.getSpacing();
        } catch (Exception ignored) {
        }
        return null;
    }

    private boolean isAutospacingOn(Object value) {
        if (value == null) {
            return false;
        }
        String s = String.valueOf(value);
        return s.equalsIgnoreCase("true") || s.equals("1") || s.equalsIgnoreCase("on");
    }

    private static FormatError buildException(Exception e) {
        FormatError error = new FormatError();
        error.setId("err_000");
        error.setCategory(ErrorCategory.FILE);
        error.setSeverity("error");
        error.setTitle("Помилка відкриття файлу: " + e.getMessage());
        error.setParagraphText("");
        return error;
    }

    private static FormatError buildTableFontNameError(String paragraphText, Set<String> incorrectFonts) {
        FormatError error = new FormatError();
        error.setId("err_table_font_name");
        error.setCategory(ErrorCategory.FONT_NAME);
        error.setSeverity("warning");
        error.setTitle("Невірна назва шрифта у таблиці");
        error.setParagraphText(paragraphText);
        error.setFound(incorrectFonts);
        error.setExpected(RequirementsHolder.getFont());
        return error;
    }

    private static FormatError buildTableFontSizeError(String paragraphText, Set<String> incorrectSizes) {
        FormatError error = new FormatError();
        error.setId("err_table_font_size");
        error.setCategory(ErrorCategory.FONT_SIZE);
        error.setSeverity("error");
        error.setTitle("Неправильний розмір шрифта у таблиці");
        error.setParagraphText(paragraphText);
        error.setFound(incorrectSizes);
        error.setExpected("12pt або 14pt");
        return error;
    }

    private static FormatError buildTableSpacingBeforeError(String paragraphText, TableSpacing spacing) {
        FormatError error = new FormatError();
        error.setId("err_table_spacing_before");
        error.setCategory(ErrorCategory.SPACING);
        error.setSeverity("error");
        error.setTitle("Невірний відступ перед абзацем у таблиці");
        error.setParagraphText(paragraphText);
        error.setFound(Set.of(spacingFound(spacing.before, spacing.beforeAuto)));
        error.setExpected("0 pt");
        return error;
    }

    private static FormatError buildTableSpacingAfterError(String paragraphText, TableSpacing spacing) {
        FormatError error = new FormatError();
        error.setId("err_table_spacing_after");
        error.setCategory(ErrorCategory.SPACING);
        error.setSeverity("error");
        error.setTitle("Невірний відступ після абзацу у таблиці");
        error.setParagraphText(paragraphText);
        error.setFound(Set.of(spacingFound(spacing.after, spacing.afterAuto)));
        error.setExpected("0 pt");
        return error;
    }

    private static String spacingFound(Double value, boolean auto) {
        if (value != null && auto) {
            return value + " pt (авто)";
        }
        if (value != null) {
            return value + " pt";
        }
        if (auto) {
            return "авто";
        }
        return "не задано";
    }
}
