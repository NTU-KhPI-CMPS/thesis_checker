package com.cmps.thesischecker.checker;

import com.cmps.thesischecker.model.FormatError;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Тести для перевірки розміру шрифту (SizeChecker).
 * Використовує згенеровані тестові документи:
 * - correct_font_size.docx  – весь текст має Times New Roman, 14 pt
 * - incorrect_font_size.docx – текст має різні неправильні розміри (12,16,10,18,20,8,22,24 pt)
 *   та один абзац з правильним розміром 14 pt.
 */
public class SizeCheckerTest extends BaseTest {

    protected Checker getChecker() {
        return new SizeChecker(14);
    }

    @Test
    void check_fontSize_findsErrors() {
        SizeChecker checker = new SizeChecker(14);
        List<FormatError> result = checker.check("src/test/resources/incorrect_font_size.docx");
        assertFalse(result.isEmpty(), "Expected errors in document with incorrect font sizes");

        assertEquals(11, result.size(), "Should find 11 size errors in test document");
    }

    @Test
    void check_fontSize_noErrorsForCorrectSize() {
        SizeChecker checker = new SizeChecker(14);
        List<FormatError> result = checker.check("src/test/resources/correct_font_size.docx");
        assertTrue(result.isEmpty(), "Expected no errors in document where all text has correct size (14 pt)");
    }

    @Test
    void check_fontSize_errorFieldsAreCorrect() {
        SizeChecker checker = new SizeChecker(14);
        List<FormatError> errors = checker.check("src/test/resources/incorrect_font_size.docx");
        assertFalse(errors.isEmpty(), "Expected errors in test document");
        
        FormatError firstError = errors.get(0);
        assertNotNull(firstError);
        assertEquals("FONT_SIZE", firstError.getCategory().toString());
        assertNotNull(firstError.getFound());
        assertFalse(firstError.getFound().isEmpty());
        assertEquals("14pt", firstError.getExpected());
    }
}