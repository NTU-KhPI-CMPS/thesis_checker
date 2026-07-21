package com.cmps.thesischecker.checker;

import com.cmps.thesischecker.model.FormatError;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Objects;

import static com.cmps.thesischecker.model.ErrorCategory.STRUCTURAL_ELEMENT_LOCATION;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class StructuralElementCheckerTest extends BaseTest {

    private final Checker checker = new StructuralElementChecker();

    @Override
    protected Checker getChecker() {
        return checker;
    }

    @Test
    void check_correctFormatting() {
        // WHEN
        List<FormatError> result = checker.check("src/test/resources/correct_structural_element_location.docx");

        // THEN
        assertTrue(result.isEmpty(), "File should not have any structural-element-location errors.");
    }

    @Test
    void check_incorrectFormatting_missingRequiredElement() {
        // WHEN
        List<FormatError> result = checker.check("src/test/resources/incorrect_structural_element_location.docx");

        // THEN
        List<FormatError> missingErrors
                = result.stream()
                        .filter(fe -> Objects.equals(fe.getId(), "err_structural_element_missing"))
                        .toList();
        assertEquals(1, missingErrors.size(), "Only one missing-element error should be found.");

        FormatError formatError = missingErrors.getFirst();
        assertEquals(STRUCTURAL_ELEMENT_LOCATION, formatError.getCategory());
        assertEquals("error", formatError.getSeverity());
        assertEquals("ВИСНОВКИ", formatError.getExpected());
    }

    @Test
    void check_incorrectFormatting_wrongOrder() {
        // WHEN
        List<FormatError> result = checker.check("src/test/resources/incorrect_structural_element_location.docx");

        // THEN
        List<FormatError> orderErrors
                = result.stream()
                        .filter(fe -> Objects.equals(fe.getId(), "err_structural_element_order"))
                        .toList();
        assertEquals(1, orderErrors.size(), "Only one order error should be found.");

        FormatError formatError = orderErrors.getFirst();
        assertEquals(STRUCTURAL_ELEMENT_LOCATION, formatError.getCategory());
        assertEquals("error", formatError.getSeverity());
        // Document has "ВСТУП" before "ЗМІСТ", while the requirement expects "ЗМІСТ" first.
        assertEquals("РЕФЕРАТ -> ЗМІСТ -> ВСТУП -> СПИСОК ДЖЕРЕЛ ІНФОРМАЦІЇ", formatError.getExpected());
        assertTrue(formatError.getFound().containsAll(
                List.of("РЕФЕРАТ", "ВСТУП", "ЗМІСТ", "СПИСОК ДЖЕРЕЛ ІНФОРМАЦІЇ")));
    }

    @Test
    void check_incorrectFormatting_doesNotStartOnNewPage() {
        // GIVEN
        String expectedParagraphText = "СПИСОК ДЖЕРЕЛ ІНФОРМАЦІЇ";

        // WHEN
        List<FormatError> result = checker.check("src/test/resources/incorrect_structural_element_location.docx");

        // THEN
        List<FormatError> pageErrors
                = result.stream()
                        .filter(fe -> Objects.equals(fe.getId(), "warn_structural_element_new_page"))
                        .toList();
        assertEquals(1, pageErrors.size(), "Only one new-page warning should be found.");

        FormatError formatError = pageErrors.getFirst();
        assertEquals(expectedParagraphText, formatError.getParagraphText());
        assertEquals(STRUCTURAL_ELEMENT_LOCATION, formatError.getCategory());
        assertEquals("warning", formatError.getSeverity());
    }

    @Test
    void check_incorrectFormatting_totalErrorCount() {
        // WHEN
        List<FormatError> result = checker.check("src/test/resources/incorrect_structural_element_location.docx");

        // THEN
        // 1 missing element ("ВИСНОВКИ") + 1 wrong order + 1 "not on a new page" warning. + 4 need to use bold style + 4 needed to center the text
        assertEquals(11, result.size(), "Expected exactly 3 structural-element-location errors in the document");
    }

    @Test
    void check_incorrectFormatting_notUsedPageBreak_usingStyles() {
        // GIVEN
        String expectedParagraphText = "ДОДАТОК";

        // WHEN
        List<FormatError> result = checker.check("src/test/resources/incorrect_structural_elements_with_style_or_without.docx");

        // THEN
        List<FormatError> errorsWithExpectedText
                = result.stream()
                .filter(fe -> Objects.equals(fe.getParagraphText(), expectedParagraphText))
                .toList();
        assertEquals(1, errorsWithExpectedText.size(), "Only one error with expected text should be found.");

        FormatError formatError = errorsWithExpectedText.getFirst();
        assertEquals(expectedParagraphText, formatError.getParagraphText());
        assertEquals(STRUCTURAL_ELEMENT_LOCATION, formatError.getCategory());
        assertEquals("warning", formatError.getSeverity());
    }

    @Test
    void check_incorrectFormatting_notUsedPageBreak_withoutStyles() {
        // GIVEN
        String expectedParagraphText = "ВИСНОВКИ";

        // WHEN
        List<FormatError> result = checker.check("src/test/resources/incorrect_structural_elements_with_style_or_without.docx");

        // THEN
        List<FormatError> errorsWithExpectedText
                = result.stream()
                .filter(fe -> Objects.equals(fe.getParagraphText(), expectedParagraphText))
                .toList();
        assertEquals(1, errorsWithExpectedText.size(), "Only one error with expected text should be found.");

        FormatError formatError = errorsWithExpectedText.getFirst();
        assertEquals(expectedParagraphText, formatError.getParagraphText());
        assertEquals(STRUCTURAL_ELEMENT_LOCATION, formatError.getCategory());
        assertEquals("warning", formatError.getSeverity());
    }

    @Test
    void check_incorrectFormatting_usedNumber_in_structuralElement() {
        // GIVEN
        String expectedId = "err_structural_element_numbered";
        String expectedParagraphText = "1 ВСТУП";

        // WHEN
        List<FormatError> result = checker.check("src/test/resources/incorrect_structural_elements_check_additional_setup.docx");

        // THEN
        List<FormatError> errorsWithExpectedId
                = result.stream()
                .filter(fe -> Objects.equals(fe.getId(), expectedId))
                .toList();
        assertEquals(1, errorsWithExpectedId.size(), "Only one error with expected ID should be found.");

        FormatError formatError = errorsWithExpectedId.getFirst();
        assertEquals(expectedParagraphText, formatError.getParagraphText());
        assertEquals(STRUCTURAL_ELEMENT_LOCATION, formatError.getCategory());
        assertEquals("error", formatError.getSeverity());
    }

    @Test
    void check_incorrectFormatting_structuralElement_withoutBoldStyle() {
        // GIVEN
        String expectedId = "err_structural_element_bold";

        // WHEN
        List<FormatError> result = checker.check("src/test/resources/incorrect_structural_elements_check_additional_setup.docx");

        // THEN
        List<FormatError> errorsWithExpectedId
                = result.stream()
                .filter(fe -> Objects.equals(fe.getId(), expectedId))
                .toList();
        assertEquals(5, errorsWithExpectedId.size(), "Five errors with expected ID should be found " +
                                                                        "for the five structural elements that are not bold.");

        FormatError formatError = errorsWithExpectedId.getFirst();
        assertEquals(STRUCTURAL_ELEMENT_LOCATION, formatError.getCategory());
        assertEquals("error", formatError.getSeverity());
    }

    @Test
    void check_incorrectFormatting_structuralElement_withItalicStyle() {
        // GIVEN
        String expectedId = "err_structural_element_italic";
        String expectedParagraphText = "СПИСОК ДЖЕРЕЛ ІНФОРМАЦІЇ";

        // WHEN
        List<FormatError> result = checker.check("src/test/resources/incorrect_structural_elements_check_additional_setup.docx");

        // THEN
        List<FormatError> errorsWithExpectedId
                = result.stream()
                .filter(fe -> Objects.equals(fe.getId(), expectedId))
                .toList();
        assertEquals(1, errorsWithExpectedId.size(), "Only one error with expected ID should be found.");

        FormatError formatError = errorsWithExpectedId.getFirst();
        assertEquals(expectedParagraphText, formatError.getParagraphText());
        assertEquals(STRUCTURAL_ELEMENT_LOCATION, formatError.getCategory());
        assertEquals("error", formatError.getSeverity());
    }

    @Test
    void check_incorrectFormatting_structuralElement_withUnderlineStyle() {
        // GIVEN
        String expectedId = "err_structural_element_underline";
        String expectedParagraphText = "ДОДАТОК";

        // WHEN
        List<FormatError> result = checker.check("src/test/resources/incorrect_structural_elements_check_additional_setup.docx");

        // THEN
        List<FormatError> errorsWithExpectedId
                = result.stream()
                .filter(fe -> Objects.equals(fe.getId(), expectedId))
                .toList();
        assertEquals(1, errorsWithExpectedId.size(), "Only one error with expected ID should be found.");

        FormatError formatError = errorsWithExpectedId.getFirst();
        assertEquals(expectedParagraphText, formatError.getParagraphText());
        assertEquals(STRUCTURAL_ELEMENT_LOCATION, formatError.getCategory());
        assertEquals("error", formatError.getSeverity());
    }

    @Test
    void check_incorrectFormatting_structuralElement_withDot_in_end() {
        // GIVEN
        String expectedId = "err_structural_element_trailing_dot";
        String expectedParagraphText = "РЕФЕРАТ.";

        // WHEN
        List<FormatError> result = checker.check("src/test/resources/incorrect_structural_elements_check_additional_setup.docx");

        // THEN
        List<FormatError> errorsWithExpectedId
                = result.stream()
                .filter(fe -> Objects.equals(fe.getId(), expectedId))
                .toList();
        assertEquals(1, errorsWithExpectedId.size(), "Only one error with expected ID should be found.");

        FormatError formatError = errorsWithExpectedId.getFirst();
        assertEquals(expectedParagraphText, formatError.getParagraphText());
        assertEquals(STRUCTURAL_ELEMENT_LOCATION, formatError.getCategory());
        assertEquals("error", formatError.getSeverity());
    }
}
