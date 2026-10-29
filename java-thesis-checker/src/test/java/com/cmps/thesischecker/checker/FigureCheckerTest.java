package com.cmps.thesischecker.checker;

import com.cmps.thesischecker.model.ErrorCategory;
import com.cmps.thesischecker.model.FormatError;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
    private static final String CORRECT_FILE = "src/test/resources/correct_figures.docx";

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
        assertEquals(ErrorCategory.FIGURE, error.getCategory());
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
        assertEquals(ErrorCategory.FIGURE, error.getCategory());
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
        assertEquals(ErrorCategory.FIGURE, error.getCategory());
        assertEquals("«Рисунок <номер> - <назва>»", error.getExpected());
        assertEquals(1, error.getFound().size());
        String found = error.getFound().iterator().next();
        assertEquals(expectedParagraphText, found);
        assertEquals(expectedParagraphText, error.getParagraphText());
    }

    @Test
    void check_correctFigures_noErrors() {
        // WHEN
        List<FormatError> errors = checker.check(CORRECT_FILE);

        // THEN
        assertTrue(errors.isEmpty(), "Expected no errors in correct_figures.docx, but found: " + errors);
    }

    @Test
    void check_figureWithNoNearestText_returnsErrorMessageInParagraphText() {
        // GIVEN
        String testFile = "src/test/resources/figure_without_text.docx";

        // WHEN
        List<FormatError> errors = checker.check(testFile);

        // THEN
        List<FormatError> captionFormatErrors = errors.stream()
                .filter(e -> Objects.equals(e.getId(), "err_figure_caption_format"))
                .toList();
        assertEquals(1, captionFormatErrors.size(), "Expected exactly one caption format error");
        FormatError error = captionFormatErrors.getFirst();
        assertEquals(ErrorCategory.FIGURE, error.getCategory());
        assertEquals("«Рисунок <номер> - <назва>»", error.getExpected());
        assertTrue(error.getParagraphText().contains("There is no text in the document with the incorrectly formatted image")
                || error.getParagraphText().contains("ERROR: Cannot find nearest to image text"),
                "Expected error message in paragraph text, but got: " + error.getParagraphText());
        assertEquals(1, error.getFound().size());
    }

    @ParameterizedTest(name = "Caption: [{0}] -> should match: {1}")
    @MethodSource("captionTestCases")
    void check_captionPattern_validatesCorrectly(String caption, boolean expectedResult) {

        // WHEN
        boolean actualResult = FigureChecker.isValidCaption(caption);

        // THEN
        assertEquals(expectedResult, actualResult, "Regex validation failed for caption: [" + caption + "]");
    }

    private static Stream<Arguments> captionTestCases() {
        return Stream.of(
                Arguments.of("Рисунок 1 - Назва", true),
                Arguments.of("Рисунок 1.10.3 - Три рівні", true),
                Arguments.of("Рисунок 5.1 (А) - Назва з дужками", true),
                Arguments.of("Рисунок 5.2 (Б) – Інтерфейс програми: вибір файлу у форматі .docx.\n(у темній темі)", true), // ТЕСТ НА БАГАТОРЯДКОВІСТЬ

                Arguments.of("Рисунок 1", false),
                Arguments.of("Рисунок 1 - ", false),
                Arguments.of("Рис. 1 - Помилка", false),
                Arguments.of("Рисунок 1-Пропущений пробіл", false),
                Arguments.of("Рисунок1 - Без пробілу після слова", false)
        );
    }

}
