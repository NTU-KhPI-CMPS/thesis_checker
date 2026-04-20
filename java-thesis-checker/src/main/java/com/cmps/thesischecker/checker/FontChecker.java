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
                String paraText = paragraph.getText().trim();
                if (paraText.isEmpty()) continue;
                Set<String> incorrectFonts = validate(paragraph);
                if (!incorrectFonts.isEmpty()) {
                    FormatError err = new FormatError();
                    err.setId("err_font");
                    err.setCategory(ErrorCategory.FONT_NAME);
                    err.setSeverity("error");
                    err.setTitle("Невірні шрифти у параграфі");
                    err.setParagraphText(paraText);
                    err.setFound(incorrectFonts);
                    err.setExpected(RequirementsHolder.getFont());
                    allErrors.add(err);
                }
            }

            // Check headers
            for (XWPFHeader header : doc.getHeaderList()) {
                for (XWPFParagraph paragraph : header.getParagraphs()) {
                    String paraText = paragraph.getText().trim();
                    if (paraText.isEmpty()) continue;
                    Set<String> incorrectFonts = validate(paragraph);
                    if (!incorrectFonts.isEmpty()) {
                        FormatError err = new FormatError();
                        err.setId("err_font_header");
                        err.setCategory(ErrorCategory.FONT_NAME);
                        err.setSeverity("error");
                        err.setTitle("Невірні шрифти у заголовку");
                        err.setParagraphText(paraText);
                        err.setFound(incorrectFonts);
                        err.setExpected(RequirementsHolder.getFont());
                        allErrors.add(err);
                    }
                }
            }

            // Check footers
            for (XWPFFooter footer : doc.getFooterList()) {
                for (XWPFParagraph paragraph : footer.getParagraphs()) {
                    String paraText = paragraph.getText().trim();
                    if (paraText.isEmpty()) continue;
                    Set<String> incorrectFonts = validate(paragraph);
                    if (!incorrectFonts.isEmpty()) {
                        FormatError err = new FormatError();
                        err.setId("err_font_footer");
                        err.setCategory(ErrorCategory.FONT_NAME);
                        err.setSeverity("error");
                        err.setTitle("Невірні шрифти у підвалі");
                        err.setParagraphText(paraText);
                        err.setFound(incorrectFonts);
                        err.setExpected(RequirementsHolder.getFont());
                        allErrors.add(err);
                    }
                }
            }

            // Check tables
            for (XWPFTable table : doc.getTables()) {
                for (XWPFTableRow row : table.getRows()) {
                    for (XWPFTableCell cell : row.getTableCells()) {
                        for (XWPFParagraph paragraph : cell.getParagraphs()) {
                            String paraText = paragraph.getText().trim();
                            if (paraText.isEmpty()) continue;
                            Set<String> incorrectFonts = validate(paragraph);
                            if (!incorrectFonts.isEmpty()) {
                                FormatError err = new FormatError();
                                err.setId("err_font_table");
                                err.setCategory(ErrorCategory.FONT_NAME);
                                err.setSeverity("error");
                                err.setTitle("Невірні шрифти у таблиці");
                                err.setParagraphText(paraText);
                                err.setFound(incorrectFonts);
                                err.setExpected(RequirementsHolder.getFont());
                                allErrors.add(err);
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
        // Алгоритм отримання шрифту з об'єкту run
        // Дістаємо шрифт з run
        String font = run.getFontFamily();
        
        // Перевіряємо: шрифт == null?
        if (font != null) {
            // Ні → Зберігаємо назву шрифту. Кінець.
            return font;
        }
        // Так → Продовжуємо.
        // Дістаємо з об'єкту run параграф, використовуючи getParent()
        Object parent = run.getParent();
        if (!(parent instanceof XWPFParagraph)) {
            // Перевіряємо: стиль == null?
            // Так → Зберігаємо назву шрифту за замовчуванням ("Normal"). Кінець.
            return RequirementsHolder.getFont();
        }
        XWPFParagraph para = (XWPFParagraph) parent;
        // Дістаємо стиль з параграфа
        String styleId = para.getStyleID();
        System.out.println("PARAGRAPH STYLE ID: " + styleId);
        
        if (styleId == null) {
            // Перевіряємо: стиль == null?
            // Так → Зберігаємо назву шрифту за замовчуванням ("Normal"). Кінець.
            return RequirementsHolder.getFont();
        }
        // Ні → Зберігаємо назву шрифту, отриману зі стилю (згідно з алгоритмом з попередньої діаграми). Кінець.
        return getFontFromParagraphStyle(para);
    }

    private String getFontFromParagraphStyle(XWPFParagraph paragraph) {
        // Алгоритм отримання шрифту з урахуванням стилю параграфа
        // Перевіряємо: стиль == null?
        String styleId = paragraph.getStyleID();
        if (styleId == null) {
            // Так → Зберігаємо назву шрифту за замовчуванням ("Normal"). Кінець.
            return RequirementsHolder.getFont();
        }
        // Ні → Продовжуємо.
        // Отримуємо документ з об'єкта параграфа
        XWPFDocument document = paragraph.getDocument();
        // За назвою стилю шукаємо його шрифт у документі.
        // Перевіряємо: шрифт == null?
        XWPFStyle style = document.getStyles().getStyle(styleId);
        
        if (style == null) {
            // Так → Повертаємо шрифт за замовчуванням ("Normal"). Кінець.
            return RequirementsHolder.getFont();
        }
        
        // Отримуємо реальний шрифт із стилю
        try {
            Object ctStyle = style.getCTStyle();
            java.lang.reflect.Method getRPrMethod = ctStyle.getClass().getMethod("getRPr");
            Object rPr = getRPrMethod.invoke(ctStyle);
            
            if (rPr != null) {
                try {
                    java.lang.reflect.Method getRFontsMethod = rPr.getClass().getMethod("getRFontsList");
                    java.util.List<?> rFontsList = (java.util.List<?>) getRFontsMethod.invoke(rPr);
                    
                    if (!rFontsList.isEmpty()) {
                        Object rFonts = rFontsList.get(0);
                        java.lang.reflect.Method getAsciiMethod = rFonts.getClass().getMethod("getAscii");
                        String fontName = (String) getAsciiMethod.invoke(rFonts);
                        
                        if (fontName != null) {
                            // Ні → Повертаємо знайдений шрифт. Кінець.
                            return fontName;
                        }
                    }
                } catch (Exception e) {
                    // Проблема з читанням шрифту зі стилю
                }
            }
        } catch (Exception e) {
            // Зовсім не можемо отримати шрифт зі стилю
            return RequirementsHolder.getFont();
        }
        
        // Так → Повертаємо шрифт за замовчуванням ("Normal"). Кінець.
        return RequirementsHolder.getFont();
    }
}
