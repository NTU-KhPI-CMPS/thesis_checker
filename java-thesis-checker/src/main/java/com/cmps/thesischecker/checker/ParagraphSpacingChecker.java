package com.cmps.thesischecker.checker;

import com.cmps.thesischecker.model.ErrorCategory;
import com.cmps.thesischecker.model.FormatError;
import com.cmps.thesischecker.model.ParagraphSpacing;
import com.cmps.thesischecker.requirements.RequirementsHolder;
import com.cmps.thesischecker.utils.StyleUtils;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFStyle;
import org.apache.poi.xwpf.usermodel.XWPFStyles;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPPrGeneral;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSpacing;

import java.io.FileInputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ParagraphSpacingChecker implements Checker {

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
     * Validates paragraph spacing and returns errors if spacing is incorrect.
     *
     * @param paragraph the paragraph to validate
     * @return list of errors, empty if spacing is correct
     */
    List<FormatError> validate(XWPFParagraph paragraph) {
        List<FormatError> errors = new ArrayList<>();
        ParagraphSpacing spacing = getSpacing(paragraph);

        if (!spacing.checkSpacing()) {
            String text = paragraph.getText().trim();

            double expectedParagraphSpacing = Double.parseDouble(RequirementsHolder.getParagraphSpacing());

            if (spacing.getUpSpacing() != null && spacing.getUpSpacing() > expectedParagraphSpacing) {
                errors.add(buildSpacingBeforeError(text, spacing.getUpSpacing()));
            }
            if (spacing.getBottomSpacing() != null && spacing.getBottomSpacing() > expectedParagraphSpacing) {
                errors.add(buildSpacingAfterError(text, spacing.getBottomSpacing()));
            }
        }

        return errors;
    }

    /**
     * Resolves effective paragraph spacing from direct formatting or styles.
     *
     * @param paragraph the paragraph to inspect
     * @return resolved spacing, fields are null if not set
     */
    ParagraphSpacing getSpacing(XWPFParagraph paragraph) {
        ParagraphSpacing spacing = getSpacingFromPPr(paragraph.getCTP().getPPr());
        if (spacing != null) {
            return spacing;
        }

        spacing = getSpacingFromStyles(paragraph);
        if (spacing != null) {
            return spacing;
        }

        return new ParagraphSpacing(null, null);
    }

    /**
     * Resolves spacing from the paragraph style chain, including the default paragraph style.
     *
     * @param paragraph the paragraph to inspect
     * @return the resolved spacing, or {@code null} if none is available
     */
    ParagraphSpacing getSpacingFromStyles(XWPFParagraph paragraph) {
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

            ParagraphSpacing fromStyle = getSpacingFromPPr(style.getCTStyle().getPPr());
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
     * @return the resolved spacing, or {@code null} if not present
     */
    ParagraphSpacing getSpacingFromPPr(CTPPr pPr) {
        if (pPr == null) {
            return null;
        }
        return getSpacingFromCTSpacing(pPr.getSpacing());
    }

    /**
     * Reads spacing from general paragraph properties.
     *
     * @param pPr the general paragraph properties
     * @return the resolved spacing, or {@code null} if not present
     */
    ParagraphSpacing getSpacingFromPPr(CTPPrGeneral pPr) {
        if (pPr == null) {
            return null;
        }
        return getSpacingFromCTSpacing(pPr.getSpacing());
    }

    /**
     * Extracts before/after spacing from CTSpacing and converts from twips to points.
     *
     * @param spacing the raw spacing definition from Word
     * @return ParagraphSpacing with values in points, or null if spacing is absent
     */
    ParagraphSpacing getSpacingFromCTSpacing(CTSpacing spacing) {
        if (spacing == null) {
            return null;
        }

        Double before = spacing.getBefore() != null
                ? ((BigInteger) spacing.getBefore()).doubleValue() / 20.0
                : null;

        Double after = spacing.getAfter() != null
                ? ((BigInteger) spacing.getAfter()).doubleValue() / 20.0
                : null;

        if (before == null && after == null) {
            return null;
        }

        return new ParagraphSpacing(before, after);
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
     * Builds a format error for incorrect spacing before paragraph.
     *
     * @param paragraphText the text of the paragraph with the error
     * @param found         the incorrect spacing value found in points
     * @return the created format error
     */
    private static FormatError buildSpacingBeforeError(String paragraphText, Double found) {
        FormatError error = new FormatError();
        error.setId("err_spacing_before");
        error.setCategory(ErrorCategory.INDENTATION);
        error.setSeverity("error");
        error.setTitle("Невірний відступ перед абзацом");
        error.setParagraphText(paragraphText);
        error.setFound(Set.of(found + " pt"));
        error.setExpected("0 pt");
        return error;
    }

    /**
     * Builds a format error for incorrect spacing after paragraph.
     *
     * @param paragraphText the text of the paragraph with the error
     * @param found         the incorrect spacing value found in points
     * @return the created format error
     */
    private static FormatError buildSpacingAfterError(String paragraphText, Double found) {
        FormatError error = new FormatError();
        error.setId("err_spacing_after");
        error.setCategory(ErrorCategory.INDENTATION);
        error.setSeverity("error");
        error.setTitle("Невірний відступ після абзацу");
        error.setParagraphText(paragraphText);
        error.setFound(Set.of(found + " pt"));
        error.setExpected("0 pt");
        return error;
    }
}
