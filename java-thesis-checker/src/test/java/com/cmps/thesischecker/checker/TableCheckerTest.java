package com.cmps.thesischecker.checker;

import com.cmps.thesischecker.model.ErrorCategory;
import com.cmps.thesischecker.model.FormatError;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class TableCheckerTest extends BaseTest {

    private final Checker checker = new TableChecker();

    protected Checker getChecker() {
        return checker;
    }

    @Test
    void check_correctTableFormat_noErrors() {
        List<FormatError> result = this.checker.check("src/test/resources/correct_table_text.docx");

        assertTrue(result.isEmpty(), "Expected no errors for correct table text, but got: " + result);
    }

    @Test
    void check_incorrectTableFormat_errorsFound() {
        List<FormatError> result = this.checker.check("src/test/resources/incorrect_table_text.docx");

        assertFalse(result.isEmpty(), "Expected errors in document with incorrect table text");
    }

    @Test
    void check_multipleViolations_specificErrorsFound() {
        List<FormatError> result = this.checker.check("src/test/resources/incorrect_table_text.docx");

        List<FormatError> fontSizeErrors = result.stream()
                .filter(e -> e.getCategory() == ErrorCategory.FONT_SIZE && "error".equals(e.getSeverity()))
                .collect(Collectors.toList());
        List<FormatError> fontNameWarnings = result.stream()
                .filter(e -> e.getCategory() == ErrorCategory.FONT_NAME && "warning".equals(e.getSeverity()))
                .collect(Collectors.toList());
        List<FormatError> spacingErrors = result.stream()
                .filter(e -> e.getCategory() == ErrorCategory.SPACING && "error".equals(e.getSeverity()))
                .collect(Collectors.toList());

        assertFalse(fontSizeErrors.isEmpty(), "Expected FONT_SIZE error for 10 pt font in table");
        assertFalse(fontNameWarnings.isEmpty(), "Expected FONT_NAME warning for Arial font in table");
        assertFalse(spacingErrors.isEmpty(), "Expected SPACING error for non-zero spacing in table");

        assertEquals("12pt або 14pt", fontSizeErrors.get(0).getExpected());
        assertEquals("Times New Roman", fontNameWarnings.get(0).getExpected());
        assertEquals("0 pt", spacingErrors.get(0).getExpected());
    }
}
