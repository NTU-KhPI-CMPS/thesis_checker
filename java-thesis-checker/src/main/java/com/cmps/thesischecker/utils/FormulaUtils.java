package com.cmps.thesischecker.utils;

import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.openxmlformats.schemas.officeDocument.x2006.math.CTOMath;
import org.openxmlformats.schemas.officeDocument.x2006.math.CTOMathPara;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTP;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class for working with formula (OMath) paragraphs.
 */
public class FormulaUtils {

    // "symbol – description;" — line-continuation of the block of explanation of symbols (6.3.2.4),
    // e.g. "м — маса;". Short "symbol" (up to 10 characters), dash, and then text.
    public static final Pattern NOTATION_CONTINUATION_PATTERN =
            Pattern.compile("^[\\p{L}\\p{N}]{1,10}\\s*[–—-]\\s*\\S.*");
    // Real formula number: "chapter.number", both natural numbers WITHOUT leading zeros,
    // chapter >= 1. This rejects decimals such as (0.01), (0.05), (1.05) used in prose.
    private static final String FORMULA_NUMBER = "[1-9]\\d*\\.[1-9]\\d*";

    public static final Pattern PLAIN_FORMULA_NUMBER_PATTERN =
            Pattern.compile("\\(\\s*" + FORMULA_NUMBER + "\\s*\\)[.,;]?\\s*$");
    private static final Pattern TRAILING_NUMBER_START_PATTERN =
            Pattern.compile("\\(\\s*" + FORMULA_NUMBER + "\\s*\\)[.,;]?\\s*$");
    // a word of 3+ letters right before the "(" means the number is part of a sentence
    private static final Pattern PROSE_WORD_BEFORE_NUMBER =
            Pattern.compile("\\p{L}{3,}\\s*$");
    // two words in a row right before the "(" ("рівні значущості", "Див. формулу") => a sentence
    private static final Pattern TWO_WORDS_BEFORE_NUMBER =
            Pattern.compile("\\p{L}{2,}\\p{P}?\\s+\\p{L}{3,}\\s*$");
    // a single word of 3+ letters used as an operand/function: operator right before it
    // ("= RMS", "· sin", optionally one single-letter operand in between: "x = a sin")
    private static final Pattern OPERATOR_BEFORE_WORD =
            Pattern.compile("[=+\\u2212×·÷/^<>≤≥*]\\s*(?:\\p{L}\\s+)?\\p{L}{3,}\\s*$");
    // tab or 2+ spaces before the "(" is the typical layout of a manually typed formula number
    private static final Pattern GAP_BEFORE_NUMBER =
            Pattern.compile("(\\t|\\s{2,})$");
    public static final Pattern DASH_FIRST_SYMBOL =
            Pattern.compile("^\\p{Pd}");
    private static final Pattern TRAILING_NUMBER_AT_END =
            Pattern.compile("\\(\\s*" + FORMULA_NUMBER + "\\s*\\)[.,;]?\\s*$");
    private static final Pattern TYPED_NUMBER_AT_END =
            Pattern.compile("\\(\\s*(" + FORMULA_NUMBER + ")\\s*\\)[.,;]?\\s*$");

    private static final Pattern WORD_3_PLUS = Pattern.compile("\\p{L}{3,}");

    private static final Pattern FORMULA_NUMBER_PATTERN =
            Pattern.compile("\\(\\s*" + FORMULA_NUMBER + "\\s*\\)");

    private static final Pattern MANUAL_FORMULA_EXPRESSION_PATTERN =
            Pattern.compile(
                    "[\\p{L}\\p{N})]\\s*[=≠≤≥≈+−×÷*/^∝∈]\\s*[\\p{L}\\p{N}(]"
                            + "|[\\p{L}\\p{N}]\\s*[²³⁴₀₁₂₃₄₅₆₇₈₉]"
            );

    private FormulaUtils() {
    }

    /**
     * Returns the formula number that is typed as plain text next to the formula object
     * (e.g. {@code "(2.6)"} after a tab), or null if the paragraph has none.
     * <p>
     * {@code paragraph.getText()} does not contain the OMath content, so a match here is a number
     * placed outside the formula, never the formula's own brackets.
     *
     * @param paragraph the paragraph that contains a formula
     * @return the number without brackets (e.g. "2.6"), or null
     */
    public static String getTypedNumberOutsideFormula(XWPFParagraph paragraph) {
        Matcher m = TYPED_NUMBER_AT_END.matcher(ParagraphUtils.getTrimmedText(paragraph));
        return m.find() ? m.group(1) : null;
    }

    /**
     * Tells whether the formula objects of the paragraph are embedded in a sentence (inline),
     * as opposed to a display formula that occupies its own line and may carry a number.
     * <p>
     * {@code paragraph.getText()} does not contain the OMath content, so what is left is the text
     * around the formulas. For a display formula it is empty or just the typed number
     * ("(2.1)", maybe preceded by a tab / punctuation); for an inline formula it contains words.
     *
     * @param paragraph the paragraph that contains at least one formula
     * @return true if there are words around the formula, i.e. the formula is inline
     */
    public static boolean isInlineFormulaInProse(XWPFParagraph paragraph) {
        return hasProseOutsideFormula(ParagraphUtils.getTrimmedText(paragraph));
    }

    /**
     * Text-level part of {@link #isInlineFormulaInProse}: removes a trailing "(N.N)" and checks
     * whether a word of 3+ letters remains.
     *
     * @param outsideText paragraph text without the OMath content
     * @return true if the text contains a word of 3+ letters (prose)
     */
    public static boolean hasProseOutsideFormula(String outsideText) {
        if (outsideText == null) {
            return false;
        }
        String withoutNumber = TRAILING_NUMBER_AT_END.matcher(outsideText.stripTrailing()).replaceFirst("");
        // single letters ("x", "n") can be typed as plain text next to a display formula,
        // so only a real word (3+ letters) counts as prose
        return WORD_3_PLUS.matcher(withoutNumber).find();
    }

