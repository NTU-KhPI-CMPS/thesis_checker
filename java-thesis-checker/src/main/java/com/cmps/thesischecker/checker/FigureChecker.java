package com.cmps.thesischecker.checker;

import com.cmps.thesischecker.model.ErrorCategory;
import com.cmps.thesischecker.model.FormatError;
import com.cmps.thesischecker.utils.AlignmentUtils;
import com.cmps.thesischecker.utils.MainContentUtils;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;

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
                if (AlignmentUtils.hasDrawing(para)) {
                    String figureText = para.getText().trim();

                    String alignment = AlignmentUtils.getAlignment(para);
                    if (!alignment.equalsIgnoreCase("CENTER")) {
                        errors.add(buildAlignmentError(figureText, alignment, "CENTER",
                                ErrorCategory.ALIGNMENT,
                                "Рисунок повинен бути вирівняним по центру"));
                    }

                    if (i == 0 || !isBlankParagraph(paragraphs.get(i - 1))) {
                        errors.add(buildBlankLineError(figureText, true,
                                "Перед рисунком має бути один пустий рядок"));
                    }

                    if (i + 1 >= paragraphs.size()) {
                        errors.add(buildMissingCaptionError(figureText,
                                "Після рисунку очікується підпис «Рисунок»"));
                    } else {
                        XWPFParagraph captionPara = paragraphs.get(i + 1);
                        String captionText = captionPara.getText().trim();

                        if (AlignmentUtils.hasDrawing(captionPara)) {
                            errors.add(buildUnexpectedFigureError(figureText,
                                    "Параграф підпису не повинен містити креслення"));
                        } else {
                            String captionAlignment = AlignmentUtils.getAlignment(captionPara);
                            if (!captionAlignment.equalsIgnoreCase("CENTER")) {
                                errors.add(buildAlignmentError(captionText, captionAlignment, "CENTER",
                                        ErrorCategory.ALIGNMENT,
                                        "Підпис рисунка повинен бути вирівняним по центру"));
                            }

                            if (!isValidCaption(captionText)) {
                                errors.add(buildCaptionFormatError(captionText,
                                        "Неправильний формат підпису рисунка. Очікується: «Рисунок <номер> - <назва>»"));
                            }

                            if (i + 2 >= paragraphs.size() || !isBlankParagraph(paragraphs.get(i + 2))) {
                                errors.add(buildBlankLineError(captionText, false,
                                        "Після підпису рисунка має бути один пустий рядок"));
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

    private static boolean isBlankParagraph(XWPFParagraph paragraph) {
        String text = paragraph.getText();
        return (text == null || text.trim().isEmpty()) && !AlignmentUtils.hasDrawing(paragraph);
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
        error.setExpected("Параграф з підписом «Рисунок»");
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
