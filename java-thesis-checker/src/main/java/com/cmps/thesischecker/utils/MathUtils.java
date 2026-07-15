package com.cmps.thesischecker.utils;

import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTP;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for working with formula (OMath) paragraphs.
 */
public class MathUtils {

    private MathUtils() {
    }

    /**
     * Checks whether the given paragraph contains at least one formula (OMath element).
     *
     * @param paragraph the paragraph to inspect
     * @return true if the paragraph contains a formula
     */
    public static boolean isFormulaOnlyParagraph(XWPFParagraph paragraph) {
        return !getFormulaXmls(paragraph).isEmpty();
    }

    /**
     * Checks whether the given paragraph is empty text-wise and contains no formulas.
     *
     * @param paragraph the paragraph to inspect
     * @return true if the paragraph has no visible text and no formulas
     */
    public static boolean isBlankParagraph(XWPFParagraph paragraph) {
        String text = paragraph.getText();
        boolean textIsEmpty = text == null || text.trim().isEmpty();
        return textIsEmpty && getFormulaXmls(paragraph).isEmpty();
    }

    /**
     * Checks whether the given paragraph is a notation-explanation paragraph,
     * i.e. one that starts with the word "де" as required by п. 6.3.2.4 of the standard
     * (explanation of formula symbols, introduced with "де" without a colon).
     *
     * @param paragraph the paragraph to inspect
     * @return true if the paragraph text starts with "де"
     */
    public static boolean isNotationParagraph(XWPFParagraph paragraph) {
        String text = paragraph.getText();
        if (text == null) {
            return false;
        }
        String trimmed = text.trim();
        return trimmed.equals("де") || trimmed.startsWith("де ") || trimmed.startsWith("де,") || trimmed.startsWith("де –") || trimmed.startsWith("де —");
    }

    /**
     * Collects the raw XML of every OMath element contained directly in the paragraph.
     *
     * @param paragraph the paragraph to inspect
     * @return list of OMath XML strings found in the paragraph, empty if none
     */
    public static List<String> getFormulaXmls(XWPFParagraph paragraph) {
        List<String> formulaXmls = new ArrayList<>();

        CTP ctp = paragraph.getCTP();
        if (ctp == null) {
            return formulaXmls;
        }

        ctp.getOMathParaList().forEach(oMathPara ->
                oMathPara.getOMathList().forEach(oMath -> formulaXmls.add(oMath.xmlText())));

        return formulaXmls;
    }
}
