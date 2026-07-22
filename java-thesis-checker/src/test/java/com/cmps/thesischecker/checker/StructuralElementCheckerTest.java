package com.cmps.thesischecker.checker;

import com.cmps.thesischecker.model.FormatError;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Objects;

import static com.cmps.thesischecker.model.ErrorCategory.STRUCTURAL_ELEMENT;
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
        assertEquals(STRUCTURAL_ELEMENT, formatError.getCategory());
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
        assertEquals(STRUCTURAL_ELEMENT, formatError.getCategory());
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
        assertEquals(STRUCTURAL_ELEMENT, formatError.getCategory());
        assertEquals("warning", formatError.getSeverity());
    }

    @Test
    void check_incorrectFormatting_totalErrorCount() {
        // WHEN
        List<FormatError> result = checker.check("src/test/resources/incorrect_structural_element_location.docx");

        // THEN
        // 1 missing element ("ВИСНОВКИ") + 1 wrong order + 1 "not on a new page" warning.
        assertEquals(3, result.size(), "Expected exactly 3 structural-element-location errors in the document");
    }
}