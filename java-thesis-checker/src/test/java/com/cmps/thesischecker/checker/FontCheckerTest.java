package com.cmps.thesischecker.checker;

import com.cmps.thesischecker.model.FormatError;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.junit.jupiter.api.Test;

import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class FontCheckerTest extends BaseTest {

    private final Checker checker = new FontChecker();
    protected Checker getChecker() {
        return checker;
    }

    @Test
    void check_fontName_findsErrors() {
        FontChecker checker = new FontChecker();
        List<FormatError> result = checker.check("src/test/resources/incorrect_fonts.docx");
        assertFalse(result.isEmpty(), "Expected errors in test document");
        assertEquals(7, result.size(), "Should find exactly 7 font errors in test document");
    }

    @Test
    void check_fontName_noErrorsForCorrectFont() {
        FontChecker checker = new FontChecker();
        List<FormatError> result = checker.check("src/test/resources/correct_font.docx");
        assertTrue(result.isEmpty(), "Expected no errors in document with correct font (Times New Roman)");
    }

    @Test
    void check_fontName_allowsCambriaMath() throws Exception {
        // GIVEN
        Path file = Files.createTempFile("cambria-math", ".docx");
        file.toFile().deleteOnExit();

        try (XWPFDocument doc = new XWPFDocument()) {
            XWPFParagraph paragraph = doc.createParagraph();
            XWPFRun run = paragraph.createRun();
            run.setText("x = 1");
            run.setFontFamily("Cambria Math");

            try (OutputStream out = Files.newOutputStream(file)) {
                doc.write(out);
            }
        }

        // WHEN
        List<FormatError> result = checker.check(file.toString());

        // THEN
        assertTrue(result.isEmpty(), "Expected no errors for Cambria Math");
    }
}
