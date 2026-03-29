package com.cmps.thesischecker.checker;

import org.apache.poi.xwpf.usermodel.*;
import java.io.FileInputStream;
import java.util.*;

public class FontChecker implements Checker {
    private final String expectedFont;

    public FontChecker() {
        this("Times New Roman");
    }

    public FontChecker(String expectedFont) {
        this.expectedFont = expectedFont;
    }

    public boolean check(String filePath) {
        try (FileInputStream fis = new FileInputStream(filePath);
             XWPFDocument doc = new XWPFDocument(fis)) {
            for (XWPFParagraph paragraph : doc.getParagraphs()) {
                String paraText = paragraph.getText().trim();
                if (paraText.isEmpty()) continue;
                int[] errorIdCounter = new int[]{0};
                List<Map<String, Object>> errors = validate(paragraph, 1, paraText, errorIdCounter);
                if (!errors.isEmpty()) return false;
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public List<Map<String, Object>> validate(XWPFParagraph paragraph, int paraIndex, String shortParaText, int[] errorIdCounter) {
        List<Map<String, Object>> errors = new ArrayList<>();
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

            String displayFragment = getDisplayFragment(runs, i, fragmentText);
            int start = startPositions[i];
            int end = start + fragmentText.length();

            Map<String, Object> err = new LinkedHashMap<>();
            err.put("id", String.format("err_%03d", ++errorIdCounter[0]));
            err.put("category", "font");
            err.put("severity", "error");
            err.put("title", "Невірний шрифт: " + fontName);
            err.put("paragraph_index", paraIndex);
            err.put("paragraph_text", shortParaText);
            Map<String, Integer> highlight = new LinkedHashMap<>();
            highlight.put("start", start);
            highlight.put("end", end);
            err.put("highlight", highlight);
            err.put("found", fontName);
            err.put("expected", expectedFont);
            err.put("suggestions", Collections.singletonList(expectedFont));
            errors.add(err);
        }
        return errors;
    }

    private String getDisplayFragment(List<XWPFRun> runs, int idx, String fragmentText) {
        boolean isSpace = fragmentText.trim().isEmpty();
        if (isSpace) {
            String prevText = "";
            for (int j = idx-1; j >= 0; j--) {
                String t = runs.get(j).getText(0);
                if (t != null && !t.trim().isEmpty()) {
                    prevText = t.trim();
                    break;
                }
            }
            String nextText = "";
            for (int j = idx+1; j < runs.size(); j++) {
                String t = runs.get(j).getText(0);
                if (t != null && !t.trim().isEmpty()) {
                    nextText = t.trim();
                    break;
                }
            }
            if (!prevText.isEmpty() || !nextText.isEmpty()) {
                return "пробіл між \"" + prevText + "\" та \"" + nextText + "\"";
            } else {
                return "<пробіл без контексту>";
            }
        } else {
            return fragmentText.length() > 70 ? fragmentText.substring(0, 70) + "..." : fragmentText;
        }
    }
}