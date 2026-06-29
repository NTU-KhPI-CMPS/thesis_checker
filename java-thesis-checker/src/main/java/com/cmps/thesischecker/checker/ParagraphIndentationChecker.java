package com.cmps.thesischecker.checker;

import com.cmps.thesischecker.model.ErrorCategory;
import com.cmps.thesischecker.model.FormatError;
import com.cmps.thesischecker.model.ParagraphIndentation;
import com.cmps.thesischecker.requirements.RequirementsHolder;
import com.cmps.thesischecker.utils.StyleUtils;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFStyle;
import org.apache.poi.xwpf.usermodel.XWPFStyles;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTInd;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPPrGeneral;

import java.io.FileInputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ParagraphIndentationChecker implements Checker {

    /**
     * Returns the error category for this checker.
     *
     * @return {@link ErrorCategory#INDENTATION}
     */
    @Override
    public ErrorCategory getErrorCategory() {
        return ErrorCategory.INDENTATION;
    }

    /**
     * Checks a Word document for paragraph indentation (left/right) issues.
     *
     * @param filePath path to the .docx file to check
     * @return list of found format errors, empty if none
     */
    @Override
    public List<FormatError> check(String filePath) {
        List<FormatError> allErrors = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(filePath);
             XWPFDocument doc = new XWPFDocument(fis)) {

            for (XWPFParagraph paragraph : doc.getParagraphs()) {
                if (paragraph.getText().trim().isEmpty()) {
                    continue;
                }

                allErrors.addAll(validate(paragraph));
            }
        } catch (Exception e) {
            allErrors.add(buildException(e));
        }

        return allErrors;
    }

    /**
     * Validates paragraph indentation and returns errors if indentation is incorrect.
     *
     * @param paragraph the paragraph to validate
     * @return list of errors, empty if indentation is correct
     */
    private List<FormatError> validate(XWPFParagraph paragraph) {
        List<FormatError> errors = new ArrayList<>();
        ParagraphIndentation spacing = getIndentation(paragraph);

        if (!spacing.checkLeftRightSpacing()) {
            String text = paragraph.getText().trim();

            double expectedParagraphIndentations = Double.parseDouble(RequirementsHolder.getParagraphIndentations());

            if (spacing.getLeftSpacing() != null && spacing.getLeftSpacing() > expectedParagraphIndentations) {
                errors.add(buildIndentationLeftError(text, spacing.getLeftSpacing()));
            }
            if (spacing.getRightSpacing() != null && spacing.getRightSpacing() > expectedParagraphIndentations) {
                errors.add(buildIndentationRightError(text, spacing.getRightSpacing()));
            }
        }

        return errors;
    }

    /**
     * Builds a format error for incorrect left indentation.
     *
     * @param paragraphText the text of the paragraph with the error
     * @param found         the incorrect indentation value found, in cm
     * @return the created format error
     */
    private static FormatError buildIndentationLeftError(String paragraphText, Double found) {
        FormatError error = new FormatError();
        error.setId("err_indentation_left");
        error.setCategory(ErrorCategory.INDENTATION);
        error.setSeverity("error");
        error.setTitle("Невірний відступ зліва");
        error.setParagraphText(paragraphText);
        error.setFound(Set.of(String.format("%.1f см", found)));
        error.setExpected("0 см");
        return error;
    }

    /**
     * Builds a format error for incorrect right indentation.
     *
     * @param paragraphText the text of the paragraph with the error
     * @param found         the incorrect indentation value found, in cm
     * @return the created format error
     */
    private static FormatError buildIndentationRightError(String paragraphText, Double found) {
        FormatError error = new FormatError();
        error.setId("err_indentation_right");
        error.setCategory(ErrorCategory.INDENTATION);
        error.setSeverity("error");
        error.setTitle("Невірний відступ справа");
        error.setParagraphText(paragraphText);
        error.setFound(Set.of(String.format("%.1f см", found)));
        error.setExpected("0 см");
        return error;
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
     * Resolves effective paragraph indentation from direct formatting or styles.
     *
     * @param paragraph the paragraph to inspect
     * @return resolved indentation, fields are null if not set
     */
    private ParagraphIndentation getIndentation(XWPFParagraph paragraph) {
        ParagraphIndentation spacing = getIndentationFromPPr(paragraph.getCTP().getPPr());
        if (spacing != null) {
            return spacing;
        }

        spacing = getIndentationFromStyles(paragraph);
        if (spacing != null) {
            return spacing;
        }

        return new ParagraphIndentation(null, null);
    }

    /**
     * Resolves indentation from the paragraph style chain, including the default paragraph style.
     *
     * @param paragraph the paragraph to inspect
     * @return the resolved indentation, or {@code null} if none is available
     */
    private ParagraphIndentation getIndentationFromStyles(XWPFParagraph paragraph) {
        XWPFStyles styles = paragraph.getDocument().getStyles();
        if (styles == null) {
            return null;
        }

        String styleId = paragraph.getStyle();
        if (styleId == null) {
            styleId = StyleUtils.getNormalStyleId(styles);
        }

        while (styleId != null) {
            XWPFStyle style = styles.getStyle(styleId);
            if (style == null || style.getCTStyle() == null) {
                break;
            }

            ParagraphIndentation fromStyle = getIndentationFromPPr(style.getCTStyle().getPPr());
            if (fromStyle != null) {
                return fromStyle;
            }

            if (!style.getCTStyle().isSetBasedOn() || style.getCTStyle().getBasedOn() == null) {
                break;
            }

            styleId = style.getCTStyle().getBasedOn().getVal();
        }

        if (styles.getDefaultParagraphStyle() != null) {
            return getIndentationFromPPr(styles.getDefaultParagraphStyle().getPPr());
        }

        return null;
    }

    /**
     * Reads indentation from paragraph properties.
     *
     * @param pPr the paragraph properties
     * @return the resolved indentation, or {@code null} if not present
     */
    private ParagraphIndentation getIndentationFromPPr(CTPPr pPr) {
        if (pPr == null) {
            return null;
        }
        return getSpacingFromCTInd(pPr.getInd());
    }

    /**
     * Reads indentation from general paragraph properties.
     *
     * @param pPr the general paragraph properties
     * @return the resolved indentation, or {@code null} if not present
     */
    private ParagraphIndentation getIndentationFromPPr(CTPPrGeneral pPr) {
        if (pPr == null) {
            return null;
        }
        return getSpacingFromCTInd(pPr.getInd());
    }

    /**
     * Extracts left/right indentation from CTInd and converts from twips to cm.
     *
     * @param spacing the raw indentation definition from Word
     * @return ParagraphIndentation with values in cm, or null if indentation is absent
     */
    private ParagraphIndentation getSpacingFromCTInd(CTInd spacing) {
        if (spacing == null) {
            return null;
        }

        Double left = spacing.getLeft() != null
                ? ((BigInteger) spacing.getLeft()).doubleValue() / 567.0
                : null;

        Double right = spacing.getRight() != null
                ? ((BigInteger) spacing.getRight()).doubleValue() / 567.0
                : null;

        if (left == null && right == null) {
            return null;
        }

        return new ParagraphIndentation(left, right);
    }
}
