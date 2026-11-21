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
    @DisplayName("Detect missing notation block after formula")
    void check_incorrectFormatting_missingNotationBlock() {
        // GIVEN
        String testFilePath = "src/test/resources/incorrect_formula.docx";
        String expectedErrorId = "err_formula_notation_missing";

        // WHEN
        List<FormatError> result = checker.check(testFilePath);

        // THEN
        List<FormatError> errorsWithExpectedId = result.stream()
                .filter(fe -> expectedErrorId.equals(fe.getId()))
                .toList();

        assertFalse(errorsWithExpectedId.isEmpty(), "Failed to detect missing formula notation block.");
        FormatError formatError = errorsWithExpectedId.getFirst();
        assertEquals(FORMULA, formatError.getCategory());
        assertEquals(Set.of("Блок пояснень «де» відсутній"), formatError.getFound());
        assertEquals("Блок пояснень «де» після формули", formatError.getExpected());
    }

    @Test
    @DisplayName("Detect incorrect formula alignment")
    void check_incorrectFormatting_alignment() {
        // GIVEN
        String testFilePath = "src/test/resources/incorrect_formula.docx";
        String expectedTitle = "Не правильно вирівняна формула";

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

        assertFalse(errorsWithExpectedId.isEmpty(), "Failed to detect incorrect formula font size.");
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

    @Test
    @DisplayName("Detect formula with missing number")
    void check_incorrectFormatting_missingNumber() {
        // GIVEN
        String testFilePath = "src/test/resources/incorrect_formula.docx";
        String expectedId = "err_formula_sequence";
        String expectedParagraphText = "Швидкість тіла визначається за формулою V = S/t (1.5).";

        // WHEN
        List<FormatError> result = checker.check(testFilePath);

        // THEN
        List<FormatError> errorsWithExpectedText = result.stream()
                .filter(fe -> expectedId.equals(fe.getId()) && expectedParagraphText.equals(fe.getParagraphText()))
                .toList();

        assertFalse(errorsWithExpectedText.isEmpty(), "Failed to detect a formula with missing number.");
        FormatError formatError = errorsWithExpectedText.getFirst();
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
    @DisplayName("Recognize manual formula without comma before notation block")
    void check_manualFormula_withoutCommaAndWithNotationOnNextParagraph() {
        // GIVEN
        String testFilePath = "src/test/resources/manual_formula.docx";
        String formulaParagraph =
                "Швидкість руху визначається за формулою V = S/t (2.1)";

        // WHEN
        List<FormatError> result = checker.check(testFilePath);

        // THEN
        List<FormatError> numberingErrors = result.stream()
                .filter(fe -> Set.of(
                        "err_formula_chapter_mismatch",
                        "err_formula_sequence",
                        "err_formula_format"
                ).contains(fe.getId()))
                .filter(fe -> formulaParagraph.equals(fe.getParagraphText()))
                .toList();

        assertTrue(numberingErrors.isEmpty(),
                "Correct manual formula number should not produce numbering errors.");
    }

    @Test
    @DisplayName("Do not recognize decimal number as a manual formula")
    void check_plainText_decimalNumber_notRecognizedAsFormula() {
        // GIVEN
        String testFilePath = "src/test/resources/manual_formula.docx";
        String paragraphText =
                "Кожен тест повертає p-value, що дозволяє кількісно визначити якість генератора на заданому рівні значущості (0.01).";

        // WHEN
        List<FormatError> result = checker.check(testFilePath);

        // THEN
        List<FormatError> formulaNumberErrors = result.stream()
                .filter(fe -> Set.of(
                        "err_formula_chapter_mismatch",
                        "err_formula_sequence",
                        "err_formula_format"
                ).contains(fe.getId()))
                .filter(fe -> paragraphText.equals(fe.getParagraphText()))
                .toList();

        assertTrue(formulaNumberErrors.isEmpty(),
                "Decimal number (0.01) should not be recognized as a formula number.");
    }

    // Inline formulas, numbers typed outside of the formula object, formulas inserted as pictures
    private static final Set<String> NUMBERING_ERROR_IDS = Set.of(
            "err_formula_chapter_mismatch",
            "err_formula_sequence",
            "err_formula_format"
    );

    private List<FormatError> numberingErrors(List<FormatError> result) {
        return result.stream()
                .filter(fe -> NUMBERING_ERROR_IDS.contains(fe.getId()))
                .toList();
    }

    private List<FormatError> errorsWithId(List<FormatError> result, String id) {
        return result.stream()
                .filter(fe -> id.equals(fe.getId()))
                .toList();
    }

    private boolean textContains(FormatError error, String fragment) {
        return error.getParagraphText() != null && error.getParagraphText().contains(fragment);
    }

    @Test
    @DisplayName("Inline formulas inside sentences are not checked for a formula number")
    void check_inlineFormulas_noNumberingErrors() {
        // GIVEN
        String testFilePath = "src/test/resources/correct_formula_inline.docx";

        // WHEN
        List<FormatError> result = checker.check(testFilePath);

        // THEN
        assertTrue(numberingErrors(result).isEmpty(),
                "Inline formulas such as the period (2^19937 - 1) must not be treated as numbered formulas.");
    }

    @Test
    @DisplayName("Brackets of an inline formula are not mistaken for a formula number")
    void check_inlineFormula_bracketsNotRecognizedAsNumber() {
        // GIVEN
        String testFilePath = "src/test/resources/correct_formula_inline.docx";

        // WHEN
        List<FormatError> result = checker.check(testFilePath);

        // THEN
        List<FormatError> errors = numberingErrors(result).stream()
                .filter(fe -> textContains(fe, "Мерсенна"))
                .toList();

        assertTrue(errors.isEmpty(), "The sentence about the Mersenne prime has no formula number.");
    }

    @Test
    @DisplayName("Number typed after the formula object is accepted and its own brackets are ignored")
    void check_numberTypedOutsideFormula_noNumberingErrors() {
        // GIVEN
        String testFilePath = "src/test/resources/correct_formula_typed_number.docx";

        // WHEN
        List<FormatError> result = checker.check(testFilePath);

        // THEN
        assertTrue(numberingErrors(result).isEmpty(),
                "Formulas (2.1), (2.2) and (2.3) are numbered correctly; brackets such as (x mod 2^32) "
                        + "or p(pi) inside the formula are not numbers.");
    }

    @Test
    @DisplayName("Number typed after the formula object is checked: broken sequence")
    void check_numberTypedOutsideFormula_wrongSequence() {
        // GIVEN
        String testFilePath = "src/test/resources/incorrect_formula_typed_number.docx";

        // WHEN
        List<FormatError> result = checker.check(testFilePath);

        // THEN
        List<FormatError> errors = errorsWithId(result, "err_formula_sequence").stream()
                .filter(fe -> textContains(fe, "(2.3)"))
                .toList();

        assertFalse(errors.isEmpty(), "Failed to detect that (2.3) follows (2.1) in the typed numbers.");
        assertEquals(FORMULA, errors.getFirst().getCategory());
    }

    @Test
    @DisplayName("Number typed after the formula object is checked: wrong chapter")
    void check_numberTypedOutsideFormula_wrongChapter() {
        // GIVEN
        String testFilePath = "src/test/resources/incorrect_formula_typed_number.docx";

        // WHEN
        List<FormatError> result = checker.check(testFilePath);

        // THEN
        List<FormatError> errors = errorsWithId(result, "err_formula_chapter_mismatch").stream()
                .filter(fe -> textContains(fe, "(3.4)"))
                .toList();

        assertFalse(errors.isEmpty(), "Failed to detect formula (3.4) placed in chapter 2.");
    }

    @Test
    @DisplayName("Correct typed number does not produce errors next to wrong ones")
    void check_numberTypedOutsideFormula_correctNumberNotReported() {
        // GIVEN
        String testFilePath = "src/test/resources/incorrect_formula_typed_number.docx";

        // WHEN
        List<FormatError> result = checker.check(testFilePath);

        // THEN
        List<FormatError> errors = numberingErrors(result).stream()
                .filter(fe -> textContains(fe, "(2.1)"))
                .toList();

        assertTrue(errors.isEmpty(), "Formula (2.1) is the first one in chapter 2 and is correct.");
    }

    @Test
    @DisplayName("Formula text is shown in the error, not only the surrounding text")
    void check_numberTypedOutsideFormula_errorContainsFormulaText() {
        // GIVEN
        String testFilePath = "src/test/resources/incorrect_formula_typed_number.docx";

        // WHEN
        List<FormatError> result = checker.check(testFilePath);

        // THEN
        List<FormatError> errors = errorsWithId(result, "err_formula_sequence").stream()
                .filter(fe -> textContains(fe, "(2.3)"))
                .toList();

        assertFalse(errors.isEmpty(), "Failed to detect the broken sequence.");
        assertTrue(textContains(errors.getFirst(), "x = y × z"),
                "The paragraph text of the error should contain the text of the formula.");
    }

    @Test
    @DisplayName("Number inside the formula next to explanation text in the same paragraph is checked")
    void check_numberInsideFormulaWithTextInParagraph_wrongSequence() {
        // GIVEN
        String testFilePath = "src/test/resources/incorrect_formula_typed_number.docx";

        // WHEN
        List<FormatError> result = checker.check(testFilePath);

        // THEN
        List<FormatError> errors = errorsWithId(result, "err_formula_sequence").stream()
                .filter(fe -> textContains(fe, "еталонна"))
                .toList();

        assertFalse(errors.isEmpty(),
                "Number #(2.9) inside the formula must be validated even if the paragraph has explanation text.");
    }

    @Test
    @DisplayName("Warn that the formula number is typed outside of the formula")
    void check_numberTypedOutsideFormula_warning() {
        // GIVEN
        String testFilePath = "src/test/resources/correct_formula_typed_number.docx";

        // WHEN
        List<FormatError> result = checker.check(testFilePath);

        // THEN
        List<FormatError> warnings = errorsWithId(result, "err_formula_number_outside");

        assertEquals(2, warnings.size(),
                "Formulas (2.1) and (2.2) have numbers typed outside; (2.3) has its number inside the formula.");
        assertTrue(warnings.stream().anyMatch(fe -> textContains(fe, "(2.1)")));
        assertTrue(warnings.stream().anyMatch(fe -> textContains(fe, "(2.2)")));
        assertEquals(FORMULA, warnings.getFirst().getCategory());
        assertEquals("warning", warnings.getFirst().getSeverity());
    }

    @Test
    @DisplayName("Warn about every formula with a number typed outside, even when the number is wrong")
    void check_numberTypedOutsideFormula_warningForWrongNumbers() {
        // GIVEN
        String testFilePath = "src/test/resources/incorrect_formula_typed_number.docx";

        // WHEN
        List<FormatError> result = checker.check(testFilePath);

        // THEN
        assertEquals(3, errorsWithId(result, "err_formula_number_outside").size(),
                "Numbers (2.1), (2.3) and (3.4) are typed outside; #(2.9) is inside the formula.");
    }

    @Test
    @DisplayName("No warning when the number is a part of the formula or the formula is inline")
    void check_numberInsideFormula_noOutsideWarning() {
        // GIVEN
        String numberInsideFormula = "src/test/resources/correct_formula.docx";
        String inlineFormulas = "src/test/resources/correct_formula_inline.docx";
        String formulaImages = "src/test/resources/correct_formula_image.docx";

        // WHEN
        List<FormatError> insideResult = checker.check(numberInsideFormula);
        List<FormatError> inlineResult = checker.check(inlineFormulas);
        List<FormatError> imageResult = checker.check(formulaImages);

        // THEN
        assertTrue(errorsWithId(insideResult, "err_formula_number_outside").isEmpty(),
                "The number #(1.1) is a part of the formula.");
        assertTrue(errorsWithId(inlineResult, "err_formula_number_outside").isEmpty(),
                "Inline formulas have no number.");
        assertTrue(errorsWithId(imageResult, "err_formula_number_outside").isEmpty(),
                "Pictures get the 'not made with formula tool' warning instead.");
    }

    @Test
    @DisplayName("Formula inserted as a picture produces the 'not made with formula tool' warning")
    void check_imageFormula_warning() {
        // GIVEN
        String testFilePath = "src/test/resources/correct_formula_image.docx";

        // WHEN
        List<FormatError> result = checker.check(testFilePath);

        // THEN
        List<FormatError> warnings = errorsWithId(result, "err_formula_not_tool");

        assertEquals(1, warnings.size(),
                "Only the picture with number (2.2) is a formula inserted as an image.");
        assertTrue(textContains(warnings.getFirst(), "(2.2)"));
        assertEquals(FORMULA, warnings.getFirst().getCategory());
    }

    @Test
    @DisplayName("Formula inserted as a picture keeps the numbering sequence intact")
    void check_imageFormula_numberingContinues() {
        // GIVEN
        String testFilePath = "src/test/resources/correct_formula_image.docx";

        // WHEN
        List<FormatError> result = checker.check(testFilePath);

        // THEN
        assertTrue(numberingErrors(result).isEmpty(),
                "(2.1) formula, (2.2) picture and (2.3) formula are numbered consecutively.");
    }

    @Test
    @DisplayName("Figure with a caption is not recognized as a formula picture")
    void check_figureWithCaption_notRecognizedAsFormula() {
        // GIVEN
        String testFilePath = "src/test/resources/correct_formula_image.docx";

        // WHEN
        List<FormatError> result = checker.check(testFilePath);

        // THEN
        List<FormatError> errors = result.stream()
                .filter(fe -> textContains(fe, "Рисунок"))
                .toList();

        assertTrue(errors.isEmpty(), "A figure and its caption are not a formula.");
    }

    @Test
    @DisplayName("Formula inserted as a picture is checked for a wrong number")
    void check_imageFormula_wrongSequence() {
        // GIVEN
        String testFilePath = "src/test/resources/incorrect_formula_image.docx";

        // WHEN
        List<FormatError> result = checker.check(testFilePath);

        // THEN
        List<FormatError> sequenceErrors = errorsWithId(result, "err_formula_sequence");

        assertTrue(sequenceErrors.stream().anyMatch(fe -> textContains(fe, "(2.4)")),
                "Failed to detect that (2.4) follows (2.1).");
        assertTrue(sequenceErrors.stream().noneMatch(fe -> textContains(fe, "(2.1)")),
                "(2.1) is correct.");
        assertEquals(2, errorsWithId(result, "err_formula_not_tool").size(),
                "Both pictures are formulas inserted as images.");
    }

    // Warning: the formula number is typed as plain text outside of the formula
    @Test
    @DisplayName("Warn about the number typed outside of the formula in every layout variant")
    void check_numberOutside_warningForEachVariant() {
        // GIVEN
        // (2.1) right after the formula, (2.2) after a comma and tabs, (2.3). with a trailing period
        String testFilePath = "src/test/resources/formula_number_outside_variants.docx";

        // WHEN
        List<FormatError> result = checker.check(testFilePath);

        // THEN
        List<FormatError> warnings = errorsWithId(result, "err_formula_number_outside");

        assertEquals(3, warnings.size(), "Only the three formulas with a number typed after them must be reported.");
        assertTrue(warnings.stream().anyMatch(fe -> textContains(fe, "(2.1)")));
        assertTrue(warnings.stream().anyMatch(fe -> textContains(fe, "(2.2)")));
        assertTrue(warnings.stream().anyMatch(fe -> textContains(fe, "(2.3)")));
    }

    @Test
    @DisplayName("No warning for a formula without a number, with the number inside, or inline in a sentence")
    void check_numberOutside_noWarningWhenNumberIsNotOutside() {
        // GIVEN
        String testFilePath = "src/test/resources/formula_number_outside_variants.docx";

        // WHEN
        List<FormatError> result = checker.check(testFilePath);

        // THEN
        List<FormatError> warnings = errorsWithId(result, "err_formula_number_outside");

        assertTrue(warnings.stream().noneMatch(fe -> textContains(fe, "dd = 4")),
                "A formula without any number.");
        assertTrue(warnings.stream().noneMatch(fe -> textContains(fe, "ee = 5")),
                "The number #(2.4) is a part of the formula.");
        assertTrue(warnings.stream().noneMatch(fe -> textContains(fe, "ff = 6")),
                "The number (1.11) ends the sentence and is not a formula number.");
        assertTrue(warnings.stream().noneMatch(fe -> textContains(fe, "gg = 7")),
                "The decimal number (0.01) is not a formula number.");
    }

    @Test
    @DisplayName("The number outside of the formula is reported as a warning, not as an error")
    void check_numberOutside_isWarning() {
        // GIVEN
        String testFilePath = "src/test/resources/formula_number_outside_variants.docx";

        // WHEN
        List<FormatError> result = checker.check(testFilePath);

        // THEN
        List<FormatError> warnings = errorsWithId(result, "err_formula_number_outside");

        assertFalse(warnings.isEmpty());
        assertTrue(warnings.stream().allMatch(fe -> "warning".equals(fe.getSeverity())));
        assertTrue(warnings.stream().allMatch(fe -> FORMULA.equals(fe.getCategory())));
    }

    @Test
    @DisplayName("A manual formula without a formula object gets its own warning, not the number outside one")
    void check_numberOutside_notReportedForPlainTextFormula() {
        // GIVEN
        String testFilePath = "src/test/resources/manual_formula.docx";

        // WHEN
        List<FormatError> result = checker.check(testFilePath);

        // THEN
        assertTrue(errorsWithId(result, "err_formula_number_outside").isEmpty(),
                "Paragraphs without a formula object are handled by the 'not made with formula tool' warning.");
    }
}
