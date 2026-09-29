package com.cmps.thesischecker.checker;

import com.cmps.thesischecker.model.ErrorCategory;
import com.cmps.thesischecker.model.FormatError;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for FigureChecker.
 */
public class FigureCheckerTest extends BaseTest {

    private final Checker checker = new FigureChecker();

    @Override
    protected Checker getChecker() {
        return checker;
    }

    private static final String TEST_FILE = "src/test/resources/incorrect_figure.docx";

    @Test
    void testFigureAlignmentError() {
        // GIVEN
        List<FormatError> errors = checker.check(TEST_FILE);

        // WHEN
        List<FormatError> alignmentErrors = errors.stream()
                .filter(e -> e.getTitle().equals("Рисунок повинен бути вирівняним по центру"))
                .toList();

        // THEN
        assertEquals(1, alignmentErrors.size(), "Expected exactly one figure alignment error");
        FormatError error = alignmentErrors.getFirst();
        assertEquals(ErrorCategory.ALIGNMENT, error.getCategory());
        assertEquals("CENTER", error.getExpected());
        assertTrue(error.getFound().size() == 1);
        String found = error.getFound().iterator().next();
        assertTrue(!found.equalsIgnoreCase("CENTER"), "Found alignment should not be CENTER");
        assertTrue(!error.getParagraphText().isEmpty(), "Paragraph text should not be empty");
    }

    @Test
    void testFigureBlankLineBeforeError() {
        // GIVEN
        List<FormatError> errors = checker.check(TEST_FILE);

        // WHEN
        List<FormatError> blankLineErrors = errors.stream()
                .filter(e -> e.getTitle().equals("Перед рисунком має бути один пустий рядок"))
                .toList();

        // THEN
        assertEquals(1, blankLineErrors.size(), "Expected exactly one missing blank line before figure error");
        FormatError error = blankLineErrors.getFirst();
        assertEquals(ErrorCategory.STRUCTURAL_ELEMENT, error.getCategory());
        assertEquals("один пустий рядок", error.getExpected());
        assertTrue(error.getFound().size() == 1);
        String found = error.getFound().iterator().next();
        assertEquals("немає пустого рядка перед", found);
        assertTrue(!error.getParagraphText().isEmpty(), "Paragraph text should not be empty");
    }

    @Test
    void testFigureCaptionFormatError() {
        // GIVEN
        List<FormatError> errors = checker.check(TEST_FILE);

        // WHEN
        List<FormatError> captionFormatErrors = errors.stream()
                .filter(e -> e.getTitle().equals("Неправильний формат підпису рисунка. Очікується: «Рисунок <номер> - <назва>»"))
                .toList();

        // THEN
        assertEquals(1, captionFormatErrors.size(), "Expected exactly one caption format error");
        FormatError error = captionFormatErrors.getFirst();
        assertEquals(ErrorCategory.STRUCTURAL_ELEMENT, error.getCategory());
        assertEquals("«Рисунок <номер> - <назва>»", error.getExpected());
        assertTrue(error.getFound().size() == 1);
        String found = error.getFound().iterator().next();
        assertFalse(found.isEmpty(), "Found caption text should not be empty");
        assertTrue(!found.matches("^Рисунок\\s+\\d+\\s*-\\s+.+"), "Caption text should not match the expected pattern");
        assertTrue(!error.getParagraphText().isEmpty(), "Paragraph text should not be empty");
    }
}