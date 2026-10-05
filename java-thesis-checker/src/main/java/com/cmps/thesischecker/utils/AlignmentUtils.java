package com.cmps.thesischecker.utils;

import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFStyles;
import org.apache.poi.xwpf.usermodel.XWPFStyle;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTP;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPPrBase;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPPrGeneral;

/**
 * Utility class for working with paragraph alignment and drawing detection.
 */
public class AlignmentUtils {

    private AlignmentUtils() {
    }

    /**
     * Checks if the paragraph properties or its alignment (Jc) component are missing.
     *
     * @param pPr the base paragraph properties
     * @return true if alignment is absent, false otherwise
     */
    private static boolean isAlignmentMissing(CTPPrBase pPr) {
        return pPr == null || !pPr.isSetJc() || pPr.getJc() == null || pPr.getJc().getVal() == null;
    }

    /**
     * Reads alignment from paragraph properties.
     *
     * @param pPr the paragraph properties
     * @return the resolved alignment value, or {@code null} if not present
     */
    private static String getAlignmentFromPPr(CTPPr pPr) {
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
    private static String getAlignmentFromPPr(CTPPrGeneral pPr) {
        if (isAlignmentMissing(pPr)) {
            return null;
        }
        return pPr.getJc().getVal().toString().toUpperCase();
    }

    /**
     * Resolves the paragraph alignment using paragraph properties or styles.
     * Priority order:
     * 1. Explicit alignment in paragraph properties (PPr)
     * 2. Alignment from paragraph style chain (style → base styles → default style)
     * <p>
     * If alignment is not configured anywhere, "LEFT" is returned as default.
     *
     * @param paragraph the paragraph to inspect
     * @return the resolved alignment value (e.g., "LEFT", "RIGHT", "CENTER", "BOTH", "DISTRIBUTE")
     */
    public static String getAlignment(XWPFParagraph paragraph) {
        String alignment = getAlignmentFromPPr(paragraph.getCTP().getPPr());
        if (alignment != null) {
            return alignment;
        }

        alignment = getAlignmentFromStyles(paragraph);
        return alignment == null ? "LEFT" : alignment;
    }

    /**
     * Resolves alignment from the paragraph style chain, including the default paragraph style.
     *
     * @param paragraph the paragraph to inspect
     * @return the resolved alignment value, or {@code null} if none is available
     */
    private static String getAlignmentFromStyles(XWPFParagraph paragraph) {
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
     * Checks if the paragraph contains a drawing (figure).
     * Excludes formulas (OMML, OLE objects, or formula-like paragraphs with numbering) from being considered as drawings.
     *
     * @param paragraph the paragraph to inspect
     * @return true if the paragraph contains at least one drawing
     */
    public static boolean hasDrawing(XWPFParagraph paragraph) {
        CTP ctp = paragraph.getCTP();
        if (ctp == null) {
            return false;
        }
        String xml = ctp.xmlText();
        if (xml.contains("<w:object") || xml.contains("<m:oMath")) {
            return false;
        }
        if (!xml.contains("<w:drawing")) {
            return false;
        }
        if (FormulaUtils.isFormulaOnlyParagraph(paragraph)
                || FormulaUtils.paragraphHasTrailingFormulaNumber(paragraph)) {
            return false;
        }
        return true;
    }
}
