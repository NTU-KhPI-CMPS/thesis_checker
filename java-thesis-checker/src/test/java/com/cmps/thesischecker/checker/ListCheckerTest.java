package com.cmps.thesischecker.checker;

import com.cmps.thesischecker.model.FormatError;
import com.cmps.thesischecker.model.NumberingFormat;
import com.cmps.thesischecker.requirements.RequirementsHolder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import static com.cmps.thesischecker.model.ErrorCategory.LIST_FORMATTING;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("List Formatting Checker Tests")
public class ListCheckerTest extends BaseTest {

    private final Checker checker = new ListChecker();

    @Override
    protected Checker getChecker() {
        return checker;
    }

    @Test
    @DisplayName("Should find no errors in a correctly formatted document")
    void check_correctFormatting() {
        //WHEN
        List<FormatError> result = checker.check("src/test/resources/correct_list_formatting.docx");

        // THEN
        assertTrue(result.isEmpty(), "File should not have any errors related to list formatting.");
    }

    @Test
    @DisplayName("Should detect error when a list level is skipped")
    void check_incorrectFormatting_skippedLevel() {
        // GIVEN
        String expectedParagraphText = "Пункт, що пропускає другий рівень і одразу переходить на третій.";

        //WHEN
        List<FormatError> result = checker.check("src/test/resources/incorrect_list_formatting.docx");

        // THEN
        List<FormatError> errorsWithExpectedText
                = result.stream()
                .filter(fe -> Objects.equals(fe.getParagraphText(), expectedParagraphText))
                .toList();
        assertEquals(1, errorsWithExpectedText.size(), "Only one error with expected text should be found.");

        FormatError formatError = errorsWithExpectedText.getFirst();
        assertEquals(expectedParagraphText, formatError.getParagraphText());
        assertEquals(LIST_FORMATTING, formatError.getCategory());
        assertEquals(Set.of("2"), formatError.getFound());
        assertEquals(RequirementsHolder.getListLevelStep(), formatError.getExpected());
    }

    @Test
    @DisplayName("Should detect error when an incorrect bullet character is used")
    void check_incorrectFormatting_wrongBulletCharacter() {
        // GIVEN
        String expectedParagraphText = "Пункт першого рівня, неправильний символ.";

        //WHEN
        List<FormatError> result = checker.check("src/test/resources/incorrect_list_formatting.docx");

        // THEN
        List<FormatError> errorsWithExpectedText
                = result.stream()
                .filter(fe -> Objects.equals(fe.getParagraphText(), expectedParagraphText))
                .toList();
        assertEquals(1, errorsWithExpectedText.size(), "Only one error with expected text should be found.");

        FormatError formatError = errorsWithExpectedText.getFirst();
        assertEquals(expectedParagraphText, formatError.getParagraphText());
        assertEquals(LIST_FORMATTING, formatError.getCategory());
        assertEquals(Set.of("\uF0B7"), formatError.getFound());
        assertEquals(RequirementsHolder.getListBulletChar(), formatError.getExpected());
    }

    @Test
    @DisplayName("Should detect error when an incorrect closing symbol is used")
    void check_incorrectFormatting_wrongClosingSymbol() {
        // GIVEN
        String expectedParagraphText = "Перший пункт переліку, цифра з крапкою.";

        //WHEN
        List<FormatError> result = checker.check("src/test/resources/incorrect_list_formatting.docx");

        // THEN
        List<FormatError> errorsWithExpectedText
                = result.stream()
                .filter(fe -> Objects.equals(fe.getParagraphText(), expectedParagraphText))
                .toList();
        assertEquals(1, errorsWithExpectedText.size(), "Only one error with expected text should be found.");

        FormatError formatError = errorsWithExpectedText.getFirst();
        assertEquals(expectedParagraphText, formatError.getParagraphText());
        assertEquals(LIST_FORMATTING, formatError.getCategory());
        assertEquals(Set.of("."), formatError.getFound());
        assertEquals(RequirementsHolder.getListMarkerClosingSymbol(), formatError.getExpected());
    }

    @Test
    @DisplayName("Should detect error when use roman numerals")
    void check_incorrectFormatting_arabicNumerals() {
        // GIVEN
        String expectedParagraphText = "Перший пункт переліку, римські числа.";

        //WHEN
        List<FormatError> result = checker.check("src/test/resources/incorrect_list_formatting.docx");

        // THEN
        List<FormatError> errorsWithExpectedText
                = result.stream()
                .filter(fe -> Objects.equals(fe.getParagraphText(), expectedParagraphText))
                .toList();
        assertEquals(1, errorsWithExpectedText.size(), "Only one error with expected text should be found.");

        FormatError formatError = errorsWithExpectedText.getFirst();
        assertEquals(expectedParagraphText, formatError.getParagraphText());
        assertEquals(LIST_FORMATTING, formatError.getCategory());
        assertEquals(Set.of(NumberingFormat.UPPER_ROMAN.getDisplayName()), formatError.getFound());
        assertEquals(RequirementsHolder.getListAllowedMarkerFormats(), formatError.getExpected());
    }
}
