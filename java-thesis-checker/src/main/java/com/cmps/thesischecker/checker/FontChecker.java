package com.cmps.thesischecker.checker;

import com.cmps.thesischecker.model.FormatError;
import com.cmps.thesischecker.requirements.RequirementsHolder;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
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
        String expectedFont = RequirementsHolder.getFont();
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
                    err.setCategory("font");
                    err.setSeverity("error");
                    err.setTitle("Невірні шрифти у параграфі");
                    err.setParagraphText(paraText);
                    err.setFound(incorrectFonts);
                    err.setExpected(RequirementsHolder.getFont());
                    err.setSuggestions(Set.of(RequirementsHolder.getFont()));
                    allErrors.add(err);
                }
            }
        } catch (Exception e) {
            FormatError err = new FormatError();
            err.setId("err_000");
            err.setCategory("file");
            err.setSeverity("error");
            err.setTitle("Помилка відкриття файлу: " + e.getMessage());
            err.setParagraphText("");
            err.setSuggestions(Set.of());
            allErrors.add(err);
        }
        return allErrors;
    }

    private Set<String> validate(XWPFParagraph paragraph) {
        Set<String> incorrectFonts = new HashSet<>();
        String expectedFont = RequirementsHolder.getFont();
        List<XWPFRun> runs = paragraph.getRuns();
        if (runs.isEmpty()) return incorrectFonts;

        for (XWPFRun run : runs) {
            String fragmentText = run.getText(0);
            if (fragmentText == null) continue;

            String fontName = run.getFontFamily();
            if (fontName != null && !fontName.equalsIgnoreCase(expectedFont)) {
                incorrectFonts.add(fontName);
            }
        }
        return incorrectFonts;
    }
}
