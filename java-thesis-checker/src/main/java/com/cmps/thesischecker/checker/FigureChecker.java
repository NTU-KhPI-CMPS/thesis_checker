package com.cmps.thesischecker.checker;

import com.cmps.thesischecker.model.ErrorCategory;
import com.cmps.thesischecker.model.FormatError;
import com.cmps.thesischecker.utils.MainContentUtils;
import com.cmps.thesischecker.requirements.RequirementsHolder;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTP;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPPr;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class FigureChecker implements Checker {

    @Override
    public ErrorCategory getErrorCategory() {
        return ErrorCategory.STRUCTURAL_ELEMENT;
    }

    @Override
    public List<FormatError> check(String filePath) {
        List<FormatError> errors = new ArrayList<>();

        try (XWPFDocument doc = new XWPFDocument(new java.io.FileInputStream(filePath))) {
            List<XWPFParagraph> paragraphs = MainContentUtils.getMainContentParagraphs(doc);

            for (int i = 0; i < paragraphs.size(); i++) {
                XWPFParagraph para = paragraphs.get(i);
                if (hasDrawing(para)) {
                    String figureText = para.getText().trim();

                    String alignment = getAlignment(para);
                    if (!alignment.equalsIgnoreCase("CENTER")) {
                        errors.add(buildAlignmentError(figureText, alignment, "CENTER",
                                ErrorCategory.ALIGNMENT,
                                "Фігура повинна бути вирівняною по центру"));
                    }

                    if (i == 0 || !isBlankParagraph(paragraphs.get(i - 1))) {
                        errors.add(buildBlankLineError(figureText, true,
                                "Перед фігурою має бути один пустий рядок"));
                    }

                    if (i + 1 >= paragraphs.size()) {
                        errors.add(buildMissingCaptionError(figureText,
                                "Після фігурії очікується підпис «Рисунок»"));
                    } else {
                        XWPFParagraph captionPara = paragraphs.get(i + 1);
                        String captionText = captionPara.getText().trim();

                        if (hasDrawing(captionPara)) {
                            errors.add(buildUnexpectedFigureError(figureText,
                                    "Параграп підпису не повинен містити креслення"));
                        } else {
                            String captionAlignment = getAlignment(captionPara);
                            if (!captionAlignment.equalsIgnoreCase("CENTER")) {
                                errors.add(buildAlignmentError(captionText, captionAlignment, "CENTER",
                                        ErrorCategory.ALIGNMENT,
                                        "Підпис фігури повинен бути вирівняною по центру"));
                            }

                            if (!isValidCaption(captionText)) {
                                errors.add(buildCaptionFormatError(captionText,
                                        "Неправильний формат підпису фігури. Очікується: «Рисунок <номер> - <назва>»"));
                            }

                            if (i + 2 >= paragraphs.size() || !isBlankParagraph(paragraphs.get(i + 2))) {
                                errors.add(buildBlankLineError(captionText, false,
                                        "Після підпису фігури має бути один пустий рядок"));
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            errors.add(buildFileError(e));
        }

        return errors;
    }

    private static boolean hasDrawing(XWPFParagraph paragraph) {
        CTP ctp = paragraph.getCTP();
        return ctp != null && ctp.xmlText().contains("<w:drawing");
    }

    private static boolean isBlankParagraph(XWPFParagraph paragraph) {
        String text = paragraph.getText();
        return (text == null || text.trim().isEmpty()) && !hasDrawing(paragraph);
    }

    private static String getAlignment(XWPFParagraph paragraph) {
        String alignment = getAlignmentFromPPr(paragraph.getCTP().getPPr());
        if (alignment != null) {
            return alignment;
        }
        alignment = getAlignmentFromStyles(paragraph);
        return alignment == null ? "LEFT" : alignment;
    }

    private static String getAlignmentFromPPr(CTPPr pPr) {
        if (pPr == null || !pPr.isSetJc() || pPr.getJc() == null || pPr.getJc().getVal() == null) {
            return null;
        }
        return pPr.getJc().getVal().toString().toUpperCase();
    }

    private static String getAlignmentFromStyles(XWPFParagraph paragraph) {
        return null; // placeholder
    }

    private static boolean isValidCaption(String caption) {
        return Pattern.matches("^Рисунок\\s+\\d+\\s*-\\s+.+", caption);
    }

    private static FormatError buildAlignmentError(String text, String found, String expected,
                                                   ErrorCategory category, String title) {
        FormatError error = new FormatError();
        error.setId("err_figure_alignment");
        error.setCategory(category);
        error.setSeverity("error");
        error.setTitle(title);
        error.setParagraphText(text);
        error.setFound(java.util.Set.of(found));
        error.setExpected(expected);
        return error;
    }

    private static FormatError buildBlankLineError(String text, boolean beforeFigure, String title) {
        FormatError error = new FormatError();
        error.setId("err_figure_blank_line");
        error.setCategory(ErrorCategory.STRUCTURAL_ELEMENT);
        error.setSeverity("error");
        error.setTitle(title);
        error.setParagraphText(text);
        error.setFound(java.util.Set.of(beforeFigure ? "немає пустого рядка перед" : "немає пустого рядка після"));
        error.setExpected("один пустий рядок");
        return error;
    }

    private static FormatError buildMissingCaptionError(String figureText, String title) {
        FormatError error = new FormatError();
        error.setId("err_figure_missing_caption");
        error.setCategory(ErrorCategory.STRUCTURAL_ELEMENT);
        error.setSeverity("error");
        error.setTitle(title);
        error.setParagraphText(figureText);
        error.setExpected("Параграп з підписом «Рисунок»");
        error.setFound(java.util.Set.of("відсутній"));
        return error;
    }

    private static FormatError buildUnexpectedFigureError(String figureText, String title) {
        FormatError error = new FormatError();
        error.setId("err_figure_unexpected_in_caption");
        error.setCategory(ErrorCategory.STRUCTURAL_ELEMENT);
        error.setSeverity("error");
        error.setTitle(title);
        error.setParagraphText(figureText);
        error.setExpected("Відсутність креслення");
        error.setFound(java.util.Set.of("Креслення присутнє"));
        return error;
    }

    private static FormatError buildCaptionFormatError(String captionText, String title) {
        FormatError error = new FormatError();
        error.setId("err_figure_caption_format");
        error.setCategory(ErrorCategory.STRUCTURAL_ELEMENT);
        error.setSeverity("error");
        error.setTitle(title);
        error.setParagraphText(captionText);
        error.setExpected("«Рисунок <номер> - <назва>»");
        error.setFound(java.util.Set.of(captionText));
        return error;
    }

    private static FormatError buildFileError(Exception e) {
        FormatError error = new FormatError();
        error.setId("err_000");
        error.setCategory(ErrorCategory.FILE);
        error.setSeverity("error");
        error.setTitle("Помилка відкриття файлу: " + e.getMessage());
        error.setParagraphText("");
        return error;
    }
}