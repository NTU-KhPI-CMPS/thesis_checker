package com.cmps.thesischecker.checker;

import com.cmps.thesischecker.model.FormatError;
import com.cmps.thesischecker.requirements.RequirementsHolder;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

import static com.cmps.thesischecker.model.ErrorCategory.INDENTATION;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ParagraphIndentationCheckerTest extends BaseTest {

    private final Checker checker = new ParagraphIndentationChecker();

    @Override
    protected Checker getChecker() {
        return checker;
    }

    @Test
    void check_correctFormatting() {
        //WHEN
        List<FormatError> result = checker.check("src/test/resources/correct_left_right_indentations.docx");

        // THEN
        assertTrue(result.isEmpty(), "File should not have any errors related to left or right indentations.");
    }

    @Test
    void check_incorrectFormatting_defaultFormatting() {
        // GIVEN
        String expectedParagraphText = "Неправильний відступ тільки лівий через верхню панель (0.5 cm).";
        double expectedValue = 0.5;

        //WHEN
        List<FormatError> result = checker.check("src/test/resources/incorrect_left_right_indentations.docx");

        // THEN
        List<FormatError> errorsWithExpectedText
                = result.stream()
                .filter(fe -> Objects.equals(fe.getParagraphText(), expectedParagraphText))
                .toList();
        assertEquals(1, errorsWithExpectedText.size(), "Only one error with expected text should be found.");

        FormatError formatError = errorsWithExpectedText.getFirst();
        assertEquals(expectedParagraphText, formatError.getParagraphText());
        assertEquals(INDENTATION, formatError.getCategory());
        assertEquals(Set.of(String.format(Locale.US, "%.1f см", expectedValue)), formatError.getFound());
        assertEquals(RequirementsHolder.getParagraphSpacing() + " см", formatError.getExpected());
    }

    @Test
    void check_incorrectFormatting_defaultFormatting_left_indentation() {
        // GIVEN
        String expectedParagraphText = "Неправильний відступ тільки лівий через верхню панель (0.5 cm).";
        double expectedValue = 0.5;

        //WHEN
        List<FormatError> result = checker.check("src/test/resources/incorrect_left_right_indentations.docx");

        // THEN
        List<FormatError> errorsWithExpectedText
                = result.stream()
                .filter(fe -> Objects.equals(fe.getParagraphText(), expectedParagraphText))
                .toList();
        assertEquals(1, errorsWithExpectedText.size(), "Only one error with expected text should be found.");

        FormatError formatError = errorsWithExpectedText.getFirst();
        assertEquals(expectedParagraphText, formatError.getParagraphText());
        assertEquals(INDENTATION, formatError.getCategory());
        assertEquals(Set.of(String.format(Locale.US, "%.1f см", expectedValue)), formatError.getFound());
        assertEquals(RequirementsHolder.getParagraphSpacing() + " см", formatError.getExpected());
    }

    @Test
    void check_incorrectFormatting_defaultFormatting_right_indentation() {
        // GIVEN
        String expectedParagraphText = "Неправильний відступ тільки правий через верхню панель (0.5 cm).";
        double expectedValue = 0.5;

        //WHEN
        List<FormatError> result = checker.check("src/test/resources/incorrect_left_right_indentations.docx");

        // THEN
        List<FormatError> errorsWithExpectedText
                = result.stream()
                .filter(fe -> Objects.equals(fe.getParagraphText(), expectedParagraphText))
                .toList();
        assertEquals(1, errorsWithExpectedText.size(), "Only one error with expected text should be found.");

        FormatError formatError = errorsWithExpectedText.getFirst();
        assertEquals(expectedParagraphText, formatError.getParagraphText());
        assertEquals(INDENTATION, formatError.getCategory());
        assertEquals(Set.of(String.format(Locale.US, "%.1f см", expectedValue)), formatError.getFound());
        assertEquals(RequirementsHolder.getParagraphSpacing() + " см", formatError.getExpected());
    }

    @Test
    void check_incorrectFormatting_defaultFormatting_both_indentations() {
        // GIVEN
        String expectedParagraphText = "Обидва відступи не правильні через верхню панель(1 cm left and right).";
        double expectedValue = 1.0;

        //WHEN
        List<FormatError> result = checker.check("src/test/resources/incorrect_left_right_indentations.docx");

        // THEN
        List<FormatError> errorsWithExpectedText
                = result.stream()
                .filter(fe -> Objects.equals(fe.getParagraphText(), expectedParagraphText))
                .toList();
        assertEquals(2, errorsWithExpectedText.size(), "Only one error with expected text should be found.");

        FormatError formatError = errorsWithExpectedText.getFirst();
        assertEquals(expectedParagraphText, formatError.getParagraphText());
        assertEquals(INDENTATION, formatError.getCategory());
        assertEquals(Set.of(String.format(Locale.US, "%.1f см", expectedValue)), formatError.getFound());
        assertEquals(RequirementsHolder.getParagraphSpacing() + " см", formatError.getExpected());
    }

    @Test
    void check_incorrectFormatting_formattingFromStyle_left_indentation() {
        // GIVEN
        String expectedParagraphText = "Неправильний лівий відступ через стилі (10 cm).";
        double expectedValue = 10.0;

        //WHEN
        List<FormatError> result = checker.check("src/test/resources/incorrect_left_right_indentations.docx");

        // THEN
        List<FormatError> errorsWithExpectedText
                = result.stream()
                .filter(fe -> Objects.equals(fe.getParagraphText(), expectedParagraphText))
                .toList();
        assertEquals(1, errorsWithExpectedText.size(), "Only one error with expected text should be found.");

        FormatError formatError = errorsWithExpectedText.getFirst();
        assertEquals(expectedParagraphText, formatError.getParagraphText());
        assertEquals(INDENTATION, formatError.getCategory());
        assertEquals(Set.of(String.format(Locale.US, "%.1f см", expectedValue)), formatError.getFound());
        assertEquals(RequirementsHolder.getParagraphSpacing() + " см", formatError.getExpected());
    }

    @Test
    void check_incorrectFormatting_formattingFromStyle_right_indentation() {
        // GIVEN
        String expectedParagraphText = "Неправильнй правий відступ через стилі (11 cm).";
        double expectedValue = 11.0;

        //WHEN
        List<FormatError> result = checker.check("src/test/resources/incorrect_left_right_indentations.docx");

        // THEN
        List<FormatError> errorsWithExpectedText
                = result.stream()
                .filter(fe -> Objects.equals(fe.getParagraphText(), expectedParagraphText))
                .toList();
        assertEquals(1, errorsWithExpectedText.size(), "Only one error with expected text should be found.");

        FormatError formatError = errorsWithExpectedText.getFirst();
        assertEquals(expectedParagraphText, formatError.getParagraphText());
        assertEquals(INDENTATION, formatError.getCategory());
        assertEquals(Set.of(String.format(Locale.US, "%.1f см", expectedValue)), formatError.getFound());
        assertEquals(RequirementsHolder.getParagraphSpacing() + " см", formatError.getExpected());
    }

    @Test
    void check_incorrectFormatting_formattingFromStyle_both_indentations() {
        // GIVEN
        String expectedParagraphText = "Два не правильних відступи не правильні через стилі (12 cm left and right).";
        double expectedValue = 12.0;

        //WHEN
        List<FormatError> result = checker.check("src/test/resources/incorrect_left_right_indentations.docx");

        // THEN
        List<FormatError> errorsWithExpectedText
                = result.stream()
                .filter(fe -> Objects.equals(fe.getParagraphText(), expectedParagraphText))
                .toList();
        assertEquals(2, errorsWithExpectedText.size(), "Only two errors with expected text should be found.");

        FormatError formatError = errorsWithExpectedText.getFirst();
        assertEquals(expectedParagraphText, formatError.getParagraphText());
        assertEquals(INDENTATION, formatError.getCategory());
        assertEquals(Set.of(String.format(Locale.US, "%.1f см", expectedValue)), formatError.getFound());
        assertEquals(RequirementsHolder.getParagraphSpacing() + " см", formatError.getExpected());
    }
}
