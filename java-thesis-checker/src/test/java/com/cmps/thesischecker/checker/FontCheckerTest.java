package com.cmps.thesischecker.checker;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class FontCheckerTest {
    @Test
    void check_fontName_findsErrors() {
        FontChecker checker = new FontChecker("Times New Roman");
        boolean result = checker.check("src/test/resources/Test.docx");
        // Test file contains errors - different fonts than expected
        assertFalse(result, "Expected errors in test document");
    }
}