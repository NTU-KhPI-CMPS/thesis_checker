package com.cmps.thesischecker.checker;

import com.cmps.thesischecker.model.FormatError;
import com.cmps.thesischecker.requirements.RequirementsHolder;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import java.io.FileInputStream;
import java.util.*;

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
                int[] errorIdCounter = new int[]{0};
                List<FormatError> errors = validate(paragraph, 1, paraText, errorIdCounter);
                allErrors.addAll(errors);
            }
        } catch (Exception e) {
            FormatError err = new FormatError();
            err.setId("err_000");
            err.setCategory("file");
            err.setSeverity("error");
            err.setTitle("Помилка відкриття файлу: " + e.getMessage());
            err.setParagraphIndex(0);
            err.setParagraphText("");
            allErrors.add(err);
        }
        return allErrors;
    }

    private List<FormatError> validate(XWPFParagraph paragraph, int paraIndex, String shortParaText, int[] errorIdCounter) {
        List<FormatError> errors = new ArrayList<>();
        String expectedFont = RequirementsHolder.getFont();
        List<XWPFRun> runs = paragraph.getRuns();
        if (runs.isEmpty()) return errors;

        int[] startPositions = new int[runs.size()];
        int pos = 0;
        for (int i = 0; i < runs.size(); i++) {
            startPositions[i] = pos;
            String runText = runs.get(i).getText(0);
            if (runText != null) pos += runText.length();
        }

        for (int i = 0; i < runs.size(); i++) {
            XWPFRun run = runs.get(i);
            String fragmentText = run.getText(0);
            if (fragmentText == null) continue;

            String fontName = run.getFontFamily();
            if (fontName == null || fontName.equalsIgnoreCase(expectedFont)) continue;

            int start = startPositions[i];
            int end = start + fragmentText.length();

            FormatError err = new FormatError();
            err.setId(String.format("err_%03d", ++errorIdCounter[0]));
            err.setCategory("font");
            err.setSeverity("error");
            err.setTitle("Невірний шрифт: " + fontName);
            err.setParagraphIndex(paraIndex);
            err.setParagraphText(shortParaText);
            err.setHighlightStart(start);
            err.setHighlightEnd(end);
            err.setFound(fontName);
            err.setExpected(expectedFont);
            errors.add(err);
        }
        return errors;
    }
}
