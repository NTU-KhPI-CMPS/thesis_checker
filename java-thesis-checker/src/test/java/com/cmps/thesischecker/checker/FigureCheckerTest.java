package com.cmps.thesischecker.checker;

import com.cmps.thesischecker.model.FormatError;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("FigureChecker Tests for document with typical figure errors")
public class FigureCheckerTest extends BaseTest {

    private static final String TEST_FILE = "src/test/resources/incorrect_figure.docx";
    private static final int EXPECTED_ERROR_COUNT = 3; // Example: alignment, missing blank line, caption format

    private static List<FormatError> cachedErrors;

    @Override
    protected Checker getChecker() { return new FigureChecker(); }

    @BeforeAll
    static void setUp() {
        FigureChecker checker = new FigureChecker();
        cachedErrors = checker.check(TEST_FILE);
    }

    @Test
    @DisplayName("Document contains expected number of figure errors")
    void check_figure_findsExpectedErrors() {
        assertEquals(EXPECTED_ERROR_COUNT, cachedErrors.size(),
                "Expected exactly " + EXPECTED_ERROR_COUNT + " figure errors in the document");
    }

    @Test
    @DisplayName("Figure alignment error is detected")
    void check_figure_alignmentError() {
        assertFalse(cachedErrors.isEmpty(), "Expected at least 1 error");
        // We assume the first error is about alignment
        FormatError firstError = cachedErrors.getFirst();
        assertTrue(firstError.getCategory().name().equals("ALIGNMENT") ||
                        firstError.getTitle().contains("вирівня"),
                "First error should be about figure alignment");
    }

    @Test
    @DisplayName("Blank line before figure error is detected")
    void check_figure_blankLineBeforeError() {
        assertTrue(cachedErrors.size() >= 2, "Expected at least 2 errors");
        // Look for an error about blank line before figure
        boolean found = cachedErrors.stream()
                .anyMatch(error -> error.getTitle().contains("пустий рядок") &&
                        error.getTitle().contains("Перед"));
        assertTrue(found, "Expected an error about missing blank line before figure");
    }

    @Test
    @DisplayName("Caption format error is detected")
    void check_figure_captionFormatError() {
        assertTrue(cachedErrors.size() >= 3, "Expected at least 3 errors");
        // Look for an error about caption format
        boolean found = cachedErrors.stream()
                .anyMatch(error -> error.getTitle().contains("підпису") &&
                        error.getTitle().contains("формат"));
        assertTrue(found, "Expected an error about incorrect caption format");
    }

    // Additional tests can be added for other figure requirements
}