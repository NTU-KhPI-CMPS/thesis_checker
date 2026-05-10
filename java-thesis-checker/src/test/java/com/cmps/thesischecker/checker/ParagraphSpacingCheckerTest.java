package com.cmps.thesischecker.checker;

import com.cmps.thesischecker.model.FormatError;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ParagraphSpacingCheckerTest extends BaseTest {

    private final ParagraphSpacingChecker checker = new ParagraphSpacingChecker();

    @Override
    protected Checker getChecker() {
        return checker;
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "src/test/resources/incorrect_paragraph_spacings.docx"
    })
    @DisplayName("Returns errors when paragraph has incorrect spacing")
    void check_spacingErrors_returnsErrors(String fileName) {
        List<FormatError> result = checker.check(fileName);

        assertFalse(result.isEmpty(), "Expected errors but got none for: " + fileName);
        assertTrue(result.stream().allMatch(e ->
                e.getId().equals("err_spacing_before") || e.getId().equals("err_spacing_after")));
    }

    @Test
    @DisplayName("Returns file error when file does not exist")
    void check_fileNotFound_returnsFileError() {
        List<FormatError> result = checker.check("non_existent_file.docx");

        assertEquals(1, result.size());
        assertEquals("err_000", result.getFirst().getId());
    }
}
