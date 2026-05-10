package com.cmps.thesischecker.checker;

import com.cmps.thesischecker.model.FormatError;
import com.cmps.thesischecker.model.ParagraphSpacing;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSpacing;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPPr;

import java.math.BigInteger;
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
            "src/test/resources/spacing_before_paragraph.docx",
            "src/test/resources/spacing_after_paragraph.docx",
            "src/test/resources/spacing_before_and_after_paragraph.docx"
    })
    @DisplayName("Returns errors when paragraph has incorrect spacing")
    void check_spacingErrors_returnsErrors(String fileName) {
        List<FormatError> result = checker.check(fileName);

        assertFalse(result.isEmpty(), "Expected errors but got none for: " + fileName);
    }

    @Test
    @DisplayName("Returns file error when file does not exist")
    void check_fileNotFound_returnsFileError() {
        List<FormatError> result = checker.check("non_existent_file.docx");

        assertEquals(1, result.size());
        assertEquals("err_000", result.getFirst().getId());
    }

    @Test
    @DisplayName("Returns null when CTSpacing is null")
    void getSpacingFromCTSpacing_null_returnsNull() {
        ParagraphSpacing result = checker.getSpacingFromCTSpacing(null);

        assertNull(result);
    }

    @Test
    @DisplayName("Returns null when both before and after are not set")
    void getSpacingFromCTSpacing_bothNull_returnsNull() {
        CTSpacing spacing = CTSpacing.Factory.newInstance();

        ParagraphSpacing result = checker.getSpacingFromCTSpacing(spacing);

        assertNotNull(result);
        assertEquals(0.0, result.getUpSpacing());
        assertEquals(0.0, result.getBottomSpacing());
    }

    @Test
    @DisplayName("Returns correct value when only before is set")
    void getSpacingFromCTSpacing_onlyBefore_returnsCorrectValue() {
        CTSpacing spacing = CTSpacing.Factory.newInstance();
        spacing.setBefore(BigInteger.valueOf(240));

        ParagraphSpacing result = checker.getSpacingFromCTSpacing(spacing);

        assertNotNull(result);
        assertEquals(12.0, result.getUpSpacing());
        assertEquals(0.0, result.getBottomSpacing());
    }

    @Test
    @DisplayName("Returns correct value when only after is set")
    void getSpacingFromCTSpacing_onlyAfter_returnsCorrectValue() {
        CTSpacing spacing = CTSpacing.Factory.newInstance();
        spacing.setAfter(BigInteger.valueOf(200));

        ParagraphSpacing result = checker.getSpacingFromCTSpacing(spacing);

        assertNotNull(result);
        assertEquals(0.0, result.getUpSpacing());
        assertEquals(10.0, result.getBottomSpacing());
    }

    @Test
    @DisplayName("Returns correct values when both before and after are set")
    void getSpacingFromCTSpacing_bothSet_returnsCorrectValues() {
        CTSpacing spacing = CTSpacing.Factory.newInstance();
        spacing.setBefore(BigInteger.valueOf(240));
        spacing.setAfter(BigInteger.valueOf(160));

        ParagraphSpacing result = checker.getSpacingFromCTSpacing(spacing);

        assertNotNull(result);
        assertEquals(12.0, result.getUpSpacing());
        assertEquals(8.0, result.getBottomSpacing());
    }

    @Test
    @DisplayName("Returns zero values when before and after are set to zero")
    void getSpacingFromCTSpacing_zeroValues_returnsZero() {
        CTSpacing spacing = CTSpacing.Factory.newInstance();
        spacing.setBefore(BigInteger.ZERO);
        spacing.setAfter(BigInteger.ZERO);

        ParagraphSpacing result = checker.getSpacingFromCTSpacing(spacing);

        assertNotNull(result);
        assertEquals(0.0, result.getUpSpacing());
        assertEquals(0.0, result.getBottomSpacing());
    }

    @Test
    @DisplayName("Returns null when CTPPr is null")
    void getSpacingFromPPr_nullCTPPr_returnsNull() {
        ParagraphSpacing result = checker.getSpacingFromPPr((CTPPr) null);

        assertNull(result);
    }

    @Test
    @DisplayName("Returns null when CTPPr has no spacing")
    void getSpacingFromPPr_noSpacing_returnsNull() {
        CTPPr pPr = CTPPr.Factory.newInstance();

        ParagraphSpacing result = checker.getSpacingFromPPr(pPr);

        assertNull(result);
    }

    @Test
    @DisplayName("Returns spacing value when CTPPr has spacing set")
    void getSpacingFromPPr_withSpacing_returnsValue() {
        CTPPr pPr = CTPPr.Factory.newInstance();
        CTSpacing spacing = pPr.addNewSpacing();
        spacing.setBefore(BigInteger.valueOf(240));

        ParagraphSpacing result = checker.getSpacingFromPPr(pPr);

        assertNotNull(result);
        assertEquals(12.0, result.getUpSpacing());
    }

    @Test
    @DisplayName("Returns no errors when paragraph has no spacing")
    void validate_noSpacing_returnsNoErrors() throws Exception {
        try (XWPFDocument doc = new XWPFDocument()) {
            XWPFParagraph paragraph = doc.createParagraph();
            paragraph.createRun().setText("Текст без відступу");

            List<FormatError> errors = checker.validate(paragraph);

            assertTrue(errors.isEmpty());
        }
    }

    @Test
    @DisplayName("Returns spacing before error when before spacing is set")
    void validate_spacingBefore_returnsBeforeError() throws Exception {
        try (XWPFDocument doc = new XWPFDocument()) {
            XWPFParagraph paragraph = doc.createParagraph();
            paragraph.createRun().setText("Текст з відступом");

            CTPPr pPr = paragraph.getCTP().addNewPPr();
            CTSpacing spacing = pPr.addNewSpacing();
            spacing.setBefore(BigInteger.valueOf(240));

            List<FormatError> errors = checker.validate(paragraph);

            assertEquals(1, errors.size());
            assertEquals("err_spacing_before", errors.getFirst().getId());
        }
    }

    @Test
    @DisplayName("Returns spacing after error when after spacing is set")
    void validate_spacingAfter_returnsAfterError() throws Exception {
        try (XWPFDocument doc = new XWPFDocument()) {
            XWPFParagraph paragraph = doc.createParagraph();
            paragraph.createRun().setText("Текст з відступом");

            CTPPr pPr = paragraph.getCTP().addNewPPr();
            CTSpacing spacing = pPr.addNewSpacing();
            spacing.setAfter(BigInteger.valueOf(200));

            List<FormatError> errors = checker.validate(paragraph);

            assertEquals(1, errors.size());
            assertEquals("err_spacing_after", errors.getFirst().getId());
        }
    }

    @Test
    @DisplayName("Returns two errors when both before and after spacing are set")
    void validate_spacingBeforeAndAfter_returnsTwoErrors() throws Exception {
        try (XWPFDocument doc = new XWPFDocument()) {
            XWPFParagraph paragraph = doc.createParagraph();
            paragraph.createRun().setText("Текст з відступами");

            CTPPr pPr = paragraph.getCTP().addNewPPr();
            CTSpacing spacing = pPr.addNewSpacing();
            spacing.setBefore(BigInteger.valueOf(240));
            spacing.setAfter(BigInteger.valueOf(200));

            List<FormatError> errors = checker.validate(paragraph);

            assertEquals(2, errors.size());
            assertTrue(errors.stream().anyMatch(e -> e.getId().equals("err_spacing_before")));
            assertTrue(errors.stream().anyMatch(e -> e.getId().equals("err_spacing_after")));
        }
    }
}
