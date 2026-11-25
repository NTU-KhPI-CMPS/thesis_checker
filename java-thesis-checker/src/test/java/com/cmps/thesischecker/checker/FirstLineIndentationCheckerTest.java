package com.cmps.thesischecker.checker;

import com.cmps.thesischecker.model.FormatError;
import com.cmps.thesischecker.requirements.RequirementsHolder;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static com.cmps.thesischecker.model.ErrorCategory.FIRST_LINE_INDENTATION;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FirstLineIndentationCheckerTest extends BaseTest {

    private final Checker checker = new FirstLineIndentationChecker();

    @Override
    protected Checker getChecker() {
        return checker;
    }

    private static final String CORRECT_FILE = "src/test/resources/correct_first_line_indentation.docx";
    private static final String CORRECT_DOC_DEFAULTS_FILE = "src/test/resources/correct_doc_defaults.docx";
    private static final String CORRECT_FIRST_LINE_CHARS_FILE = "src/test/resources/correct_first_line_chars.docx";
    private static final String IGNORED_ELEMENTS_FILE = "src/test/resources/ignored_elements_with_wrong_indents.docx";
    private static final String INCORRECT_FILE = "src/test/resources/incorrect_first_line_indentation.docx";

    @Test
    void check_correctFormatting() {
        // GIVEN
        String filePath = CORRECT_FILE;

        // WHEN
        List<FormatError> result = checker.check(filePath);

        // THEN
        assertTrue(result.isEmpty(), "File should not have any errors related to first line indentation.");
    }

    @Test
    void check_docDefaults_noErrors() {
        // GIVEN
        String filePath = CORRECT_DOC_DEFAULTS_FILE;

        // WHEN
        List<FormatError> result = checker.check(filePath);

        // THEN
        assertTrue(result.isEmpty(), "File should not have any errors related to first line indentation.");
    }

    @Test
    void check_firstLineChars_noErrors() {
        // GIVEN
        String filePath = CORRECT_FIRST_LINE_CHARS_FILE;

        // WHEN
        List<FormatError> result = checker.check(filePath);

        // THEN
        assertTrue(result.isEmpty(), "File should not have any errors related to first line indentation.");
    }

    @Test
    void check_ignoredElements_noErrors() {
        // GIVEN
        String filePath = IGNORED_ELEMENTS_FILE;

        // WHEN
        List<FormatError> result = checker.check(filePath);

        // THEN
        assertTrue(result.isEmpty(), "File should not have any errors related to first line indentation.");
    }

    @Test
    void check_hangingIndent_warningFound() {
        // GIVEN
        String filePath = INCORRECT_FILE;

        // WHEN
        List<FormatError> result = checker.check(filePath);

        // THEN
        List<FormatError> warnings = result.stream()
                .filter(e -> "warn_first_line_hanging".equals(e.getId()))
                .toList();
        assertEquals(1, warnings.size(), "Expected exactly one hanging indent warning");
        FormatError warning = warnings.getFirst();
        assertEquals("warn_first_line_hanging", warning.getId());
        assertEquals(FIRST_LINE_INDENTATION, warning.getCategory());
        assertEquals("warning", warning.getSeverity());
        assertEquals(Set.of("-1.25"), warning.getFound());
        assertEquals(RequirementsHolder.getFirstLineIndentation(), warning.getExpected());
    }

    @Test
    void check_zeroFirstLine_errorFound() {
        // GIVEN
        String filePath = INCORRECT_FILE;

        // WHEN
        List<FormatError> result = checker.check(filePath);

        // THEN
        List<FormatError> errors = result.stream()
                .filter(e -> "err_first_line_indentation".equals(e.getId()))
                .toList();
        assertEquals(1, errors.size(), "Expected exactly one first line indentation error");
        FormatError error = errors.getFirst();
        assertEquals("err_first_line_indentation", error.getId());
        assertEquals(FIRST_LINE_INDENTATION, error.getCategory());
        assertEquals("error", error.getSeverity());
        assertEquals(Set.of("0"), error.getFound());
        assertEquals(RequirementsHolder.getFirstLineIndentation(), error.getExpected());
    }
}
