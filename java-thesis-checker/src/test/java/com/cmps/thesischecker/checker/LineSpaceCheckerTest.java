package com.cmps.thesischecker.checker;

import com.cmps.thesischecker.model.FormatError;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("LineSpaceChecker Tests for Test(LineSpacing).docx")
class LineSpaceCheckerTest  extends BaseTest {

    private static final String TEST_FILE = "src/test/resources/Test(LineSpacing).docx";
    private static final int EXPECTED_ERROR_COUNT = 4;
    private static final String EXPECTED_FIRST_SPACING = "1.16";
    private static final String EXPECTED_SECOND_SPACING = "1.00";
    private static final String EXPECTED_THIRD_SPACING = "2.00";
    private static final String EXPECTED_FOURTH_SPACING = "15.00";

    protected Checker getChecker() {
        return new FontChecker();
    }

    @Test
    @DisplayName("Document contains exactly 4 line spacing errors")
    void check_lineSpacing_finds4Errors() {
        LineSpaceChecker checker = new LineSpaceChecker();
        List<FormatError> result = checker.check(TEST_FILE);

        List<FormatError> lineSpacingErrors = result.stream()
                .filter(error -> "err_linespace".equals(error.getId()))
                .toList();

        assertEquals(EXPECTED_ERROR_COUNT, lineSpacingErrors.size(),
                "Expected exactly 4 line spacing errors in the document");
    }

    @Test
    @DisplayName("Plain text with default spacing auto-set to 1.16 is detected as error")
    void check_lineSpacing_firstErrorIs1Point16() {
        LineSpaceChecker checker = new LineSpaceChecker();
        List<FormatError> result = checker.check(TEST_FILE);

        List<FormatError> lineSpacingErrors = result.stream()
                .filter(error -> "err_linespace".equals(error.getId()))
                .toList();

        assertFalse(lineSpacingErrors.isEmpty(), "Expected at least 1 error");
        FormatError firstError = lineSpacingErrors.getFirst();
        assertTrue(firstError.getFound().contains(EXPECTED_FIRST_SPACING),
                "First error should contain spacing value 1.16, but found: " + firstError.getFound());
    }

    @Test
    @DisplayName("Text with spacing set to 1.00 via toolbar is detected as error")
    void check_lineSpacing_secondErrorIs1Point0() {
        LineSpaceChecker checker = new LineSpaceChecker();
        List<FormatError> result = checker.check(TEST_FILE);

        List<FormatError> lineSpacingErrors = result.stream()
                .filter(error -> "err_linespace".equals(error.getId()))
                .toList();

        assertTrue(lineSpacingErrors.size() >= 2, "Expected at least 2 errors");
        FormatError secondError = lineSpacingErrors.get(1);
        assertTrue(secondError.getFound().contains(EXPECTED_SECOND_SPACING),
                "Second error should contain spacing value 1.00, but found: " + secondError.getFound());
    }

    @Test
    @DisplayName("Text with spacing set to 2.00 via paragraph style is detected as error")
    void check_lineSpacing_thirdErrorIs2Point0() {
        LineSpaceChecker checker = new LineSpaceChecker();
        List<FormatError> result = checker.check(TEST_FILE);

        List<FormatError> lineSpacingErrors = result.stream()
                .filter(error -> "err_linespace".equals(error.getId()))
                .toList();

        assertTrue(lineSpacingErrors.size() >= 3, "Expected at least 3 errors");
        FormatError thirdError = lineSpacingErrors.get(2);
        assertTrue(thirdError.getFound().contains(EXPECTED_THIRD_SPACING),
                "Third error should contain spacing value 2.00, but found: " + thirdError.getFound());
    }

    @Test
    @DisplayName("Text with spacing set to 15pt via paragraph settings is detected as error")
    void check_lineSpacing_fourthErrorIs15() {
        LineSpaceChecker checker = new LineSpaceChecker();
        List<FormatError> result = checker.check(TEST_FILE);

        List<FormatError> lineSpacingErrors = result.stream()
                .filter(error -> "err_linespace".equals(error.getId()))
                .toList();

        assertTrue(lineSpacingErrors.size() >= 4, "Expected at least 4 errors");
        FormatError fourthError = lineSpacingErrors.get(3);
        assertTrue(fourthError.getFound().contains(EXPECTED_FOURTH_SPACING),
                "Fourth error should contain spacing value 15.00, but found: " + fourthError.getFound());
    }

    @Test
    @DisplayName("All errors have correct expected spacing value")
    void check_lineSpacing_allErrorsHaveCorrectExpectedValue() {
        LineSpaceChecker checker = new LineSpaceChecker();
        List<FormatError> result = checker.check(TEST_FILE);

        List<FormatError> lineSpacingErrors = result.stream()
                .filter(error -> "err_linespace".equals(error.getId()))
                .toList();

        assertEquals(EXPECTED_ERROR_COUNT, lineSpacingErrors.size(),
                "Expected exactly 4 line spacing errors");

        lineSpacingErrors.forEach(error -> assertEquals("1.5", error.getExpected(),
                "All errors should expect spacing of 1.5"));
    }

    @Test
    @DisplayName("All errors preserve paragraph text")
    void check_lineSpacing_allErrorsPreserveParagraphText() {
        LineSpaceChecker checker = new LineSpaceChecker();
        List<FormatError> result = checker.check(TEST_FILE);

        List<FormatError> lineSpacingErrors = result.stream()
                .filter(error -> "err_linespace".equals(error.getId()))
                .toList();

        assertEquals(EXPECTED_ERROR_COUNT, lineSpacingErrors.size(),
                "Expected exactly 4 line spacing errors");

        lineSpacingErrors.forEach(error -> {
            assertNotNull(error.getParagraphText(),
                    "Paragraph text should not be null");
            assertFalse(error.getParagraphText().isEmpty(),
                    "Paragraph text should not be empty");
        });
    }

    @Test
    @DisplayName("Errors appear in correct order: 1.16, 1.00, 2.00, 15.00")
    void check_lineSpacing_allValuesInCorrectOrder() {
        LineSpaceChecker checker = new LineSpaceChecker();
        List<FormatError> result = checker.check(TEST_FILE);

        List<FormatError> lineSpacingErrors = result.stream()
                .filter(error -> "err_linespace".equals(error.getId()))
                .toList();

        assertEquals(EXPECTED_ERROR_COUNT, lineSpacingErrors.size(),
                "Expected exactly 4 line spacing errors");

        assertTrue(lineSpacingErrors.get(0).getFound().contains(EXPECTED_FIRST_SPACING),
                "First error: expected 1.16");
        assertTrue(lineSpacingErrors.get(1).getFound().contains(EXPECTED_SECOND_SPACING),
                "Second error: expected 1.00");
        assertTrue(lineSpacingErrors.get(2).getFound().contains(EXPECTED_THIRD_SPACING),
                "Third error: expected 2.00");
        assertTrue(lineSpacingErrors.get(3).getFound().contains(EXPECTED_FOURTH_SPACING),
                "Fourth error: expected 15.00");
    }
}
