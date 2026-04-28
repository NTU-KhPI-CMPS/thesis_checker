package com.cmps.thesischecker.checker;

import java.io.FileInputStream;
import java.math.BigInteger;
import java.util.*;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFStyle;
import org.apache.poi.xwpf.usermodel.XWPFStyles;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPPrGeneral;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSpacing;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STLineSpacingRule;

import com.cmps.thesischecker.model.ErrorCategory;
import com.cmps.thesischecker.model.FormatError;
import com.cmps.thesischecker.requirements.RequirementsHolder;

public class LineSpaceChecker implements Checker {

    /**
     * Checks the document for line-spacing violations and returns all found format errors.
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
                Set<String> incorrectLineSpacings = validate(paragraph);
                if (!incorrectLineSpacings.isEmpty()) {
                    allErrors.add(buildLineSpaceError(paragraphText, incorrectLineSpacings));
                }
            }
        } catch (Exception e) {
            allErrors.add(buildLineSpaceException(e));
        }

        return allErrors;
    }

    /**
     * Creates and returns a file-level error when the document cannot be opened or read.
     *
     * @param e the exception that was thrown
     * @return the created format error
     */
    private static FormatError buildLineSpaceException(Exception e) {
        FormatError error = new FormatError();
        error.setId("err_000");
        error.setCategory(ErrorCategory.FILE);
        error.setSeverity("error");
        error.setTitle("Помилка відкриття файлу: " + e.getMessage());
        error.setParagraphText("");
        return error;
    }

    /**
     * Creates and returns a line-spacing error for a paragraph with invalid spacing values.
     *
     * @param paragraphText the paragraph text
     * @param incorrectLineSpacings the set of detected spacing values
     * @return the created format error
     */
    private static FormatError buildLineSpaceError(String paragraphText, Set<String> incorrectLineSpacings) {
        FormatError error = new FormatError();
        error.setId("err_linespace");
        error.setCategory(ErrorCategory.LINE_SPACING);
        error.setSeverity("error");
        error.setTitle("Неправильні інтервали між рядками у параграфі");
        error.setParagraphText(paragraphText);
        error.setFound(incorrectLineSpacings);
        error.setExpected(RequirementsHolder.getLineSpacing());
        return error;
    }

    /**
     * Validates a paragraph against the expected line spacing.
     *
     * @param paragraph the paragraph to validate
     * @return a set with detected spacing values when the paragraph is invalid
     */
    Set<String> validate(XWPFParagraph paragraph) {
        Set<String> incorrectLineSpacings = new HashSet<>();
        String expectedLineSpacing = RequirementsHolder.getLineSpacing();

        Double spacing = getSpacing(paragraph);
        double finalValue = (spacing == null) ? 1.0 : spacing;

        if (Math.abs(finalValue - Double.parseDouble(expectedLineSpacing)) > 0.01) {
            incorrectLineSpacings.add(String.format(Locale.US, "%.2f", finalValue));
        }

        return incorrectLineSpacings;
    }

    /**
     * Resolves the paragraph spacing using paragraph properties, styles, or direct paragraph spacing.
     * Priority order: <p>
     * 1. Explicit spacing in paragraph properties (PPr) - if value > 0 <p>
     * 2. Spacing from paragraph style chain (style → base styles → default style) - if value > 0 <p>
     * 3. Paragraph's direct spacing (getSpacingBetween) <p>
     * <p>
     * If spacing is configured in both paragraph PPr and style, the paragraph's local setting takes precedence.
     *
     * @param paragraph the paragraph to inspect
     * @return the resolved spacing value, or {@code null} if none is available
     */
    Double getSpacing(XWPFParagraph paragraph) {
        Double spacing = getSpacingFromPPr(paragraph.getCTP().getPPr());
        if (spacing != null && spacing > 0) {
            return spacing;
        }

        spacing = getSpacingFromStyles(paragraph);
        if (spacing != null && spacing > 0) {
            return spacing;
        }

        spacing = paragraph.getSpacingBetween();
        return spacing > 0 ? spacing : null;
    }

    /**
     * Resolves spacing from the paragraph style chain, including the default paragraph style.
     *
     * @param paragraph the paragraph to inspect
     * @return the resolved spacing value, or {@code null} if none is available
     */
    Double getSpacingFromStyles(XWPFParagraph paragraph) {
        XWPFStyles styles = paragraph.getDocument().getStyles();
        if (styles == null) {
            return null;
        }

        String styleId = paragraph.getStyle();
        while (styleId != null) {
            XWPFStyle style = styles.getStyle(styleId);
            if (style == null || style.getCTStyle() == null) {
                break;
            }

            Double fromStyle = getSpacingFromPPr(style.getCTStyle().getPPr());
            if (fromStyle != null) {
                return fromStyle;
            }

            if (!style.getCTStyle().isSetBasedOn() || style.getCTStyle().getBasedOn() == null) {
                break;
            }

            styleId = style.getCTStyle().getBasedOn().getVal();
        }

        if (styles.getDefaultParagraphStyle() != null) {
            return getSpacingFromPPr(styles.getDefaultParagraphStyle().getPPr());
        }

        return null;
    }

    /**
     * Reads spacing from paragraph properties.
     *
     * @param pPr the paragraph properties
     * @return the resolved spacing value, or {@code null} if not present
     */
    Double getSpacingFromPPr(CTPPr pPr) {
        if (pPr == null) {
            return null;
        }
        return getSpacingFromCTSpacing(pPr.getSpacing());
    }

    /**
     * Reads spacing from general paragraph properties.
     *
     * @param pPr the general paragraph properties
     * @return the resolved spacing value, or {@code null} if not present
     */
    Double getSpacingFromPPr(CTPPrGeneral pPr) {
        if (pPr == null) {
            return null;
        }
        return getSpacingFromCTSpacing(pPr.getSpacing());
    }

    /**
     * Converts the raw Word spacing value into a normalized line-spacing factor.
     *
     * @param spacing the raw spacing definition from Word
     * @return normalized spacing value, or {@code null} if the value is missing or invalid
     */
    Double getSpacingFromCTSpacing(CTSpacing spacing) {
        if (spacing == null || spacing.getLine() == null) {
            return null;
        }

        double lineVal = ((BigInteger) spacing.getLine()).doubleValue();
        if (lineVal <= 0) {
            return null;
        }

        STLineSpacingRule.Enum rule = spacing.getLineRule();

        if (rule == null || rule.equals(STLineSpacingRule.AUTO)) {
            return lineVal / 240.0;
        } else {
            return lineVal / 20.0;
        }
    }
}
