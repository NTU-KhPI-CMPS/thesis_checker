package com.cmps.thesischecker.checker;

import com.cmps.thesischecker.model.ErrorCategory;
import com.cmps.thesischecker.model.FormatError;
import com.cmps.thesischecker.requirements.RequirementsHolder;
import com.cmps.thesischecker.utils.AlignmentUtils;
import com.cmps.thesischecker.utils.FormulaUtils;
import com.cmps.thesischecker.utils.MainContentUtils;
import com.cmps.thesischecker.utils.StyleUtils;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

public class AlignmentChecker implements Checker {

    /**
     * Enum containing basic paragraph text alignment values with their localized names.
     */
    public enum Alignment {

        LEFT("По лівому краю"),
        RIGHT("По правому краю"),
        CENTER("По центру"),
        BOTH("По ширині"),
        FORMULA_ALIGNMENT("По ширині або правому краю");

        final String name;

        Alignment(String name) {
            this.name = name;
        }
    }


    /**
     * Returns the error category for this instance.
     *
     * @return {@link ErrorCategory#ALIGNMENT} indicating that this error
     *         is related to alignment issues
     */
    @Override
    public ErrorCategory getErrorCategory() {
        return ErrorCategory.ALIGNMENT;
    }

    /**
     * Checks the document for text alignment violations and returns all found format errors.
     *
     * @param filePath path to the DOCX file
     * @return list of detected formatting errors
     */
    @Override
    public List<FormatError> check(String filePath) {
        List<FormatError> allErrors = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(filePath);
             XWPFDocument doc = new XWPFDocument(fis)) {
            for (XWPFParagraph paragraph : MainContentUtils.getMainContentParagraphs(doc)) {
                String paragraphText = paragraph.getText().trim();
                if (paragraphText.isEmpty()) {
                    continue;
                }
                Optional<String> incorrectAlignment = validate(paragraph);
                if (incorrectAlignment.isPresent()) {
                    String expectedRaw;

                    if (StyleUtils.isHeading1(paragraph)) {
                        expectedRaw = RequirementsHolder.getHeadingAlignment();
                    } else if (FormulaUtils.paragraphIsFormula(paragraph)) {
                        expectedRaw = RequirementsHolder.getFormulaAlignment();
                    } else {
                        expectedRaw = RequirementsHolder.getMainTextAlignment();
                    }

                    String expectedLocalized = mapToLocalized(expectedRaw);
                    allErrors.add(buildAlignmentError(paragraphText, incorrectAlignment.get(), expectedLocalized));
                }
            }
        } catch (Exception e) {
            allErrors.add(buildException(e));
        }

        return allErrors;
    }

    /**
     * Creates and returns a file-level error when the document cannot be opened or read.
     *
     * @param e the exception that was thrown
     * @return the created format error
     */
    private static FormatError buildException(Exception e) {
        FormatError error = new FormatError();
        error.setId("err_000");
        error.setCategory(ErrorCategory.FILE);
        error.setSeverity("error");
        error.setTitle("Помилка відкриття файлу: " + e.getMessage());
        error.setParagraphText("");
        return error;
    }

    /**
     * Creates and returns an alignment error for a paragraph with invalid alignment values.
     *
     * @param paragraphText the paragraph text
     * @param incorrectAlignment the detected incorrect alignment value
     * @param expected localized string specifying the expected alignment
     * @return the created format error
     */
    private static FormatError buildAlignmentError(String paragraphText, String incorrectAlignment, String expected) {
        FormatError error = new FormatError();
        error.setId("err_alignment");
        error.setCategory(ErrorCategory.ALIGNMENT);
        error.setSeverity("error");
        error.setTitle("Невірне вирівнювання тексту");
        error.setParagraphText(paragraphText);

        // Map raw alignment string to localized name for output
        error.setFound(Set.of(mapToLocalized(incorrectAlignment)));
        error.setExpected(expected);
        return error;
    }

    /**
     * Maps raw POI alignment strings to readable, localized names.
     *
     * @param align raw alignment string (e.g., "LEFT", "CENTER")
     * @return the localized alignment name, or the raw string if unknown
     */
    private static String mapToLocalized(String align) {
        return switch (align) {
            case "LEFT" -> Alignment.LEFT.name;
            case "RIGHT" -> Alignment.RIGHT.name;
            case "CENTER" -> Alignment.CENTER.name;
            case "BOTH" -> Alignment.BOTH.name;
            case "BOTH, RIGHT" -> Alignment.FORMULA_ALIGNMENT.name;
            default -> align;
        };
    }

    /**
     * Checks if the paragraph text starts with a figure caption prefix.
     * Figure captions are handled by {@link FigureChecker} and should not be validated here.
     *
     * @param paragraph the paragraph to check
     * @return true if the paragraph text starts with "Рисунок" or "Рис.", false otherwise
     */
    private static boolean isFigureCaption(XWPFParagraph paragraph) {
        String text = paragraph.getText();
        if (text == null) {
            return false;
        }
        String trimmed = text.trim();
        return trimmed.startsWith("Рисунок") || trimmed.startsWith("Рис.");
    }

    /**
     * Validates a paragraph against the expected alignment.
     *
     * @param paragraph the paragraph to validate
     * @return an Optional containing the detected alignment value if the paragraph is invalid, or empty otherwise
     */
    Optional<String> validate(XWPFParagraph paragraph) {
        String actualAlignment = AlignmentUtils.getAlignment(paragraph);

        if (isFigureCaption(paragraph)) {
            return Optional.empty();
        }

        // If paragraph contains a drawing, it must be centered
        if (AlignmentUtils.hasDrawing(paragraph)) {
            if (!actualAlignment.equals("CENTER")) {
                return Optional.of(actualAlignment);
            }
            return Optional.empty();
        }

        if (StyleUtils.isHeading1(paragraph)) {
            if (!actualAlignment.equals(RequirementsHolder.getHeadingAlignment())) {
                return Optional.of(actualAlignment);
            }
            return Optional.empty();
        }
        if (FormulaUtils.paragraphIsFormula(paragraph)) {
            if (!Arrays.asList(RequirementsHolder.getFormulaAlignment().split(", ")).contains(actualAlignment)) {
                return Optional.of(actualAlignment);
            }
            return Optional.empty();
        }
        if (!actualAlignment.equals(RequirementsHolder.getMainTextAlignment())) {
            return Optional.of(actualAlignment);
        }

        return Optional.empty();
    }
}
