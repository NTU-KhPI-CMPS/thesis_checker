
package com.cmps.thesischecker.utils;

import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.openxmlformats.schemas.officeDocument.x2006.math.CTOMath;
import org.openxmlformats.schemas.officeDocument.x2006.math.CTOMathPara;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTP;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
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
            Pattern.compile("^[\\p{L}\\p{N}]{1,10}\\h*[–—-]\\h*\\S.*");

    // Real formula number: "chapter.number", both natural numbers WITHOUT leading zeros,
    // chapter >= 1. This rejects decimals such as (0.01), (0.05), (1.05) used in prose.
    private static final String FORMULA_NUMBER = "[1-9]\\d*\\.[1-9]\\d*";

    public static final Pattern PLAIN_FORMULA_NUMBER_PATTERN =
            Pattern.compile("\\(\\h*" + FORMULA_NUMBER + "\\h*\\)[.,;]?\\h*$");

    // a word of 3+ letters right before the "(" means the number is part of a sentence
    private static final Pattern PROSE_WORD_BEFORE_NUMBER =
            Pattern.compile("\\p{L}{3,}\\h*$");

    // two words in a row right before the "(" ("рівні значущості", "Див. формулу") => a sentence
    private static final Pattern TWO_WORDS_BEFORE_NUMBER =
            Pattern.compile("\\p{L}{2,}\\p{P}?\\h+\\p{L}{3,}\\h*$");

    // a single word of 3+ letters used as an operand/function: operator right before it
    // ("= RMS", "· sin", optionally one single-letter operand in between: "x = a sin")
    private static final Pattern OPERATOR_BEFORE_WORD =
            Pattern.compile("[=+\\u2212×·÷/^<>≤≥*]\\h*(?:\\p{L}\\h+)?\\p{L}{3,}\\h*$");

    // tab or 2+ spaces before the "(" is the typical layout of a manually typed formula number
    private static final Pattern GAP_BEFORE_NUMBER =
            Pattern.compile("(\\t|\\h{2,})$");

    public static final Pattern DASH_FIRST_SYMBOL =
            Pattern.compile("^\\p{Pd}");

    private static final Pattern TYPED_NUMBER_AT_END =
            Pattern.compile("\\(\\h*(" + FORMULA_NUMBER + ")\\h*\\)[.,;]?\\h*$");

    private static final Pattern WORD_3_PLUS = Pattern.compile("\\p{L}{3,}");

    private static final Pattern FORMULA_NUMBER_PATTERN =
            Pattern.compile("\\(\\h*" + FORMULA_NUMBER + "\\h*\\)");

    private static final Pattern MANUAL_FORMULA_EXPRESSION_PATTERN =
            Pattern.compile(
                    "[\\p{L}\\p{N})]\\h*[=≠≤≥≈+−×÷*/^∝∈]\\h*[\\p{L}\\p{N}(]"
                            + "|[\\p{L}\\p{N}]\\h*[²³⁴₀₁₂₃₄₅₆₇₈₉]"
            );

    // a unit / remark in brackets at the end of a formula: "(грн)", "(ГРН)", "(кг)", "(м/с)", "(%)".
    // Letters are required, digits are not allowed, so a number such as "(1.1)" never matches.
    private static final Pattern TRAILING_UNIT_BRACKET =
            Pattern.compile("\\(\\h*[%°/.\\h²³]*\\p{L}[\\p{L}%°/.\\h²³]*\\)[.,;]?\\h*$");

    // content of a delimiter that is a unit: letters only, at least two ("грн", "кг")
    private static final Pattern UNIT_ONLY_PATTERN =
            Pattern.compile("^\\p{L}{2,}[\\p{L}%°/.\\h²³]*$");

    private FormulaUtils() {
    }

    /**
     * Tells whether the text ends with a unit in brackets, e.g. "(грн)", "(ГРН)" or "(кг)".
     * A bracket with a number such as "(1.1)" is not a unit.
     *
     * @param text the paragraph text
     * @return true if the text ends with a bracket that holds letters and no digits
     */
    public static boolean endsWithUnitBracket(String text) {
        return text != null && TRAILING_UNIT_BRACKET.matcher(text.strip()).find();
    }

    /**
     * Tells whether the content of a formula delimiter is a unit ("грн", "кг"): letters only, 2 or more.
     *
     * @param inner the text inside the delimiter, without the brackets
     * @return true if it is a unit
     */
    public static boolean isUnitText(String inner) {
        return inner != null && UNIT_ONLY_PATTERN.matcher(inner.strip()).matches();
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

        String withoutNumber = PLAIN_FORMULA_NUMBER_PATTERN
                .matcher(outsideText.stripTrailing())
                .replaceFirst("");

        // single letters ("x", "n") can be typed as plain text next to a display formula,
        // so only a real word (3+ letters) counts as prose
        return WORD_3_PLUS.matcher(withoutNumber).find();
    }

    /**
     * Collects the text of a paragraph in document order: ordinary text runs ({@code w:t}),
     * tabs and the text of math runs ({@code m:t}) exactly where they stand.
     *
     * @param paragraph the source paragraph
     * @return the text with formulas included, or an empty string if it cannot be read
     */
    public static String orderedParagraphText(XWPFParagraph paragraph) {
        try {
            String xml = paragraph.getCTP().xmlText();
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(false);
            Document document = factory.newDocumentBuilder()
                    .parse(new InputSource(new StringReader(xml)));

            StringBuilder builder = new StringBuilder();
            appendOrderedText(document.getDocumentElement(), builder);
            return builder.toString();
        } catch (Exception e) {
            return "";
        }
    }

    private static void appendOrderedText(Node node, StringBuilder builder) {
        if (node.getNodeType() != Node.ELEMENT_NODE) {
            return;
        }

        String name = ((Element) node).getTagName();
        switch (name) {
            case "w:t", "m:t" -> {
                builder.append(node.getTextContent());
                return;
            }
            case "w:tab" -> {
                builder.append('\t');
                return;
            }
            // properties (incl. tab stop definitions in w:pPr), deleted text and field codes carry no visible text
            case "w:pPr", "w:rPr", "m:rPr", "w:delText", "w:instrText" -> {
                return;
            }
            default -> {
            }
        }

        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            appendOrderedText(children.item(i), builder);
        }
    }

    /**
     * Tells whether a formula ends with a unit in brackets ("(грн)") instead of a number.
     * Looks at the typed text ("... (грн)" next to the formula object, or literal brackets inside the formula)
     * and at a formula delimiter that holds only a unit and stands at the end of the formula.
     *
     * @param paragraph   the paragraph that contains the formula
     * @param formulaXmls raw XML of the OMath elements of the paragraph
     * @return true if the formula ends with a unit in brackets
     */
    public static boolean hasUnitInBrackets(XWPFParagraph paragraph, List<String> formulaXmls) {
        if (FormulaUtils.endsWithUnitBracket(orderedParagraphText(paragraph))) {
            return true;
        }

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(false);

            for (String xml : formulaXmls) {
                Document document = factory.newDocumentBuilder()
                        .parse(new InputSource(new StringReader(xml)));
                String flat = flattenElementText(document.getDocumentElement()).trim();

                NodeList delimiters = document.getElementsByTagName("m:d");
                for (int i = 0; i < delimiters.getLength(); i++) {
                    String inner = flattenElementText((Element) delimiters.item(i)).trim();
                    if (FormulaUtils.isUnitText(inner) && flat.endsWith(inner)) {
                        return true;
                    }
                }
            }
        } catch (Exception ignored) {
        }
        return false;
    }

    /**
     * Concatenates the text of every m:t descendant of the given element.
     *
     * @param element the element whose descendant m:t text should be collected
     * @return the combined text from all descendant math text nodes
     */
    public static String flattenElementText(Element element) {
        NodeList texts = element.getElementsByTagName("m:t");
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < texts.getLength(); i++) {
            Node node = texts.item(i);
            String t = node.getTextContent();
            if (t != null) builder.append(t);
        }
        return builder.toString();
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
        Matcher m = PLAIN_FORMULA_NUMBER_PATTERN.matcher(rtrimmed);
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
