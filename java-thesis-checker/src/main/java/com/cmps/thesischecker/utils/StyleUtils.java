package com.cmps.thesischecker.utils;

import com.cmps.thesischecker.model.Style;
import org.apache.poi.xwpf.usermodel.XWPFStyles;

/**
 * Utility class for working with document styles, providing methods to retrieve style IDs based on known style aliases.
 */
public class StyleUtils {
    /**
     * Retrieves the style ID for the "Normal" style by checking against known aliases. <p>
 *     If no matching style is found, returns the provided default ID.
     *
     * @param styles    the XWPFStyles object containing all styles in the document
     * @param defaultId the default style ID to return if no match is found
     * @return the style ID for the "Normal" style, or the default ID if no match is found
     */
    public static String getNormalStyleId(XWPFStyles styles, String defaultId) {
        return Style.NORMAL.getAliases().stream()
                .filter(alias -> styles.getStyle(alias) != null)
                .findFirst()
                .orElse(defaultId);
    }
}
