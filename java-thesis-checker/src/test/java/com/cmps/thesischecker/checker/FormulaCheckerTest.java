package com.cmps.thesischecker.checker;

import com.cmps.thesischecker.model.FormatError;
import com.cmps.thesischecker.requirements.RequirementsHolder;
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
    void check_correctFormatting() {
        // GIVEN
        String testFilePath = "src/test/resources/correct_formula.docx";

        // WHEN
        List<FormatError> result = checker.check(testFilePath);

        // THEN
        assertTrue(result.isEmpty(), "File should not have any errors related to formulas.");
    }

    @Test
    void check_incorrectFormatting_missingSpaceBefore() {
        // GIVEN
        String testFilePath = "src/test/resources/incorrect_formula.docx";
        String expectedErrorId = "err_formula_spacing_before";

        // WHEN
        List<FormatError> result = checker.check(testFilePath);

        // THEN
        List<FormatError> errorsWithExpectedText = result.stream()
                .filter(fe -> expectedErrorId.equals(fe.getId()))
                .toList();

        assertFalse(errorsWithExpectedText.isEmpty(), "Should find at least one missing space before error.");
        FormatError formatError = errorsWithExpectedText.getFirst();
        assertEquals(FORMULA, formatError.getCategory());
        assertEquals(RequirementsHolder.getFormulaSpacing(), formatError.getExpected());
    }

    @Test
    void check_incorrectFormatting_missingSpaceAfter() {
        // GIVEN
        String testFilePath = "src/test/resources/incorrect_formula.docx";
        String expectedErrorId = "err_formula_spacing_after";

        // WHEN
        List<FormatError> result = checker.check(testFilePath);

        // THEN
        List<FormatError> errorsWithExpectedText = result.stream()
                .filter(fe -> expectedErrorId.equals(fe.getId()))
                .toList();

        assertFalse(errorsWithExpectedText.isEmpty(), "Should find at least one missing space after error.");
        FormatError formatError = errorsWithExpectedText.getFirst();
        assertEquals(FORMULA, formatError.getCategory());
        assertEquals(RequirementsHolder.getFormulaSpacing(), formatError.getExpected());
    }

    @Test
    void check_incorrectFormatting_missingSpaceAfterNotation() {
        // GIVEN
        String testFilePath = "src/test/resources/incorrect_formula.docx";
        String expectedErrorId = "err_formula_notation_spacing";

        // WHEN
        List<FormatError> result = checker.check(testFilePath);

        // THEN
        List<FormatError> errorsWithExpectedText = result.stream()
                .filter(fe -> expectedErrorId.equals(fe.getId()))
                .toList();

        assertFalse(errorsWithExpectedText.isEmpty(), "Should find error for missing space after 'де...' block.");
        FormatError formatError = errorsWithExpectedText.getFirst();
        assertEquals(FORMULA, formatError.getCategory());
        assertEquals(RequirementsHolder.getFormulaSpacing(), formatError.getExpected());
    }

    @Test
    void check_incorrectFormatting_alignment() {
        // GIVEN
        String testFilePath = "src/test/resources/incorrect_formula.docx";
        String expectedTitle = "Формула вирівняна по лівому краю";

        // WHEN
        List<FormatError> result = checker.check(testFilePath);

        // THEN
        List<FormatError> errorsWithExpectedText = result.stream()
                .filter(fe -> Objects.equals(fe.getTitle(), expectedTitle))
                .toList();

        assertFalse(errorsWithExpectedText.isEmpty(), "Should find alignment error.");
        FormatError formatError = errorsWithExpectedText.getFirst();
        assertEquals(FORMULA, formatError.getCategory());
        assertEquals(Set.of("LEFT"), formatError.getFound());
        assertEquals(RequirementsHolder.getFormulaAlignment(), formatError.getExpected());
    }

    @Test
    void check_incorrectFormatting_multipleFormulasPerLine() {
        // GIVEN
        String testFilePath = "src/test/resources/incorrect_formula.docx";
        String expectedTitle = "Кілька формул в одному рядку";

        // WHEN
        List<FormatError> result = checker.check(testFilePath);

        // THEN
        List<FormatError> errorsWithExpectedText = result.stream()
                .filter(fe -> Objects.equals(fe.getTitle(), expectedTitle))
                .toList();

        assertFalse(errorsWithExpectedText.isEmpty(), "Should find multiple formulas per line error.");
        FormatError formatError = errorsWithExpectedText.getFirst();
        assertEquals(FORMULA, formatError.getCategory());
        assertEquals(RequirementsHolder.getFormulaMultiplePerLine(), formatError.getExpected());
    }

    @Test
    void check_incorrectFormatting_fontSize() {
        // GIVEN
        String testFilePath = "src/test/resources/incorrect_formula.docx";
        String expectedId = "err_formula_font_size";

        // WHEN
        List<FormatError> result = checker.check(testFilePath);

        // THEN
        List<FormatError> errorsWithExpectedText = result.stream()
                .filter(fe -> expectedId.equals(fe.getId()))
                .toList();

        assertFalse(errorsWithExpectedText.isEmpty(), "Should find font size error.");
        FormatError formatError = errorsWithExpectedText.getFirst();
        assertEquals(FORMULA, formatError.getCategory());
        assertTrue(formatError.getTitle().contains("Неправильний розмір шрифту у формулі (на фрагменті"));
        assertEquals(RequirementsHolder.getFontSize() + "pt", formatError.getExpected());
    }

    @Test
    void check_incorrectFormatting_chapterMismatch() {
        // GIVEN
        String testFilePath = "src/test/resources/incorrect_formula.docx";
        String expectedId = "err_formula_chapter_mismatch";

        // WHEN
        List<FormatError> result = checker.check(testFilePath);

        // THEN
        List<FormatError> errorsWithExpectedText = result.stream()
                .filter(fe -> expectedId.equals(fe.getId()))
                .toList();

        assertFalse(errorsWithExpectedText.isEmpty(), "Should find chapter mismatch error.");
        FormatError formatError = errorsWithExpectedText.getFirst();
        assertEquals(FORMULA, formatError.getCategory());
    }

    @Test
    void check_incorrectFormatting_sequence() {
        // GIVEN
        String testFilePath = "src/test/resources/incorrect_formula.docx";
        String expectedId = "err_formula_sequence";

        // WHEN
        List<FormatError> result = checker.check(testFilePath);

        // THEN
        List<FormatError> errorsWithExpectedText = result.stream()
                .filter(fe -> expectedId.equals(fe.getId()))
                .toList();

        assertFalse(errorsWithExpectedText.isEmpty(), "Should find sequence error.");
        FormatError formatError = errorsWithExpectedText.getFirst();
        assertEquals(FORMULA, formatError.getCategory());
    }

    @Test
    void check_incorrectFormatting_format() {
        // GIVEN
        String testFilePath = "src/test/resources/incorrect_formula.docx";
        String expectedId = "err_formula_format";

        // WHEN
        List<FormatError> result = checker.check(testFilePath);

        // THEN
        List<FormatError> errorsWithExpectedText = result.stream()
                .filter(fe -> expectedId.equals(fe.getId()))
                .toList();

        assertFalse(errorsWithExpectedText.isEmpty(), "Should find numbering format error.");
        FormatError formatError = errorsWithExpectedText.getFirst();
        assertEquals(FORMULA, formatError.getCategory());
    }
}
