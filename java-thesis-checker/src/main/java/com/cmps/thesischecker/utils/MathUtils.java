package com.cmps.thesischecker.utils;

import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTP;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Utility class for working with formula (OMath) paragraphs.
 */
public class MathUtils {

    // "symbol – description;" — line-continuation of the block of explanation of symbols (6.3.2.4),
    // e.g. "м — маса;". Short "symbol" (up to 10 characters), dash, and then text.
    private static final Pattern NOTATION_CONTINUATION_PATTERN =
            Pattern.compile("^[\\p{L}\\p{N}]{1,10}\\s*[–—-]\\s*\\S.*");

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
     * Checks whether the given paragraph is the FIRST line of a notation-explanation block,
     * i.e. one that starts with the word "де" as required by п. 6.3.2.4 of the standard
     * (explanation of formula symbols, introduced with "де" without a colon — though we
     * tolerate a colon too, since it's a common real-world deviation).
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
        if (trimmed.length() < 2 || !trimmed.regionMatches(true, 0, "де", 0, 2)) {
            return false;
        }
        if (trimmed.length() == 2) {
            return true;
        }

        // "де" is considered the beginning of an explanation, only if it is not followed by a letter
        // (space, comma, colon, dash, etc.), otherwise it is just a word with "де-"
        // (e.g. "detailed", "democracy")
        return !Character.isLetter(trimmed.charAt(2));
    }

    /**
     * Checks whether the given paragraph is a CONTINUATION line of a notation block
     * (either the "де ..." line itself, or a subsequent "символ – опис;" line, e.g.
     * "m — маса;"), as opposed to unrelated prose that just happens to follow it.
     *
     * @param paragraph the paragraph to inspect
     * @return true if the paragraph looks like part of a notation-explanation block
     */
    public static boolean isNotationContinuationLine(XWPFParagraph paragraph) {
        if (isNotationParagraph(paragraph)) {
            return true;
        }

        String text = paragraph.getText();
        if (text == null) {
            return false;
        }

        String trimmed = text.trim();
        return !trimmed.isEmpty() && NOTATION_CONTINUATION_PATTERN.matcher(trimmed).matches();
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