    /**
     * Detects a formula inserted as a picture: the paragraph has a picture, no OMath object,
     * and its whole text is a formula number such as "(2.6)". A figure with a caption never
     * matches, because captions contain words.
     *
     * @param paragraph the paragraph to inspect
     * @return true if the paragraph looks like "picture of a formula + its number"
     */
    public static boolean isImageFormulaParagraph(XWPFParagraph paragraph) {
        if (paragraphContainsFormula(paragraph) || !ParagraphUtils.paragraphHasPicture(paragraph)) {
            return false;
        }

        return paragraphHasTrailingFormulaNumber(paragraph);
    }

    /**
     * Checks whether the given paragraph contains at least one formula (OMath element).
     *
     * @param paragraph the paragraph to inspect
     * @return true if the paragraph contains a formula
     */
    public static boolean paragraphContainsFormula(XWPFParagraph paragraph) {
        return !getFormulaXmls(paragraph).isEmpty();
    }

    /**
     * Checks whether the given paragraph is empty text-wise and contains no formulas.
     *
     * @param paragraph the paragraph to inspect
     * @return true if the paragraph has no visible text and no formulas
     */
    public static boolean isBlankParagraph(XWPFParagraph paragraph) {
        return ParagraphUtils.isBlank(paragraph) && getFormulaXmls(paragraph).isEmpty();
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
        if (ParagraphUtils.isBlank(paragraph)) {
            return false;
        }

        String trimmed = ParagraphUtils.getTrimmedText(paragraph);

        if (paragraphContainsFormula(paragraph) && DASH_FIRST_SYMBOL.matcher(trimmed).find()) {
            return true;
        } if (trimmed.length() < 2 || !trimmed.regionMatches(true, 0, "де", 0, 2)) {
            return false;
        } if (trimmed.length() == 2) {
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

        return ParagraphUtils.checkParagraphByPattern(paragraph, NOTATION_CONTINUATION_PATTERN);
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

        for (CTOMath oMath : ctp.getOMathList()) {
            formulaXmls.add(oMath.xmlText());
        }

        for (CTOMathPara oMathPara : ctp.getOMathParaList()) {
            for (CTOMath oMath : oMathPara.getOMathList()) {
                formulaXmls.add(oMath.xmlText());
            }
        }

        return formulaXmls;
    }

    /**
     * Checks whether the given paragraph contains a formula number
     * matching the expected formula notation pattern, such as "(1.1)".
     *
     * @param paragraph the paragraph to inspect
     * @return true if the paragraph contains a formula number matching the pattern
     */
    public static boolean paragraphIsFormula(XWPFParagraph paragraph) {
        return ParagraphUtils.checkParagraphByPattern(paragraph, PLAIN_FORMULA_NUMBER_PATTERN);
    }

    /**
     * Checks whether the given paragraph ends with a formula number like "(1.1)",
     * even if there is regular text before it.
     *
     * @param paragraph the paragraph to inspect
     * @return true if the paragraph ends with a formula-style number
     */
    public static boolean paragraphHasTrailingFormulaNumber(XWPFParagraph paragraph) {
        if (ParagraphUtils.isBlank(paragraph)) {
            return false;
        }
        return isTrailingFormulaNumber(ParagraphUtils.getTrimmedText(paragraph));
    }

    /**
     * Text-level check behind {@link #paragraphHasTrailingFormulaNumber}: the text must end with
     * a valid "(N.N)" number AND that number must not be part of a sentence
     * (e.g. "... на рівні значущості (1.11).", "де p = 0.5 на рівні значущості (1.11)").
     *
     * @param text paragraph text (not trimmed of inner whitespace)
     * @return true if the text ends with a formula number rather than an in-prose value
     */
    public static boolean isTrailingFormulaNumber(String text) {
        if (text == null) {
            return false;
        }
        String rtrimmed = text.stripTrailing();
        Matcher m = TRAILING_NUMBER_START_PATTERN.matcher(rtrimmed);
        if (!m.find()) {
            return false;
        }

        String before = rtrimmed.substring(0, m.start());
        if (before.isBlank()) {
            return true;
        }
        if (GAP_BEFORE_NUMBER.matcher(before).find()) {
            return true;
        }
        if (!PROSE_WORD_BEFORE_NUMBER.matcher(before).find()) {
            return true;
        }
        // the text before the number ends with a word of 3+ letters: sentence or formula?
        // two words in a row => sentence; a single word right after an operator => formula (e.g. "σ = RMS");
        // a single word after a comma, digit or other text => sentence ("при n = 10 вибірка (1.11)")
        if (TWO_WORDS_BEFORE_NUMBER.matcher(before).find()) {
            return false;
        }
        return OPERATOR_BEFORE_WORD.matcher(before).find();
    }

    /**
     * Checks whether the given paragraph contains a manual formula (not an OMath object)
     * followed by a formula number like "(1.1)".
     *
     * @param paragraph the paragraph to inspect
     * @return true if the paragraph contains a manual formula followed by a formula number
     */
    public static boolean paragraphHasManualFormulaWithTrailingNumber(XWPFParagraph paragraph) {
        String text = ParagraphUtils.getTrimmedText(paragraph);
        Matcher matcher = FORMULA_NUMBER_PATTERN.matcher(text);

        while (matcher.find()) {
            String expression = text.substring(0, matcher.start()).trim();

            if (MANUAL_FORMULA_EXPRESSION_PATTERN.matcher(expression).find()) {
                return true;
            }
        }

        return false;
    }
}
