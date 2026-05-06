package com.cmps.thesischecker.checker;

import com.cmps.thesischecker.model.FormatError;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("AlignmentChecker Tests for document with typical alignment errors")
public class AlignmentCheckerTest extends BaseTest {

    private static final String TEST_FILE = "src/test/resources/incorrect_alignment.docx";
    private static final int EXPECTED_ERROR_COUNT = 4;
    private static final String EXPECTED_FIRST_ALIGNMENT = "По лівому краю";
    private static final String EXPECTED_SECOND_ALIGNMENT = "По центру";
    private static final String EXPECTED_THIRD_ALIGNMENT = "По правому краю";
    private static final String EXPECTED_FOURTH_ALIGNMENT = "По правому краю";

    private static List<FormatError> cachedErrors;

    @Override
    protected Checker getChecker() { return new AlignmentChecker(); }

    @BeforeAll
    static void setUp() {
        AlignmentChecker checker = new AlignmentChecker();
        cachedErrors = checker.check(TEST_FILE);
    }

    @Test
    @DisplayName("Document contains exactly 4 alignment errors")
    void check_alignment_finds4Errors() {
        assertEquals(EXPECTED_ERROR_COUNT, cachedErrors.size(),
                "Expected exactly 4 alignment errors in the document");
    }

    @Test
    @DisplayName("Text with no specified alignment (defaults to LEFT)")
    void check_alignment_firstErrorIsLeft() {
        assertFalse(cachedErrors.isEmpty(), "Expected at least 1 error");
        FormatError firstError = cachedErrors.getFirst();
        assertTrue(firstError.getFound().contains(EXPECTED_FIRST_ALIGNMENT),
                "First error should be default LEFT alignment, but found: " + firstError.getFound());
    }

    @Test
    @DisplayName("Alignment set to CENTER via top toolbar")
    void check_alignment_secondErrorIsCenter() {
        assertTrue(cachedErrors.size() >= 2, "Expected at least 2 errors");
        FormatError secondError = cachedErrors.get(1);
        assertTrue(secondError.getFound().contains(EXPECTED_SECOND_ALIGNMENT),
                "Second error should be CENTER alignment, but found: " + secondError.getFound());
    }

    @Test
    @DisplayName("Alignment set to RIGHT via paragraph style")
    void check_alignment_thirdErrorIsViaStyles() {
        assertTrue(cachedErrors.size() >= 3, "Expected at least 3 errors");
        FormatError thirdError = cachedErrors.get(2);
        assertTrue(thirdError.getFound().contains(EXPECTED_THIRD_ALIGNMENT),
                "Third error should match alignment from style, but found: " + thirdError.getFound());
    }

    @Test
    @DisplayName("Alignment set to RIGHT via inherited paragraph style")
    void check_alignment_fourthErrorIsViaInheritedStyles() {
        assertTrue(cachedErrors.size() >= 4, "Expected at least 4 errors");
        FormatError fourthError = cachedErrors.get(3);
        assertTrue(fourthError.getFound().contains(EXPECTED_FOURTH_ALIGNMENT),
                "Fourth error should match alignment from inherited style, but found: " + fourthError.getFound());
    }

    @Test
    @DisplayName("All errors preserve paragraph text")
    void check_alignment_allErrorsPreserveParagraphText() {
        assertEquals(EXPECTED_ERROR_COUNT, cachedErrors.size(),
                "Expected exactly 4 alignment errors");

        cachedErrors.forEach(error -> {
            assertNotNull(error.getParagraphText(),
                    "Paragraph text should not be null");
            assertFalse(error.getParagraphText().isEmpty(),
                    "Paragraph text should not be empty");
        });
    }

    @Test
    @DisplayName("All errors have correct expected alignment value (BOTH and CENTER)")
    void check_alignment_allErrorsHaveCorrectExpectedValue() {
        assertEquals(EXPECTED_ERROR_COUNT, cachedErrors.size(),
                "Expected exactly 4 alignment errors");

        for (int i = 0; i < cachedErrors.size(); i++) {
            FormatError error = cachedErrors.get(i);
            if (i < 3) {
                assertEquals("По ширині", error.getExpected(),
                        "The first 3 errors should expect alignment of Both (По ширині)");
            } else {
                assertEquals("По центру", error.getExpected(),
                        "The last error should expect alignment of Center (По центру)");
            }
        }
    }
}
