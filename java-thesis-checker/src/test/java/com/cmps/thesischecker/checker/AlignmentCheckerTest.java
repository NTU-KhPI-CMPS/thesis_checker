package com.cmps.thesischecker.checker;

import com.cmps.thesischecker.model.FormatError;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("AlignmentChecker Tests for document with typical alignment errors")
public class AlignmentCheckerTest extends BaseTest {

    private static final String TEST_FILE = "src/test/resources/incorrect_alignment.docx";
    private static final int EXPECTED_ERROR_COUNT = 4;
    private static final String EXPECTED_FIRST_ALIGNMENT = "LEFT";
    private static final String EXPECTED_SECOND_ALIGNMENT = "CENTER";
    private static final String EXPECTED_THIRD_ALIGNMENT = "RIGHT";
    private static final String EXPECTED_FOURTH_ALIGNMENT = "RIGHT";

    @Override
    protected Checker getChecker() { return new AlignmentChecker(); }

    @Test
    @DisplayName("Document contains exactly 4 alignment errors")
    void check_alignment_finds4Errors() {
        AlignmentChecker checker = new AlignmentChecker();
        List<FormatError> result = checker.check(TEST_FILE);

        List<FormatError> alignmentErrors = result.stream()
                .filter(error -> "err_alignment".equals(error.getId()))
                .toList();

        assertEquals(EXPECTED_ERROR_COUNT, alignmentErrors.size(),
                "Expected exactly 4 alignment errors in the document");
    }

    @Test
    @DisplayName("Text with no specified alignment (defaults to LEFT)")
    void check_alignment_firstErrorIsLeft() {
        AlignmentChecker checker = new AlignmentChecker();
        List<FormatError> result = checker.check(TEST_FILE);

        List<FormatError> alignmentErrors = result.stream()
                .filter(error -> "err_alignment".equals(error.getId()))
                .toList();

        assertFalse(alignmentErrors.isEmpty(), "Expected at least 1 error");
        FormatError firstError = alignmentErrors.getFirst();
        assertTrue(firstError.getFound().contains(EXPECTED_FIRST_ALIGNMENT),
                "First error should be default LEFT alignment, but found: " + firstError.getFound());
    }

    @Test
    @DisplayName("Alignment set to CENTER via top toolbar")
    void check_alignment_secondErrorIsCenter() {
        AlignmentChecker checker = new AlignmentChecker();
        List<FormatError> result = checker.check(TEST_FILE);

        List<FormatError> alignmentErrors = result.stream()
                .filter(error -> "err_alignment".equals(error.getId()))
                .toList();

        assertTrue(alignmentErrors.size() >= 2, "Expected at least 2 errors");
        FormatError secondError = alignmentErrors.get(1);
        assertTrue(secondError.getFound().contains(EXPECTED_SECOND_ALIGNMENT),
                "Second error should be CENTER alignment, but found: " + secondError.getFound());
    }

    @Test
    @DisplayName("Alignment set to RIGHT via paragraph style")
    void check_alignment_thirdErrorIsViaStyles() {
        AlignmentChecker checker = new AlignmentChecker();
        List<FormatError> result = checker.check(TEST_FILE);

        List<FormatError> alignmentErrors = result.stream()
                .filter(error -> "err_alignment".equals(error.getId()))
                .toList();

        assertTrue(alignmentErrors.size() >= 3, "Expected at least 3 errors");
        FormatError thirdError = alignmentErrors.get(2);
        assertTrue(thirdError.getFound().contains(EXPECTED_THIRD_ALIGNMENT),
                "Third error should match alignment from style, but found: " + thirdError.getFound());
    }

    @Test
    @DisplayName("Alignment set to RIGHT via inherited paragraph style")
    void check_alignment_fourthErrorIsViaInheritedStyles() {
        AlignmentChecker checker = new AlignmentChecker();
        List<FormatError> result = checker.check(TEST_FILE);

        List<FormatError> alignmentErrors = result.stream()
                .filter(error -> "err_alignment".equals(error.getId()))
                .toList();

        assertTrue(alignmentErrors.size() >= 4, "Expected at least 4 errors");
        FormatError fourthError = alignmentErrors.get(3);
        assertTrue(fourthError.getFound().contains(EXPECTED_FOURTH_ALIGNMENT),
                "Fourth error should match alignment from inherited style, but found: " + fourthError.getFound());
    }

    @Test
    @DisplayName("All errors preserve paragraph text")
    void check_alignment_allErrorsPreserveParagraphText() {
        AlignmentChecker checker = new AlignmentChecker();
        List<FormatError> result = checker.check(TEST_FILE);

        List<FormatError> alignmentErrors = result.stream()
                .filter(error -> "err_alignment".equals(error.getId()))
                .toList();

        assertEquals(EXPECTED_ERROR_COUNT, alignmentErrors.size(),
                "Expected exactly 4 alignment errors");

        alignmentErrors.forEach(error -> {
            assertNotNull(error.getParagraphText(),
                    "Paragraph text should not be null");
            assertFalse(error.getParagraphText().isEmpty(),
                    "Paragraph text should not be empty");
        });
    }

    @Test
    @DisplayName("All errors have correct expected alignment value (DISTRIBUTE)")
    void check_alignment_allErrorsHaveCorrectExpectedValue() {
        AlignmentChecker checker = new AlignmentChecker();
        List<FormatError> result = checker.check(TEST_FILE);

        List<FormatError> alignmentErrors = result.stream()
                .filter(error -> "err_alignment".equals(error.getId()))
                .toList();

        assertEquals(EXPECTED_ERROR_COUNT, alignmentErrors.size(),
                "Expected exactly 4 alignment errors");

        alignmentErrors.forEach(error -> assertEquals("DISTRIBUTE", error.getExpected(),
                "All errors should expect alignment of DISTRIBUTE"));
    }
}
