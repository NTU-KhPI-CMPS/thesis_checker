package com.cmps.thesischecker.checker;

import com.cmps.thesischecker.model.FormatError;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

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
    @ValueSource(strings = {"src/test/resources/normal_style.docx",
            "src/test/resources/inherited_styles.docx"})
    void check_noErrors(String fileName) {
        // WHEN
        List<FormatError> result = getChecker().check(fileName);

        // THEN
        assertTrue(result.isEmpty(), "Expected no errors in test document");
    }
}
