package com.cmps.thesischecker.utils;

import org.apache.poi.xwpf.usermodel.BodyElementType;
import org.apache.poi.xwpf.usermodel.IBodyElement;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFTable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Extracts the actual, checkable content of a thesis document - skipping the
 * "front matter" pages that precede it, namely the title page and the
 * supervisor's review ("відгук керівника"), which may span one or several pages -
 * before any checker starts analyzing the content.
 * <p>
 * Apache POI has no notion of a rendered page, so counting pages directly would
 * require actually laying the document out (which POI cannot do). Instead, this
 * class looks for the first paragraph that matches one of a small set of known
 * heading markers ({@link #CONTENT_START_MARKERS}) which reliably signal the end
 * of the front matter and the start of the actual thesis text, regardless of how
 * many pages the title page and the review happened to occupy.
 * <p>
 * A candidate paragraph only counts as a genuine heading - and not, say, an
 * accidental mention of the same word inside the supervisor's review text - if
 * its entire (trimmed) content is exactly one of the markers AND it is written
 * fully in upper case. This mirrors how such section headings are actually
 * formatted in the document.
 * <p>
 * All paragraphs and tables located in the document body BEFORE that marker are
 * ignored by the checkers, since they belong to the title page / review section
 * rather than to the thesis content itself.
 */
public final class MainContentUtils {

    /**
     * Headings that mark the start of the actual thesis content. Everything located
     * before the first occurrence of any of these headings is treated as the title
     * page and/or the supervisor's review.
     * <p>
     * Not every thesis has all of these sections - some go straight to "ЗМІСТ",
     * others start with "РЕФЕРАТ" or "АНОТАЦІЯ" - so whichever heading is found
     * first, in document order, wins.
     */
    private static final Set<String> CONTENT_START_MARKERS =
            Set.of("ЗМІСТ", "CONTENT", "АНОТАЦІЯ", "ABSTRACT", "РЕФЕРАТ");

    private MainContentUtils() {
    }

    /**
     * Returns the body elements (paragraphs and tables) of the document in their
     * natural document order, starting from - and including - the first paragraph
     * that matches one of {@link #CONTENT_START_MARKERS}.
     * <p>
     * If none of the markers is found, the whole document is returned unfiltered
     * as a safe fallback, so content is never silently dropped.
     *
     * @param doc the Word document to filter
     * @return body elements starting at the first content-start marker, or all
     *         body elements if no marker was found
     */
    public static List<IBodyElement> getMainContentElements(XWPFDocument doc) {
        List<IBodyElement> elements = doc.getBodyElements();
        int startIndex = findContentStartIndex(elements);

        return startIndex < 0 ? elements : elements.subList(startIndex, elements.size());
    }

    /**
     * Same as {@link #getMainContentElements(XWPFDocument)}, but returns
     * only the paragraphs.
     *
     * @param doc the Word document to filter
     * @return paragraphs starting at the first content-start marker, or all
     *         paragraphs if no marker was found
     */
    public static List<XWPFParagraph> getMainContentParagraphs(XWPFDocument doc) {
        List<XWPFParagraph> result = new ArrayList<>();

        for (IBodyElement element : getMainContentElements(doc)) {
            if (element.getElementType() == BodyElementType.PARAGRAPH) {
                result.add((XWPFParagraph) element);
            }
        }

        return result;
    }

    /**
     * Same as {@link #getMainContentElements(XWPFDocument)}, but returns
     * only the tables.
     *
     * @param doc the Word document to filter
     * @return tables starting at the first content-start marker, or all tables if
     *         no marker was found
     */
    public static List<XWPFTable> getMainContentTables(XWPFDocument doc) {
        List<XWPFTable> result = new ArrayList<>();

        for (IBodyElement element : getMainContentElements(doc)) {
            if (element.getElementType() == BodyElementType.TABLE) {
                result.add((XWPFTable) element);
            }
        }

        return result;
    }

    /**
     * Finds the index, within the given list of body elements, of the first
     * paragraph that qualifies as a content-start heading.
     *
     * @param elements body elements of the document, in document order
     * @return index of the first matching paragraph, or {@code -1} if none of the
     *         markers was found
     */
    private static int findContentStartIndex(List<IBodyElement> elements) {
        for (int i = 0; i < elements.size(); i++) {
            IBodyElement element = elements.get(i);

            if (element.getElementType() == BodyElementType.PARAGRAPH) {
                String text = ((XWPFParagraph) element).getText().trim();

                if (isContentStartMarker(text)) {
                    return i;
                }
            }
        }

        return -1;
    }

    /**
     * Checks whether the given paragraph text qualifies as a content-start
     * heading: it must be exactly one of {@link #CONTENT_START_MARKERS} - the
     * only text in the paragraph, not just a mention somewhere inside a longer
     * sentence - and it must be written entirely in upper case, which is how
     * genuine section headings are formatted. This prevents a stray occurrence
     * of the same word inside the supervisor's review (e.g. as part of a
     * sentence) from being mistaken for the actual heading.
     *
     * @param paragraphText trimmed text of a paragraph
     * @return {@code true} if the text is a genuine content-start heading
     */
    private static boolean isContentStartMarker(String paragraphText) {
        if (paragraphText.isEmpty()) {
            return false;
        }

        boolean isUpperCase = paragraphText.equals(paragraphText.toUpperCase(Locale.ROOT));

        return isUpperCase && CONTENT_START_MARKERS.contains(paragraphText);
    }
}
