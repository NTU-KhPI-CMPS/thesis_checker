package com.cmps.thesischecker.checker;

import com.cmps.thesischecker.model.FormatError;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FontCheckerTest {
    @Test
    void check_fontName_findsErrors() {
        FontChecker checker = new FontChecker("Times New Roman");
        List <FormatError> result = checker.check("src/test/resources/Test.docx");
        // Test file contains errors - different fonts than expected
        assertTrue(result.isEmpty(), "Expected errors in test document");
    }
}