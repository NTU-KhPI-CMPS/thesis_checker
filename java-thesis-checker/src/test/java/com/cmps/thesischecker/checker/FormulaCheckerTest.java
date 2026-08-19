package com.cmps.thesischecker.checker;

import com.cmps.thesischecker.model.FormatError;
import com.cmps.thesischecker.requirements.RequirementsHolder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import static com.cmps.thesischecker.model.ErrorCategory.FORMULA;
import static org.junit.jupiter.api.Assertions.*;

public class FormulaCheckerTest extends BaseTest {

    private final Checker checker = new FormulaChecker();

    @Override
    protected Checker getChecker() {
        return checker;
    }

    @Test
    @DisplayName("Verify correct formula formatting")
    void check_correctFormatting() {
        // GIVEN
        String testFilePath = "src/test/resources/correct_formula.docx";

        // WHEN
        List<FormatError> result = checker.check(testFilePath);

        // THEN
        assertTrue(result.isEmpty(), "File contains no formula formatting errors.");
    }

    @Test
    @DisplayName("Detect missing blank line before formula")
    void check_incorrectFormatting_missingSpaceBefore() {
        // GIVEN
        String testFilePath = "src/test/resources/incorrect_formula.docx";
        String expectedErrorId = "err_formula_spacing_before";

        // WHEN
        List<FormatError> result = checker.check(testFilePath);

        // THEN
        List<FormatError> errorsWithExpectedId = result.stream()
                .filter(fe -> expectedErrorId.equals(fe.getId()))
                .toList();

        assertFalse(errorsWithExpectedId.isEmpty(), "Failed to detect missing spacing before the formula.");
        FormatError formatError = errorsWithExpectedId.getFirst();
        assertEquals(FORMULA, formatError.getCategory());
        assertEquals("Порожній рядок перед формулою", formatError.getExpected());
    }

    @Test
    @DisplayName("Detect missing blank line after formula")
    void check_incorrectFormatting_missingSpaceAfter() {
        // GIVEN
        String testFilePath = "src/test/resources/incorrect_formula.docx";
        String expectedErrorId = "err_formula_spacing_after";

        // WHEN
        List<FormatError> result = checker.check(testFilePath);

        // THEN
        List<FormatError> errorsWithExpectedId = result.stream()
                .filter(fe -> expectedErrorId.equals(fe.getId()))
                .toList();

        assertFalse(errorsWithExpectedId.isEmpty(), "Failed to detect missing spacing after the formula.");
        FormatError formatError = errorsWithExpectedId.getFirst();
        assertEquals(FORMULA, formatError.getCategory());
        assertEquals("Порожній рядок після формули", formatError.getExpected());
    }

    @Test
    @DisplayName("Detect missing blank line after notation block")
    void check_incorrectFormatting_missingSpaceAfterNotation() {
        // GIVEN
        String testFilePath = "src/test/resources/incorrect_formula.docx";
        String expectedErrorId = "err_formula_notation_spacing";

        // WHEN
        List<FormatError> result = checker.check(testFilePath);

        // THEN
        List<FormatError> errorsWithExpectedId = result.stream()
                .filter(fe -> expectedErrorId.equals(fe.getId()))
                .toList();

        assertFalse(errorsWithExpectedId.isEmpty(), "Failed to detect missing spacing after the notation block.");
        FormatError formatError = errorsWithExpectedId.getFirst();
        assertEquals(FORMULA, formatError.getCategory());
        assertEquals("Порожній рядок після блоку пояснень", formatError.getExpected());
    }

    @Test
    @DisplayName("Detect incorrect formula alignment")
    void check_incorrectFormatting_alignment() {
        // GIVEN
        String testFilePath = "src/test/resources/incorrect_formula.docx";
        String expectedTitle = "Формула вирівняна по лівому краю";

        // WHEN
        List<FormatError> result = checker.check(testFilePath);

        // THEN
        List<FormatError> errorsWithExpectedId = result.stream()
                .filter(fe -> Objects.equals(fe.getTitle(), expectedTitle))
                .toList();

        assertFalse(errorsWithExpectedId.isEmpty(), "Failed to detect incorrect formula alignment.");
        FormatError formatError = errorsWithExpectedId.getFirst();
        assertEquals(FORMULA, formatError.getCategory());
        assertEquals(Set.of("LEFT"), formatError.getFound());
        assertEquals(RequirementsHolder.getFormulaAlignment(), formatError.getExpected());
    }

