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

public class FirstLineIndentationCheckerTest extends BaseTest {

    private Checker checker = new FirstLineIndentationChecker();

    @Override
    protected Checker getChecker() {
        return checker;
    }

    @Test
    void check_correctFormatting() {
        List<FormatError> result = checker.check("src/test/resources/correct_first_line_indentation.docx");
        assertTrue(result.isEmpty(), "File should not have any errors related to first line indentation.");
    }

    @Test
    void check_figureCaption_noErrors() {
        List<FormatError> result = checker.check("src/test/resources/correct_first_line_indentation.docx");
        assertTrue(result.stream().noneMatch(e -> Objects.equals(e.getParagraphText(), "Рисунок 1 - Назва")),
                "Figure caption should not produce errors.");
    }

    @Test
    void check_tableText_noErrors() {
        List<FormatError> result = checker.check("src/test/resources/correct_first_line_indentation.docx");
        assertTrue(result.stream().noneMatch(e -> Objects.equals(e.getParagraphText(), "Текст у таблиці")),
                "Table text should not produce errors.");
    }

    @Test
    void check_hangingIndent_warningFound() {
        List<FormatError> result = checker.check("src/test/resources/incorrect_first_line_indentation.docx");
        List<FormatError> warnings = result.stream()
                .filter(e -> "warn_first_line_hanging".equals(e.getId()))
                .toList();
        assertEquals(1, warnings.size());
        assertEquals(Set.of("-1.25"), warnings.get(0).getFound());
    }

    @Test
    void check_zeroFirstLine_errorFound() {
        List<FormatError> result = checker.check("src/test/resources/incorrect_first_line_indentation.docx");
        List<FormatError> errors = result.stream()
                .filter(e -> "err_first_line_indentation".equals(e.getId()))
                .toList();
        assertEquals(1, errors.size());
        assertEquals(Set.of("0"), errors.get(0).getFound());
    }
}
