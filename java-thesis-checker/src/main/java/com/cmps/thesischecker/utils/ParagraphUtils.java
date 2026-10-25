package com.cmps.thesischecker.utils;

import org.apache.poi.xwpf.usermodel.XWPFParagraph;

import java.util.List;
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

    /**
     * Returns the text of the given paragraph if it is not blank.
     * Otherwise, searches for the nearest non-blank paragraph in the provided
     * list (looking at previous and next paragraphs) and returns its text.
     * If no non-blank paragraph is found, returns an empty string.
     *
     * @param paragraph  the paragraph to get text from
     * @param paragraphs the list of all paragraphs in document order
     * @return the paragraph text, or nearest non-blank paragraph text, or empty string
     */
    public static String getNearestParagraphText(XWPFParagraph paragraph, List<XWPFParagraph> paragraphs) {
        String text = getTrimmedText(paragraph);
        if (!text.isEmpty()) {
            return text;
        }

        int index = paragraphs.indexOf(paragraph);
        if (index < 0) {
            return "ERROR: Cannot find nearest to image text. Index is < 0";
        }

        for (int offset = 1; offset < paragraphs.size(); offset++) {
            int prevIdx = index - offset;
            if (prevIdx >= 0) {
                String prevText = getTrimmedText(paragraphs.get(prevIdx));
                if (!prevText.isEmpty()) {
                    return prevText;
                }
            }

            int nextIdx = index + offset;
            if (nextIdx < paragraphs.size()) {
                String nextText = getTrimmedText(paragraphs.get(nextIdx));
                if (!nextText.isEmpty()) {
                    return nextText;
                }
            }
        }

        return "There is no text in the document with the incorrectly formatted image.";
    }
}
