package com.cmps.thesischecker.utils;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
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
    void getNearestParagraphText_returnsOwnText_whenParagraphHasText() {
        // GIVEN
        XWPFParagraph p1 = correctParagraphs.get(0);

        // WHEN
        String result = ParagraphUtils.getNearestParagraphText(p1, correctParagraphs);

        // THEN
        assertEquals("First paragraph", result);
    }

    @Test
    void getNearestParagraphText_returnsPreviousText_whenCurrentIsEmpty() {
        // GIVEN
        XWPFParagraph p3 = correctParagraphs.get(2);

        // WHEN
        String result = ParagraphUtils.getNearestParagraphText(p3, correctParagraphs);

        // THEN
        assertEquals("Second paragraph", result);
    }

    @Test
    void getNearestParagraphText_returnsNextText_whenPreviousIsAlsoEmpty() {
        // GIVEN
        XWPFParagraph p4 = correctParagraphs.get(3);

        // WHEN
        String result = ParagraphUtils.getNearestParagraphText(p4, correctParagraphs);

        // THEN
        assertEquals("Next paragraph", result);
    }

    @Test
    void getNearestParagraphText_returnsEmpty_whenAllParagraphsAreEmpty() throws IOException {
        // GIVEN
        try (XWPFDocument emptyDoc = new XWPFDocument(new FileInputStream(INCORRECT_FILE))) {
            List<XWPFParagraph> emptyParagraphs = emptyDoc.getParagraphs();
            XWPFParagraph emptyPara = emptyParagraphs.get(1);

            // WHEN
            String result = ParagraphUtils.getNearestParagraphText(emptyPara, List.of(emptyPara));

            // THEN
            assertEquals("", result);
        }
    }

    @Test
    void getNearestParagraphText_prefersCloserParagraph() {
        // GIVEN
        XWPFParagraph p3 = correctParagraphs.get(2);

        // WHEN
        String result = ParagraphUtils.getNearestParagraphText(p3, correctParagraphs);

        // THEN
        assertEquals("Second paragraph", result);
    }

    @Test
    void getNearestParagraphText_handlesNullParagraphGracefully() {
        // GIVEN
        XWPFParagraph nullParagraph = null;

        // WHEN
        String result = ParagraphUtils.getNearestParagraphText(nullParagraph, correctParagraphs);

        // THEN
        assertEquals("", result);
    }

    @Test
    void getNearestParagraphText_returnsOwnTextEvenIfNotInList() throws IOException {
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