    @Test
    @DisplayName("Detect multiple formulas in a single line")
    void check_incorrectFormatting_multipleFormulasPerLine() {
        // GIVEN
        String testFilePath = "src/test/resources/incorrect_formula.docx";
        String expectedTitle = "Кілька формул в одному рядку";

        // WHEN
        List<FormatError> result = checker.check(testFilePath);

        // THEN
        List<FormatError> errorsWithExpectedId = result.stream()
                .filter(fe -> Objects.equals(fe.getTitle(), expectedTitle))
                .toList();

        assertFalse(errorsWithExpectedId.isEmpty(), "Failed to detect multiple formulas in one line.");
        FormatError formatError = errorsWithExpectedId.getFirst();
        assertEquals(FORMULA, formatError.getCategory());
        assertEquals(RequirementsHolder.getFormulaMultiplePerLine(), formatError.getExpected());
    }

    @Test
    @DisplayName("Detect formula font size mismatch")
    void check_incorrectFormatting_fontSize() {
        // GIVEN
        String testFilePath = "src/test/resources/incorrect_formula.docx";
        String expectedId = "err_formula_font_size";

        // WHEN
        List<FormatError> result = checker.check(testFilePath);

        // THEN
        List<FormatError> errorsWithExpectedId = result.stream()
                .filter(fe -> expectedId.equals(fe.getId()))
                .toList();

        assertFalse(errorsWithExpectedId.isEmpty(), "Failed to detect incorrect formula font size.");
        FormatError formatError = errorsWithExpectedId.getFirst();
        assertEquals(FORMULA, formatError.getCategory());
        assertTrue(formatError.getTitle().contains("Неправильний розмір шрифту у формулі"));
        assertEquals(RequirementsHolder.getFontSize() + "pt", formatError.getExpected());
    }

    @Test
    @DisplayName("Detect formula chapter prefix mismatch")
    void check_incorrectFormatting_chapterMismatch() {
        // GIVEN
        String testFilePath = "src/test/resources/incorrect_formula.docx";
        String expectedId = "err_formula_chapter_mismatch";

        // WHEN
        List<FormatError> result = checker.check(testFilePath);

        // THEN
        List<FormatError> errorsWithExpectedId = result.stream()
                .filter(fe -> expectedId.equals(fe.getId()))
                .toList();

        assertFalse(errorsWithExpectedId.isEmpty(), "Failed to detect formula and active chapter mismatch.");
        FormatError formatError = errorsWithExpectedId.getFirst();
        assertEquals(FORMULA, formatError.getCategory());
    }

    @Test
    @DisplayName("Detect broken formula sequence numbering")
    void check_incorrectFormatting_sequence() {
        // GIVEN
        String testFilePath = "src/test/resources/incorrect_formula.docx";
        String expectedId = "err_formula_sequence";

        // WHEN
        List<FormatError> result = checker.check(testFilePath);

        // THEN
        List<FormatError> errorsWithExpectedId = result.stream()
                .filter(fe -> expectedId.equals(fe.getId()))
                .toList();

        assertFalse(errorsWithExpectedId.isEmpty(), "Failed to detect broken numbering sequence.");
        FormatError formatError = errorsWithExpectedId.getFirst();
        assertEquals(FORMULA, formatError.getCategory());
    }

    @Test
    @DisplayName("Detect malformed formula number layout")
    void check_incorrectFormatting_format() {
        // GIVEN
        String testFilePath = "src/test/resources/incorrect_formula.docx";
        String expectedId = "err_formula_format";

        // WHEN
        List<FormatError> result = checker.check(testFilePath);

        // THEN
        List<FormatError> errorsWithExpectedId = result.stream()
                .filter(fe -> expectedId.equals(fe.getId()))
                .toList();

        assertFalse(errorsWithExpectedId.isEmpty(), "Failed to detect incorrect formula number format.");
        FormatError formatError = errorsWithExpectedId.getFirst();
        assertEquals(FORMULA, formatError.getCategory());
    }
}
