package com.cmps.thesischecker.checker;

import com.cmps.thesischecker.model.ErrorCategory;
import com.cmps.thesischecker.model.FormatError;
import com.cmps.thesischecker.model.ListLevelState;
import com.cmps.thesischecker.model.MarkerDefinition;
import com.cmps.thesischecker.model.NumberingFormat;
import com.cmps.thesischecker.requirements.RequirementsHolder;
import com.cmps.thesischecker.utils.StyleUtils;
import org.apache.poi.xwpf.usermodel.XWPFAbstractNum;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFNum;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFStyle;
import org.apache.poi.xwpf.usermodel.XWPFStyles;
import org.apache.poi.xwpf.usermodel.XWPFNumbering;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTAbstractNum;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTLvl;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTNumPr;

import java.io.FileInputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class ListChecker implements Checker {

    /**
     * Returns the error category for this instance.
     *
     * @return {@link ErrorCategory#LIST_FORMATTING} indicating list-formatting issues
     */
    @Override
    public ErrorCategory getErrorCategory() {
        return ErrorCategory.LIST_FORMATTING;
    }

    /**
     * Runs all list-formatting checks against the given .docx file.
     *
     * @param filePath absolute or relative path to the .docx file to check
     * @return the list of format errors found; empty if the document has no list
     *         formatting issues or contains no lists
     */
    @Override
    public List<FormatError> check(String filePath) {
        List<FormatError> allErrors = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(filePath);
             XWPFDocument doc = new XWPFDocument(fis)) {

            Map<BigInteger, ListLevelState> stateByNumId = new HashMap<>();

            for (XWPFParagraph paragraph : doc.getParagraphs()) {
                String paragraphText = paragraph.getText().trim();
                if (paragraphText.isEmpty()) {
                    continue;
                }

                ListInfo listInfo = resolveListInfo(paragraph);
                if (listInfo == null) {
                    continue;
                }

                List<MarkerDefinition> markers = resolveMarkerDefinitions(doc, listInfo.numId(), listInfo.ilvl());
                if (markers.isEmpty()) {
                    continue;
                }

                ListLevelState state = stateByNumId.computeIfAbsent(listInfo.numId(), ignored -> new ListLevelState());

                checkLevelSkip(paragraphText, listInfo.ilvl(), state, allErrors);

                Set<String> resolvedFormats = extractFormats(markers);
                if (resolvedFormats.size() > 1) {
                    allErrors.add(buildFormatMismatchError(paragraphText, resolvedFormats));
                    continue;
                }

                String resolvedFormat = resolvedFormats.iterator().next();
                checkFormatConsistency(paragraphText, listInfo.ilvl(), resolvedFormat, state, allErrors);

                if (state.markMarkerValidated(listInfo.ilvl())) {
                    MarkerDefinition representativeMarker = markers.getFirst();
                    FormatError markerError = validateMarker(paragraphText, representativeMarker);
                    if (markerError != null) {
                        allErrors.add(markerError);
                    }
                }
            }
        } catch (Exception e) {
            allErrors.add(buildException(e));
        }

        return allErrors;
    }

    /**
     * Checks whether the current paragraph's indentation level skips over one or more
     * levels relative to the last level seen for the same list, and records the current
     * level as the new last-seen level.
     *
     * @param paragraphText text of the paragraph being checked, used for error reporting
     * @param ilvl          indentation level of the current paragraph
     * @param state         running state for the list this paragraph belongs to
     * @param allErrors     list of errors to append to if a level skip is detected
     */
    private void checkLevelSkip(String paragraphText, BigInteger ilvl, ListLevelState state, List<FormatError> allErrors) {
        Integer lastLevel = state.getLastLevel();
        int currentLevel = ilvl.intValue();

        boolean levelSkipped = (lastLevel != null && currentLevel > lastLevel + 1)
                || (lastLevel == null && currentLevel > 0);

        if (levelSkipped) {
            allErrors.add(buildLevelSkipError(paragraphText, currentLevel));
        }

        state.setLastLevel(currentLevel);
    }

    /**
     * Checks that the numbering format observed for a given level is consistent with the
     * format previously recorded for the same level, and records the format if this is
     * the first paragraph seen at this level.
     *
     * @param paragraphText  text of the paragraph being checked, used for error reporting
     * @param ilvl           indentation level of the current paragraph
     * @param resolvedFormat numbering format resolved for the current paragraph
     * @param state          running state for the list this paragraph belongs to
     * @param allErrors      list of errors to append to if a format mismatch is detected
     */
    private void checkFormatConsistency(String paragraphText, BigInteger ilvl, String resolvedFormat,
                                         ListLevelState state, List<FormatError> allErrors) {
        String expectedFormat = state.getExpectedFormat(ilvl);
        if (expectedFormat == null) {
            state.recordFormat(ilvl, resolvedFormat);
            return;
        }

        if (!expectedFormat.equalsIgnoreCase(resolvedFormat)) {
            allErrors.add(buildFormatMismatchError(paragraphText, Set.of(resolvedFormat)));
        }
    }

    /**
     * Validates a marker definition against the standard's rules for in-text lists:
     * the format must be one of the allowed formats, decimal/lowerLetter markers must be
     * closed with a bracket, and bullet markers must use a dash character.
     * <p>
     * Note: exclusion of specific Ukrainian letters (є, з, і, ї, й, о, ч, ь) for the
     * lowerLetter format is not checked here, since the actual letter used for a given
     * paragraph is computed by Word from its position in the sequence and is not stored
     * explicitly per paragraph in the document XML.
     *
     * @param paragraphText text of the paragraph being checked, used for error reporting
     * @param marker        marker definition resolved for the paragraph's list level
     * @return a format error describing the first rule violation found, or null if the
     *         marker satisfies all checked rules
     */
    private FormatError validateMarker(String paragraphText, MarkerDefinition marker) {
        if (!marker.isFormatAllowed()) {
            return buildMarkerFormatError(paragraphText, marker);
        }

        if (marker.isBullet()) {
            if (!marker.hasValidBulletChar()) {
                String foundChar = marker.getLvlText() != null ? marker.getLvlText().trim() : "";
                return buildBulletCharError(paragraphText, foundChar);
            }
            return null;
        }

        if (!marker.hasValidClosingSymbol()) {
            return buildMarkerSuffixError(paragraphText, marker.getLvlText());
        }

        return null;
    }

    /**
     * Builds the error reported when the checker itself fails to open or parse the file.
     *
     * @param e the exception that was caught while opening or reading the document
     * @return a format error describing the file-level failure
     */
    private static FormatError buildException(Exception e) {
        FormatError error = new FormatError();
        error.setId("err_000");
        error.setCategory(ErrorCategory.FILE);
        error.setSeverity("error");
        error.setTitle("Помилка відкриття файлу: " + e.getMessage());
        error.setParagraphText("");
        return error;
    }

    /**
     * Builds the error reported when a paragraph's indentation level skips over one or
     * more levels relative to the previous paragraph in the same list.
     *
     * @param paragraphText text of the paragraph where the skip was detected
     * @param foundLevel    indentation level found in the paragraph
     * @return a format error describing the level-skip violation
     */
    private static FormatError buildLevelSkipError(String paragraphText, int foundLevel) {
        FormatError error = new FormatError();
        error.setId("err_list_level_skip");
        error.setCategory(ErrorCategory.LIST_FORMATTING);
        error.setSeverity("error");
        error.setTitle("Пропущено рівень вкладеності списку");
        error.setParagraphText(paragraphText);
        error.setFound(Set.of(String.valueOf(foundLevel)));
        error.setExpected(RequirementsHolder.getListLevelStep());
        return error;
    }

    /**
     * Builds the error reported when a list level uses more than one numbering format,
     * or a format different from the one previously recorded for that level.
     *
     * @param paragraphText text of the paragraph where the mismatch was detected
     * @param foundFormats  the numbering format(s) found for the paragraph's level
     * @return a format error describing the format-mismatch violation
     */
    private static FormatError buildFormatMismatchError(String paragraphText, Set<String> foundFormats) {
        FormatError error = new FormatError();
        error.setId("err_list_format");
        error.setCategory(ErrorCategory.LIST_FORMATTING);
        error.setSeverity("error");
        error.setTitle("Невідповідний формат нумерації на одному рівні списку");
        error.setParagraphText(paragraphText);
        error.setFound(foundFormats);
        error.setExpected(RequirementsHolder.getListFormattingConsistency());
        return error;
    }

    /**
     * Builds the error reported when a list level uses a numbering format that is not
     * one of the formats allowed by the standard.
     *
     * @param paragraphText text of the paragraph where the disallowed format was found
     * @param marker        the marker definition resolved for the paragraph's level;
     *                      used to extract a human-readable value for the 'found' field
     * @return a format error describing the disallowed-format violation
     */
    private static FormatError buildMarkerFormatError(String paragraphText, MarkerDefinition marker) {
        FormatError error = new FormatError();
        error.setId("err_list_marker_format");
        error.setCategory(ErrorCategory.LIST_FORMATTING);
        error.setSeverity("error");
        error.setTitle("Недозволений формат позначення переліку");
        error.setParagraphText(paragraphText);

        String displayedFound = marker.getFriendlyFormat();
        if (NumberingFormat.OTHER.getDisplayName().equals(displayedFound)) {
            displayedFound = marker.getFormat();
        }

        error.setFound(Set.of(displayedFound));
        error.setExpected(RequirementsHolder.getListAllowedMarkerFormats());
        return error;
    }

    /**
     * Builds the error reported when a decimal or lowerLetter marker is closed with a
     * period instead of the required bracket.
     *
     * @param paragraphText text of the paragraph where the wrong closing symbol was found
     * @param foundLvlText  the raw level text pattern found for the paragraph's level
     * @return a format error describing the wrong-closing-symbol violation
     */
    private static FormatError buildMarkerSuffixError(String paragraphText, String foundLvlText) {
        FormatError error = new FormatError();
        error.setId("err_list_marker_suffix");
        error.setCategory(ErrorCategory.LIST_FORMATTING);
        error.setSeverity("error");
        error.setTitle("Пункт переліку має закінчуватися дужкою, а не крапкою");
        error.setParagraphText(paragraphText);

        String displayedFound;
        if (foundLvlText == null) {
            displayedFound = "";
        } else {
            String t = foundLvlText.trim();
            // If level text is a placeholder pattern like "%1." extract the punctuation only
            if (t.matches(".*%\\d+\\.$")) {
                displayedFound = ".";
            } else if (t.matches(".*%\\d+\\)$")) {
                displayedFound = ")";
            } else if (t.length() == 1) {
                displayedFound = t;
            } else {
                char last = t.charAt(t.length() - 1);
                if (last == '.' || last == ')') {
                    displayedFound = String.valueOf(last);
                } else {
                    displayedFound = t;
                }
            }
        }

        error.setFound(Set.of(displayedFound));
        error.setExpected(RequirementsHolder.getListMarkerClosingSymbol());
        return error;
    }

    /**
     * Builds the error reported when a bullet-style marker uses a symbol other than an
     * allowed dash character.
     *
     * @param paragraphText text of the paragraph where the disallowed bullet symbol was found
     * @param foundChar     the raw level text (bullet character) found for the paragraph's level
     * @return a format error describing the disallowed-bullet-character violation
     */
    private static FormatError buildBulletCharError(String paragraphText, String foundChar) {
        FormatError error = new FormatError();
        error.setId("err_list_bullet_char");
        error.setCategory(ErrorCategory.LIST_FORMATTING);
        error.setSeverity("error");
        error.setTitle("Недозволений символ маркера переліку — має бути тире");
        error.setParagraphText(paragraphText);
        error.setFound(Set.of(foundChar));
        error.setExpected(RequirementsHolder.getListBulletChar());
        return error;
    }

    /**
     * Resolves the numbering id and indentation level that apply to a paragraph, either
     * from the paragraph's own direct numbering properties or, failing that, from its
     * style hierarchy. (Walking up the "based on" chain until numbering properties are
     * found or the chain ends.)
     *
     * @param paragraph the paragraph to resolve list information for
     * @return the resolved numbering id and indentation level, or null if the paragraph
     *         is not part of a numbered list
     */
    private ListInfo resolveListInfo(XWPFParagraph paragraph) {
        BigInteger numId = paragraph.getNumID();
        BigInteger ilvl = paragraph.getNumIlvl();

        if (numId != null && ilvl != null) {
            return new ListInfo(numId, ilvl);
        }

        XWPFStyles styles = paragraph.getDocument().getStyles();
        if (styles == null) {
            return null;
        }

        String styleId = paragraph.getStyle();
        if (styleId == null) {
            styleId = StyleUtils.getNormalStyleId(styles);
        }

        while (styleId != null) {
            XWPFStyle style = styles.getStyle(styleId);
            if (style == null || style.getCTStyle() == null) {
                break;
            }

            CTNumPr numPr = getNumPr(style.getCTStyle().getPPr());
            if (numId == null && numPr != null && numPr.getNumId() != null && numPr.getNumId().getVal() != null) {
                numId = numPr.getNumId().getVal();
            }
            if (ilvl == null && numPr != null && numPr.getIlvl() != null && numPr.getIlvl().getVal() != null) {
                ilvl = numPr.getIlvl().getVal();
            }

            if (numId != null && ilvl != null) {
                break;
            }

            if (!style.getCTStyle().isSetBasedOn() || style.getCTStyle().getBasedOn() == null) {
                break;
            }

            styleId = style.getCTStyle().getBasedOn().getVal();
        }

        if (numId == null || ilvl == null) {
            return null;
        }

        return new ListInfo(numId, ilvl);
    }

    /**
     * Extracts the numbering properties from a style's paragraph properties, if present.
     *
     * @param pPr the style's general paragraph properties, may be null
     * @return the numbering properties defined on the style, or null if none are set
     */
    private CTNumPr getNumPr(org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPPrGeneral pPr) {
        if (pPr == null || !pPr.isSetNumPr() || pPr.getNumPr() == null) {
            return null;
        }
        return pPr.getNumPr();
    }

    /**
     * Resolves all marker definitions declared for a given numbering id and indentation
     * level in the document's abstract numbering definitions. Normally returns a single
     * definition; may return more than one if the document declares conflicting entries
     * for the same level.
     *
     * @param document the document to read numbering definitions from
     * @param numId    the numbering id referenced by the paragraph
     * @param ilvl     the indentation level referenced by the paragraph
     * @return the list of marker definitions found for this numId/ilvl combination;
     *         empty if no numbering or no matching level definition exists
     */
    private List<MarkerDefinition> resolveMarkerDefinitions(XWPFDocument document, BigInteger numId, BigInteger ilvl) {
        XWPFNumbering numbering = document.getNumbering();
        if (numbering == null) {
            return List.of();
        }

        XWPFNum num = numbering.getNum(numId);
        if (num == null || num.getCTNum() == null || num.getCTNum().getAbstractNumId() == null
                || num.getCTNum().getAbstractNumId().getVal() == null) {
            return List.of();
        }

        BigInteger abstractNumId = num.getCTNum().getAbstractNumId().getVal();
        XWPFAbstractNum abstractNum = numbering.getAbstractNum(abstractNumId);
        if (abstractNum == null) {
            return List.of();
        }

        CTAbstractNum ctAbstractNum = abstractNum.getCTAbstractNum();
        if (ctAbstractNum == null) {
            return List.of();
        }

        List<MarkerDefinition> markers = new ArrayList<>();
        for (CTLvl lvl : ctAbstractNum.getLvlList()) {
            if (lvl.getIlvl() != null && Objects.equals(lvl.getIlvl(), ilvl) && lvl.getNumFmt() != null
                    && lvl.getNumFmt().getVal() != null) {
                markers.add(MarkerDefinition.fromCTLvl(lvl));
            }
        }

        return markers;
    }

    /**
     * Extracts the distinct numbering formats used across a set of marker definitions.
     *
     * @param markers marker definitions to extract formats from
     * @return the set of distinct (lower-cased) numbering formats found
     */
    private Set<String> extractFormats(List<MarkerDefinition> markers) {
        Set<String> formats = new LinkedHashSet<>();
        for (MarkerDefinition marker : markers) {
            formats.add(marker.getFormat());
        }
        return formats;
    }

    /**
     * Resolved numbering id and indentation level for a single paragraph.
     *
     * @param numId the numbering id the paragraph belongs to
     * @param ilvl  the indentation level of the paragraph within that numbering
     */
    private record ListInfo(BigInteger numId, BigInteger ilvl) {
    }
}
