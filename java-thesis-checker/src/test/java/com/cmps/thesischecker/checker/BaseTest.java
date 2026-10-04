package com.cmps.thesischecker.checker;

import com.cmps.thesischecker.model.FormatError;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Contains tests for basic formatting scenarios.
 * Verifies only files without any formatting errors.
 * All cases where formatting errors are present should
 * be verified in test classes for specific checkers.
 *
 * @author Mariia Borodin (HappyMary16)
 * @since 1.0
 */
public abstract class BaseTest {

    protected abstract Checker getChecker();

    @ParameterizedTest
    @DisplayName("Verify that files don't have any warning or errors")
    @ValueSource(strings = {"src/test/resources/correct_normal_style.docx",
            "src/test/resources/correct_inherited_styles.docx"})
    void check_noErrorsOrWarnings(String fileName) {
        if (getChecker() == null) {
            return;
        }

        // WHEN
        List<FormatError> result = getChecker().check(fileName);

        // THEN
        assertEquals(Collections.emptyList(), result, "Expected no errors in test document");
    }

    @ParameterizedTest
    @DisplayName("Verify that files don't have any errors, warnings are allowed")
    @ValueSource(strings = {"src/test/resources/correct_formulas.docx"})
    void check_noErrors(String fileName) {
        if (getChecker() == null) {
            return;
        }

        // WHEN
        List<FormatError> result = getChecker().check(fileName);

        // THEN
        List<FormatError> onlyErrors =
                result.stream()
                      .filter(error -> !Objects.equals(error.getSeverity(), "warning"))
                      .toList();
        assertEquals(Collections.emptyList(), onlyErrors,
                     "Expected no errors in test document");
    }
}
