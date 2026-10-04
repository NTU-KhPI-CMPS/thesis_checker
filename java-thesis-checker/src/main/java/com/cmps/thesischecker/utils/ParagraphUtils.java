package com.cmps.thesischecker.utils;

import org.apache.poi.xwpf.usermodel.XWPFParagraph;

import java.util.regex.Pattern;

public class ParagraphUtils {

    /**
     * Returns the visible text of a paragraph without surrounding whitespace.
     *
     * @param paragraph the paragraph to inspect
     * @return the trimmed paragraph text, or an empty string if no text is available
     */
    public static String getTrimmedText(XWPFParagraph paragraph) {
        if (paragraph == null) {
            return "";
        }

        String text = paragraph.getText();
        return text != null ? text.trim() : "";
    }

    /**
     * Checks whether a paragraph has no visible text.
     *
     * @param paragraph the paragraph to inspect
     * @return true if the paragraph is empty after trimming
     */
    public static boolean isBlank(XWPFParagraph paragraph) {
        return getTrimmedText(paragraph).isEmpty();
    }

    /**
     * Checks whether the given paragraph contains text matching the specified
     * regular expression pattern. The paragraph text is trimmed before matching,
     * and empty paragraphs are considered invalid.
     *
     * @param paragraph the paragraph to inspect
     * @param pattern the regular expression pattern used to search the paragraph text
     * @return true if the paragraph is not empty and contains a match for the pattern
     */
    public static boolean checkParagraphByPattern(XWPFParagraph paragraph, Pattern pattern) {
        if (ParagraphUtils.isBlank(paragraph)) {
            return false;
        }

        String trimmedText = ParagraphUtils.getTrimmedText(paragraph);
        return pattern.matcher(trimmedText).find();
    }
}
