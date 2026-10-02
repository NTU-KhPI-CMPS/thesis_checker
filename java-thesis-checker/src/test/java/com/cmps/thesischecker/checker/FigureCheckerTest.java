package com.cmps.thesischecker.checker;

import com.cmps.thesischecker.model.ErrorCategory;
import com.cmps.thesischecker.model.FormatError;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests for FigureChecker.
 */
public class FigureCheckerTest extends BaseTest {

    private final Checker checker = new FigureChecker();

    @Override
    protected Checker getChecker() {
        return checker;
    }

    private static final String TEST_FILE = "src/test/resources/incorrect_figure.docx";

    @Test
    void check_incorrectFigureAlignment_errorFound() {
        // GIVEN
        String expectedParagraphText = "Тестовий абзац, що містить рисунок:";

        // WHEN
        List<FormatError> errors = checker.check(TEST_FILE);

        // THEN
        List<FormatError> alignmentErrors = errors.stream()
                .filter(e -> Objects.equals(e.getParagraphText(), expectedParagraphText))
                .filter(e -> Objects.equals(e.getId(), "err_figure_alignment"))
                .toList();
        assertEquals(1, alignmentErrors.size(), "Expected exactly one figure alignment error");
        FormatError error = alignmentErrors.getFirst();
        assertEquals(ErrorCategory.ALIGNMENT, error.getCategory());
        assertEquals("CENTER", error.getExpected());
        assertEquals(1, error.getFound().size());
        String found = error.getFound().iterator().next();
        assertEquals("LEFT", found);
        assertEquals(expectedParagraphText, error.getParagraphText());
    }

    @Test
    void check_missingBlankLineBeforeFigure_errorFound() {
        // GIVEN
        String expectedParagraphText = "Тестовий абзац, що містить рисунок:";

        // WHEN
        List<FormatError> errors = checker.check(TEST_FILE);

        // THEN
        List<FormatError> blankLineErrors = errors.stream()
                .filter(e -> Objects.equals(e.getParagraphText(), expectedParagraphText))
                .filter(e -> Objects.equals(e.getId(), "err_figure_blank_line"))
                .filter(e -> Objects.equals(e.getTitle(), "Перед рисунком має бути один пустий рядок"))
                .toList();
        assertEquals(1, blankLineErrors.size(), "Expected exactly one missing blank line before figure error");
        FormatError error = blankLineErrors.getFirst();
        assertEquals(ErrorCategory.STRUCTURAL_ELEMENT, error.getCategory());
        assertEquals("один пустий рядок", error.getExpected());
        assertEquals(1, error.getFound().size());
        String found = error.getFound().iterator().next();
        assertEquals("немає пустого рядка перед", found);
        assertEquals(expectedParagraphText, error.getParagraphText());
    }

    @Test
    void check_incorrectFigureCaptionFormat_errorFound() {
        // GIVEN
        String expectedParagraphText = "Рис. 1 Неправильний підпис рисунка";

        // WHEN
        List<FormatError> errors = checker.check(TEST_FILE);

        // THEN
        List<FormatError> captionFormatErrors = errors.stream()
                .filter(e -> Objects.equals(e.getParagraphText(), expectedParagraphText))
                .filter(e -> Objects.equals(e.getId(), "err_figure_caption_format"))
                .toList();
        assertEquals(1, captionFormatErrors.size(), "Expected exactly one caption format error");
        FormatError error = captionFormatErrors.getFirst();
        assertEquals(ErrorCategory.STRUCTURAL_ELEMENT, error.getCategory());
        assertEquals("«Рисунок <номер> - <назва>»", error.getExpected());
        assertEquals(1, error.getFound().size());
        String found = error.getFound().iterator().next();
        assertEquals(expectedParagraphText, found);
        assertEquals(expectedParagraphText, error.getParagraphText());
    }
}
