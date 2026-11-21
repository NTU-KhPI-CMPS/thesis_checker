package com.cmps.thesischecker.utils;

import org.apache.poi.common.usermodel.PictureType;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FormulaUtilsTest {

    @ParameterizedTest
    @DisplayName("Numbers inside prose are not formula numbers")
    @ValueSource(strings = {
            "Кожен тест повертає p-value, що дозволяє кількісно визначити якість генератора на заданому рівні значущості (0.01).",
            "Рівень значущості (0.05)",
            "Рівень значущості (1.05).",
            "Поріг на заданому рівні значущості (1.11).",
            "Див. формулу (1.11).",
            "Значення (1.11).",
            "де p = 0.5, значущості (1.11)",
            "при n = 10 вибірка (1.11).",
            "де p = 0.5 на рівні значущості (1.11)",
            "Швидкість тіла визначається за формулою V = S/t при значенні (1.11)."
    })
    void isTrailingFormulaNumber_prose(String text) {
        assertFalse(FormulaUtils.isTrailingFormulaNumber(text));
    }

    @ParameterizedTest
    @DisplayName("Real formula numbers are detected")
    @ValueSource(strings = {
            "(1.1)",
            "V = S/t (1.5).",
            "Швидкість тіла визначається за формулою V = S/t (1.5).",
            "x >> 27,       (2.2)",
            "S = a × b\t(2.10)",
            "E = mc2 (3.12);",
            "σ = RMS (1.3)",
            "x = a sin (1.2)",
            "y = x · sin (1.1)",
            "Швидкість визначається за формулою V = RMS (1.5)."
    })
    void isTrailingFormulaNumber_formula(String text) {
        assertTrue(FormulaUtils.isTrailingFormulaNumber(text));
    }

    @ParameterizedTest
    @DisplayName("Formula embedded in a sentence has no number")
    @ValueSource(strings = {
            "Свою назву він отримав завдяки тому, що його період  є простим числом Мерсенна.",
            "де  – поточне значення послідовності (сід),",
            "– коефіцієнт множення,"
    })
    void hasProseOutsideFormula_inline(String outsideText) {
        assertTrue(FormulaUtils.hasProseOutsideFormula(outsideText));
    }

    @ParameterizedTest
    @DisplayName("Display formula: nothing but a number / punctuation / single letters around it")
    @ValueSource(strings = {
            "",
            "(2.1)",
            ",       (2.1)",
            "x ^= x ^= , x ^= ,(2.2)",
            ",    (2.10)"
    })
    void hasProseOutsideFormula_display(String outsideText) {
        assertFalse(FormulaUtils.hasProseOutsideFormula(outsideText));
    }

    // paragraphs built in memory

    private static void addPicture(XWPFParagraph paragraph) throws Exception {
        BufferedImage image = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        ImageIO.write(image, "png", bytes);

        XWPFRun run = paragraph.createRun();
        run.addPicture(new ByteArrayInputStream(bytes.toByteArray()),
                PictureType.PNG, "formula.png", 100000, 100000);
    }

    @Test
    @DisplayName("Picture followed by a number only is a formula inserted as an image")
    void isImageFormulaParagraph_pictureAndNumber() throws Exception {
        try (XWPFDocument document = new XWPFDocument()) {
            XWPFParagraph paragraph = document.createParagraph();
            addPicture(paragraph);
            paragraph.createRun().setText("  (2.6)");

            assertTrue(FormulaUtils.isImageFormulaParagraph(paragraph));
        }
    }

    @Test
    @DisplayName("Picture with a caption is a figure, not a formula")
    void isImageFormulaParagraph_pictureWithCaption() throws Exception {
        try (XWPFDocument document = new XWPFDocument()) {
            XWPFParagraph paragraph = document.createParagraph();
            addPicture(paragraph);
            paragraph.createRun().setText("Рисунок 2.1 – Назва рисунка");

            assertFalse(FormulaUtils.isImageFormulaParagraph(paragraph));
        }
    }

    @Test
    @DisplayName("Picture without any text is not a formula")
    void isImageFormulaParagraph_pictureOnly() throws Exception {
        try (XWPFDocument document = new XWPFDocument()) {
            XWPFParagraph paragraph = document.createParagraph();
            addPicture(paragraph);

            assertFalse(FormulaUtils.isImageFormulaParagraph(paragraph));
        }
    }

    @Test
    @DisplayName("Text number without a picture is not a formula picture")
    void isImageFormulaParagraph_numberOnly() throws Exception {
        try (XWPFDocument document = new XWPFDocument()) {
            XWPFParagraph paragraph = document.createParagraph();
            paragraph.createRun().setText("(2.6)");

            assertFalse(FormulaUtils.isImageFormulaParagraph(paragraph));
        }
    }

    @Test
    @DisplayName("Number typed after the formula object is returned without brackets")
    void getTypedNumberOutsideFormula_returnsNumber() throws Exception {
        try (XWPFDocument document = new XWPFDocument()) {
            XWPFParagraph paragraph = document.createParagraph();
            XWPFRun run = paragraph.createRun();
            run.setText(", ");
            run.addTab();
            run.addTab();
            XWPFRun numberRun = paragraph.createRun();
            numberRun.setText("      (2.16)");

            assertEquals("2.16", FormulaUtils.getTypedNumberOutsideFormula(paragraph));
        }
    }

    @Test
    @DisplayName("No typed number when the paragraph does not end with one")
    void getTypedNumberOutsideFormula_noNumber() throws Exception {
        try (XWPFDocument document = new XWPFDocument()) {
            XWPFParagraph paragraph = document.createParagraph();
            paragraph.createRun().setText("де x — поточне значення.");

            assertNull(FormulaUtils.getTypedNumberOutsideFormula(paragraph));
        }
    }
}
