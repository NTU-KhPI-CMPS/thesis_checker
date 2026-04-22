package com.cmps.thesischecker.checker;

import com.cmps.thesischecker.model.FormatError;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class FontCheckerTest {
    @Test
    void check_fontName_findsErrors() {
        FontChecker checker = new FontChecker();
        List<FormatError> result = checker.check("src/test/resources/Test.docx");
        assertFalse(result.isEmpty(), "Expected errors in test document");
        assertEquals(7, result.size(), "Should find exactly 7 font errors in test document");
    }

    @Test
    void check_fontName_noErrorsForCorrectFont() {
        FontChecker checker = new FontChecker();
        List<FormatError> result = checker.check("src/test/resources/Test(Font).docx");
        assertTrue(result.isEmpty(), "Expected no errors in document with correct font (Times New Roman)");
    }
}
