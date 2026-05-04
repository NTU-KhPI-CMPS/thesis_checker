package com.cmps.thesischecker.checker;

import com.cmps.thesischecker.model.ErrorCategory;
import com.cmps.thesischecker.model.FormatError;
import com.cmps.thesischecker.requirements.RequirementsHolder;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFStyle;
import org.apache.poi.xwpf.usermodel.XWPFStyles;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPPrBase;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPPrGeneral;

import java.io.FileInputStream;
import java.util.*;

public class AlignmentChecker implements Checker {

    /**
     * Checks the document for text alignment violations and returns all found format errors.
     *
     * @param filePath path to the DOCX file
     * @return list of detected formatting errors
     */
    @Override
    public List<FormatError> check(String filePath) {
        List<FormatError> allErrors = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(filePath);
             XWPFDocument doc = new XWPFDocument(fis)) {
            for (XWPFParagraph paragraph : doc.getParagraphs()) {
                String paragraphText = paragraph.getText().trim();
                if (paragraphText.isEmpty()) {
                    continue;
                }
                Set<String> incorrectAlignments = validate(paragraph);
                if (!incorrectAlignments.isEmpty()) {
                    allErrors.add(buildAlignmentError(paragraphText, incorrectAlignments));
                }
            }
        } catch (Exception e) {
            allErrors.add(buildException(e));
        }

        return allErrors;
    }

    /**
     * Creates and returns a file-level error when the document cannot be opened or read.
     *
     * @param e the exception that was thrown
     * @return the created format error
     */
    private static FormatError buildException(Exception e) {
        FormatError error = new FormatError();
        error.setId("err_000");
        error.setCategory(ErrorCategory.FILE);
        error.setSeverity("error");
        error.setTitle("Помилка відкриття файлу: " + e.getMessage());
        error.setParagraphText("");
        return error;
    }

    /**
     * Creates and returns an alignment error for a paragraph with invalid alignment values.
     *
     * @param paragraphText the paragraph text
     * @param incorrectAlignments the set of detected alignment values
     * @return the created format error
     */
    private static FormatError buildAlignmentError(String paragraphText, Set<String> incorrectAlignments) {
        FormatError error = new FormatError();
        error.setId("err_alignment");
        error.setCategory(ErrorCategory.ALIGNMENT);
        error.setSeverity("error");
        error.setTitle("Невірне вирівнювання тексту");
        error.setParagraphText(paragraphText);
        error.setFound(incorrectAlignments);

        // Return only one of the equivalent alignment names for typical UI rendering (e.g. "DISTRIBUTE")
        error.setExpected("DISTRIBUTE");
        return error;
    }

    /**
     * Validates a paragraph against the expected alignment.
     *
     * @param paragraph the paragraph to validate
     * @return a set with detected alignment values when the paragraph is invalid
     */
    Set<String> validate(XWPFParagraph paragraph) {
        Set<String> incorrectAlignments = new HashSet<>();
        String actualAlignment = getAlignment(paragraph);

        String styleId = paragraph.getStyle();
        if (styleId != null && styleId.equals("Heading1") && actualAlignment.equals("CENTER")) {
            return incorrectAlignments;
        }

        if (!RequirementsHolder.getAlignment().containsKey(actualAlignment)) {
            incorrectAlignments.add(actualAlignment);
        }

        return incorrectAlignments;
    }

    /**
     * Resolves the paragraph alignment using paragraph properties or styles.
     * Priority order: <p>
     * 1. Explicit alignment in paragraph properties (PPr) <p>
     * 2. Alignment from paragraph style chain (style → base styles → default style) <p>
     * <p>
     * If alignment is not configured anywhere, "LEFT" is returned as default.
     *
     * @param paragraph the paragraph to inspect
     * @return the resolved alignment value (e.g., "LEFT", "RIGHT", "CENTER", "BOTH", "DISTRIBUTE")
     */
    String getAlignment(XWPFParagraph paragraph) {
        String alignment = getAlignmentFromPPr(paragraph.getCTP().getPPr());
        if (alignment != null) {
            return alignment;
        }

        alignment = getAlignmentFromStyles(paragraph);
        return Objects.requireNonNullElse(alignment, "LEFT");

    }

    /**
     * Resolves alignment from the paragraph style chain, including the default paragraph style.
     *
     * @param paragraph the paragraph to inspect
     * @return the resolved alignment value, or {@code null} if none is available
     */
    String getAlignmentFromStyles(XWPFParagraph paragraph) {
        XWPFStyles styles = paragraph.getDocument().getStyles();
        if (styles == null) {
            return null;
        }

        String styleId = paragraph.getStyle();
        if (styleId == null) {
            styleId = "Normal";
        }
        while (styleId != null) {
            XWPFStyle style = styles.getStyle(styleId);
            if (style == null || style.getCTStyle() == null) {
                break;
            }

            String fromStyle = getAlignmentFromPPr(style.getCTStyle().getPPr());
            if (fromStyle != null) {
                return fromStyle;
            }

            if (!style.getCTStyle().isSetBasedOn() || style.getCTStyle().getBasedOn() == null) {
                break;
            }

            styleId = style.getCTStyle().getBasedOn().getVal();
        }

        if (styles.getDefaultParagraphStyle() != null) {
            return getAlignmentFromPPr(styles.getDefaultParagraphStyle().getPPr());
        }

        return null;
    }

    /**
     * Checks if the paragraph properties or its alignment (Jc) component are missing.
     *
     * @param pPr the base paragraph properties
     * @return true if alignment is absent, false otherwise
     */
    boolean isAlignmentMissing(CTPPrBase pPr) {
        return pPr == null || !pPr.isSetJc() || pPr.getJc() == null || pPr.getJc().getVal() == null;
    }

    /**
     * Reads alignment from paragraph properties.
     *
     * @param pPr the paragraph properties
     * @return the resolved alignment value, or {@code null} if not present
     */
    String getAlignmentFromPPr(CTPPr pPr) {
        if (isAlignmentMissing(pPr)) {
            return null;
        }
        return pPr.getJc().getVal().toString().toUpperCase();
    }

    /**
     * Reads alignment from general paragraph properties.
     *
     * @param pPr the general paragraph properties
     * @return the resolved alignment value, or {@code null} if not present
     */
    String getAlignmentFromPPr(CTPPrGeneral pPr) {
        if (isAlignmentMissing(pPr)) {
            return null;
        }
        return pPr.getJc().getVal().toString().toUpperCase();
    }
}
