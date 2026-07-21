package com.cmps.thesischecker.utils;

import com.cmps.thesischecker.model.Style;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFStyle;
import org.apache.poi.xwpf.usermodel.XWPFStyles;

import java.util.List;

/**
 * Utility class for working with document styles, providing methods to retrieve style IDs based on known style aliases.
 */
public class StyleUtils {

    /**
     * Retrieves the style ID for the "Normal" style by checking against known aliases. <p>
     * If no matching style is found, returns the provided default ID.
     *
     * @param styles    the XWPFStyles object containing all styles in the document
     * @return the style ID for the "Normal" style, or the default ID if no match is found
     */
    public static String getNormalStyleId(XWPFStyles styles) {
        return Style.NORMAL.getAliases().stream()
                .filter(alias -> styles.getStyle(alias) != null)
                .findFirst()
                .orElse(Style.NORMAL.getPrimaryId());
    }

    /**
     * Checks if a paragraph uses the Heading 1 style by checking its style ID or localized name.
     *
     * @param paragraph the paragraph to evaluate
     * @return {@code true} if the paragraph is styled as Heading 1; {@code false} otherwise
     */
    public static boolean isHeading1(XWPFParagraph paragraph) {
        if (paragraph == null) {
            return false;
        }

        String styleId = paragraph.getStyleID();
        if (Style.HEADING_1.matches(styleId)) {
            return true;
        }

        if (paragraph.getDocument() != null && styleId != null) {
            XWPFStyles styles = paragraph.getDocument().getStyles();
            if (styles != null) {
                XWPFStyle style = styles.getStyle(styleId);
                if (style != null && style.getName() != null) {
                    String styleName = style.getName().trim().toLowerCase();
                    return Style.HEADING_1.getAliases().stream()
                            .anyMatch(alias -> alias.trim().toLowerCase().equals(styleName));
                }
            }
        }

        return false;
    }
}
