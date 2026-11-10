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

    @Test
    @DisplayName("Should detect warning when list builded manually")
    void check_incorrectFormatting_manualList() {
        // GIVEN
        String firstExpectedParagraphText = "1) Ручне форматування списку, перше";
        String secondExpectedParagraphText = "2. Ручне форматування списку, друге";
        String thirdExpectedParagraphText = "а) Ручне, форматування списку, третє";

        // WHEN
        List<FormatError> result = checker.check("src/test/resources/incorrect_list_formatting.docx");

        // THEN
        List<FormatError> errorsWithExpectedText
                = result.stream()
                .filter(fe -> Objects.equals(fe.getParagraphText(), firstExpectedParagraphText)
                        || Objects.equals(fe.getParagraphText(), secondExpectedParagraphText)
                        || Objects.equals(fe.getParagraphText(), thirdExpectedParagraphText))
                .toList();
        assertEquals(3, errorsWithExpectedText.size(), "Three warnings with expected text should be found.");
    }

    @Test
    @DisplayName("Should accept plain numbers without brackets or periods in the bibliography")
    void check_correctBibliography_plainNumbers() {
        // WHEN
        List<FormatError> result = checker.check("src/test/resources/correct_bibliography_list.docx");

        // THEN
        assertTrue(result.isEmpty(),
                "Plain numbers in the bibliography and \"1)\" lists elsewhere must not produce errors.");
    }

    @Test
    @DisplayName("Should detect numbers with a period or bracket in the bibliography")
    void check_incorrectBibliography_periodAndBracket() {
        // WHEN
        List<FormatError> result = checker.check("src/test/resources/incorrect_bibliography_list.docx");

        // THEN
        assertEquals(4, result.size(), "One error per distinct wrong marker (auto and manual) is expected.");
        result.forEach(error -> {
            assertEquals("err_bibliography_marker", error.getId());
            assertEquals(LIST_FORMATTING, error.getCategory());
            assertEquals("error", error.getSeverity());
            assertEquals(RequirementsHolder.getBibliographyMarkerFormat(), error.getExpected());
        });

        assertBibliographyError(result, "Перше джерело, нумерація з крапкою.", "1.");
        assertBibliographyError(result, "Третє джерело, нумерація з дужкою.", "1)");
        assertBibliographyError(result, "1. Четверте джерело, номер з крапкою надруковано вручну.", "1.");
        assertBibliographyError(result, "3) Шосте джерело, номер з дужкою надруковано вручну.", "1)");
    }

    @Test
    @DisplayName("Should detect a number without a bracket when items do not start with a capital letter")
    void check_incorrectList_numberWithoutBracket() {
        // WHEN
        List<FormatError> result = checker.check("src/test/resources/incorrect_list_without_bracket.docx");

        // THEN
        assertEquals(1, result.size(), "The list level must be reported once, not per item.");
        FormatError error = result.getFirst();
        assertEquals("err_list_marker_no_bracket", error.getId());
        assertEquals(LIST_FORMATTING, error.getCategory());
        assertEquals("1231", error.getParagraphText());
        assertEquals(Set.of("1"), error.getFound());
        assertEquals(RequirementsHolder.getListMarkerClosingSymbol(), error.getExpected());
    }

    @Test
    @DisplayName("Should accept a number without a bracket for items that are sentences with a capital letter")
    void check_correctList_fullSentencesWithoutBracket() {
        // WHEN
        List<FormatError> result = checker.check("src/test/resources/correct_list_full_sentences.docx");

        // THEN
        assertTrue(result.isEmpty(), "п. 6.2.9: capitalised sentences may be numbered without a bracket.");
    }

    private static void assertBibliographyError(List<FormatError> errors, String paragraphText, String foundMarker) {
        FormatError error = errors.stream()
                .filter(fe -> Objects.equals(fe.getParagraphText(), paragraphText))
                .findFirst()
                .orElseThrow(() -> new AssertionError("No error for paragraph: " + paragraphText));
        assertEquals(Set.of(foundMarker), error.getFound());
    }
}
