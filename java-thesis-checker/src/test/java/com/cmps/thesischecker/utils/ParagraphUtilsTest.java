package com.cmps.thesischecker.utils;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ParagraphUtilsTest {

    private static final String CORRECT_FILE = "src/test/resources/correct_paragraph_utils_test.docx";
    private static final String INCORRECT_FILE = "src/test/resources/incorrect_paragraph_utils_test.docx";

    private static XWPFDocument correctDoc;
    private static List<XWPFParagraph> correctParagraphs;

    @BeforeAll
    static void setUp() throws IOException {
        correctDoc = new XWPFDocument(new FileInputStream(CORRECT_FILE));
        correctParagraphs = correctDoc.getParagraphs();
    }

    @AfterAll
    static void tearDown() throws IOException {
        if (correctDoc != null) {
            correctDoc.close();
        }
    }

    @Test
    void getNearestParagraphText_paragraphHasText_returnsOwnText() {
        // GIVEN
        XWPFParagraph p1 = correctParagraphs.get(0);

        // WHEN
        String result = ParagraphUtils.getNearestParagraphText(p1, correctParagraphs);

        // THEN
        assertEquals("First paragraph", result);
    }

    @Test
    void getNearestParagraphText_currentIsEmpty_returnsPreviousText() {
        // GIVEN
        XWPFParagraph p3 = correctParagraphs.get(2);

        // WHEN
        String result = ParagraphUtils.getNearestParagraphText(p3, correctParagraphs);

        // THEN
        assertEquals("Second paragraph", result);
    }

    @Test
    void getNearestParagraphText_previousIsAlsoEmpty_returnsNextText() {
        // GIVEN
        XWPFParagraph p4 = correctParagraphs.get(3);

        // WHEN
        String result = ParagraphUtils.getNearestParagraphText(p4, correctParagraphs);

        // THEN
        assertEquals("Next paragraph", result);
    }

    @Test
    void getNearestParagraphText_allParagraphsAreEmpty_returnsEmpty() throws IOException {
        // GIVEN
        try (XWPFDocument emptyDoc = new XWPFDocument(new FileInputStream(INCORRECT_FILE))) {
            List<XWPFParagraph> emptyParagraphs = emptyDoc.getParagraphs();
            XWPFParagraph emptyParagraph = emptyParagraphs.get(1);

            // WHEN
            String result = ParagraphUtils.getNearestParagraphText(emptyParagraph, List.of(emptyParagraph));

            // THEN
            assertEquals("There is no text in the document with the incorrectly formatted image.", result);
        }
    }

    @Test
    void getNearestParagraphText_nullParagraph_returnsEmpty() {
        // GIVEN
        XWPFParagraph nullParagraph = null;

        // WHEN
        String result = ParagraphUtils.getNearestParagraphText(nullParagraph, correctParagraphs);

        // THEN
        assertEquals("ERROR: Cannot find nearest to image text. Index is < 0", result);
    }

    @Test
    void getNearestParagraphText_paragraphWithTextNotInList_returnsOwnText() throws IOException {
        // GIVEN
        XWPFParagraph outsideParagraph;
        try (XWPFDocument otherDoc = new XWPFDocument()) {
            outsideParagraph = otherDoc.createParagraph();
            outsideParagraph.createRun().setText("From other doc");
        }

        // WHEN
        String result = ParagraphUtils.getNearestParagraphText(outsideParagraph, correctParagraphs);

        // THEN
        assertEquals("From other doc", result);
    }
}
