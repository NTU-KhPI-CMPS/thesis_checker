package com.cmps.thesischecker.utils;

import com.cmps.thesischecker.model.DocumentHeader;
import com.cmps.thesischecker.requirements.RequirementsHolder;

import java.util.Locale;

/**
 * Helpers for locating the bibliography section ("СПИСОК ДЖЕРЕЛ ІНФОРМАЦІЇ") of a thesis.
 * <p>
 * The section starts at its heading and lasts until the next structural heading
 * (e.g. "ДОДАТОК А"). As in {@link MainContentUtils}, a paragraph counts as a heading only if
 * its whole text is the title written in upper case, so table-of-contents entries
 * (which carry page numbers) and ordinary sentences are not mistaken for headings.
 */
public final class BibliographyUtils {

    private static final String BIBLIOGRAPHY_TITLE = "СПИСОК ДЖЕРЕЛ ІНФОРМАЦІЇ";
    private static final String APPENDIX_TITLE = "ДОДАТОК";

    private BibliographyUtils() {
    }

    /**
     * @param paragraphText trimmed text of a paragraph
     * @return true if the paragraph is the bibliography heading
     */
    public static boolean isBibliographyHeading(String paragraphText) {
        return BIBLIOGRAPHY_TITLE.equals(normalizeHeading(paragraphText));
    }

    /**
     * @param paragraphText trimmed text of a paragraph
     * @return true if the paragraph is a structural heading other than the bibliography,
     *         i.e. a heading that ends the bibliography section
     */
    public static boolean isOtherStructuralHeading(String paragraphText) {
        String normalized = normalizeHeading(paragraphText);
        if (normalized.isEmpty() || BIBLIOGRAPHY_TITLE.equals(normalized)) {
            return false;
        }

        for (DocumentHeader header : RequirementsHolder.getOrderStructuredElements()) {
            String title = header.getTitle();
            if (APPENDIX_TITLE.equals(title)) {
                if (normalized.equals(title) || normalized.startsWith(title + " ")) {
                    return true;
                }
            } else if (normalized.equals(title)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Strips a trailing period and returns the text only if it is fully upper case;
     * otherwise returns an empty string (the paragraph cannot be a heading).
     */
    private static String normalizeHeading(String paragraphText) {
        String text = paragraphText.trim();
        if (text.endsWith(".")) {
            text = text.substring(0, text.length() - 1).trim();
        }
        return text.equals(text.toUpperCase(Locale.ROOT)) ? text : "";
    }
}
