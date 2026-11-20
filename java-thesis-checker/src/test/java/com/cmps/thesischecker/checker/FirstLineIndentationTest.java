package com.cmps.thesischecker.checker;

import com.cmps.thesischecker.model.FormatError;
import com.cmps.thesischecker.requirements.RequirementsHolder;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import static com.cmps.thesischecker.model.ErrorCategory.FIRST_LINE_INDENTATION;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FirstLineIndentationTest extends BaseTest {

    private Checker checker = new FirstLineIndentationChecker();

    @Override
    protected Checker getChecker() {
        return checker;
    }

    @Test
    void check_correctFormatting() {
        //WHEN
        List<FormatError> result = checker.check("src/test/resources/correct_first_line_indentation.docx");

        // THEN
        assertTrue(result.isEmpty(), "File should not have any errors related to first line indentation.");
    }

    @Test
    void check_incorrectFormatting_defaultFormatting() {
        // GIVEN
        String expectedParagraphText
                = "Не правильно відформатований відступ першого рядка абзацу (відступ за замовчуванням).";

        //WHEN
        List<FormatError> result = checker.check("src/test/resources/incorrect_first_line_indentation.docx");

        // THEN
        List<FormatError> errorsWithExpectedText
                = result.stream()
                        .filter(fe -> Objects.equals(fe.getParagraphText(), expectedParagraphText))
                        .toList();
        assertEquals(1, errorsWithExpectedText.size(), "Only one error with expected text should be found.");

        FormatError formatError = errorsWithExpectedText.getFirst();
        assertEquals(expectedParagraphText, formatError.getParagraphText());
        assertEquals(FIRST_LINE_INDENTATION, formatError.getCategory());
        assertEquals(Set.of("0"), formatError.getFound());
        assertEquals(RequirementsHolder.getFirstLineIndentation(), formatError.getExpected());
    }

    @Test
    void check_incorrectFormatting_formatIsSpecifiedOnText() {
        // GIVEN
        String expectedParagraphText = "Не правильно відформатований відступ першого рядка абзацу (відступ вказаний на тексті).";

        //WHEN
        List<FormatError> result = checker.check("src/test/resources/incorrect_first_line_indentation.docx");

        // THEN
        List<FormatError> errorsWithExpectedText
                = result.stream()
                        .filter(fe -> Objects.equals(fe.getParagraphText(), expectedParagraphText))
                        .toList();
        assertEquals(1, errorsWithExpectedText.size(), "Only one error with expected text should be found.");

        FormatError formatError = errorsWithExpectedText.getFirst();
        assertEquals(expectedParagraphText, formatError.getParagraphText());
        assertEquals(FIRST_LINE_INDENTATION, formatError.getCategory());
        assertEquals(Set.of("0.5"), formatError.getFound());
        assertEquals(RequirementsHolder.getFirstLineIndentation(), formatError.getExpected());
    }
}
