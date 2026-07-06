package com.cmps.thesischecker.model;

import lombok.Getter;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTLvl;

import java.util.Locale;
import java.util.Set;

/**
 * Represents the marker definition (numbering format and level text pattern) resolved
 * for a single list level from the document's abstract numbering definition. <br>
 *
 * Encapsulates the university formatting rules for in-text lists. Allowed formats
 * include "decimal", "lowerLetter" and "bullet". For decimal/lowerLetter levels
 * a closing bracket ')' OR a placeholder without trailing punctuation (for example
 * the lvlText "%1") is acceptable for lists consisting of completed phrases
 * (see п. 6.2.9). Bullet levels must use an allowed dash character.
 */
@Getter
public class MarkerDefinition {

    private static final Set<String> ALLOWED_FORMATS = Set.of(
            "decimal", "lowerletter", "bullet", "russianlower", "ukrainianlower"
    );
    private static final Set<Character> ALLOWED_DASH_CHARS = Set.of('-', '–', '—');

    private final String format;
    private final String lvlText;

    /**
     * Creates a marker definition.
     *
     * @param format  lower-cased numbering format value (e.g. "decimal", "bullet")
     * @param lvlText raw level text pattern (e.g. "%1)", "%1.", or a literal bullet character); may be null
     */
    public MarkerDefinition(String format, String lvlText) {
        this.format = format;
        this.lvlText = lvlText;
    }

    /**
     * Builds a marker definition by reading the numbering format and level text pattern
     * from a POI/OOXML abstract numbering level.
     *
     * @param level the abstract numbering level to read; must have a non-null numFmt
     * @return a new marker definition instance describing the given level
     */
    public static MarkerDefinition fromCTLvl(CTLvl level) {
        String format = level.getNumFmt().getVal().toString().toLowerCase(Locale.ROOT);
        String text = (level.getLvlText() != null && level.getLvlText().getVal() != null)
                ? level.getLvlText().getVal()
                : null;
        return new MarkerDefinition(format, text);
    }

    /**
     * Checks whether the numbering format is one of the three formats allowed by
     * the standard for in-text lists.
     *
     * @return true if the format is decimal, lowerLetter or bullet; false otherwise
     */
    public boolean isFormatAllowed() {
        return ALLOWED_FORMATS.contains(format);
    }

    /**
     * @return true if this marker uses the bullet numbering format, false otherwise
     */
    public boolean isBullet() {
        return "bullet".equals(format);
    }

    /**
     * Checks whether the bullet character used is one of the allowed dash characters.
     * Only meaningful when {@link #isBullet()} returns true.
     *
     * @return true if the level text is exactly one allowed dash character, or if the
     *         level text is not defined; false if it is any other symbol
     */
    public boolean hasValidBulletChar() {
        if (lvlText == null) {
            return true;
        }
        String trimmed = lvlText.trim();
        return trimmed.length() == 1 && ALLOWED_DASH_CHARS.contains(trimmed.charAt(0));
    }

    /**
     * Checks whether a decimal or lowerLetter marker uses an allowed closing symbol.
     * Per the standard, either a closing bracket ')' or no trailing punctuation
     * (i.e. a plain number/letter like "1", "2", ...) is acceptable for
     * completed-phrase lists (see п. 6.2.9). Only meaningful when the format is
     * decimal or lowerLetter.
     *
     * @return true if the level text ends with ")", or represents a placeholder
     *         without trailing punctuation (e.g. "%1"), or if the level text is not defined;
     *         false otherwise
     */
    public boolean hasValidClosingSymbol() {
        if (lvlText == null) {
            return true;
        }

        String trimmed = lvlText.trim();

        if ("decimal".equals(format) || "lowerletter".equals(format) ||
                "russianlower".equals(format) || "ukrainianlower".equals(format)) {

            // Allowed: explicit closing bracket ")"
            if (trimmed.endsWith(")")) {
                return true;
            }
            // Allowed: placeholder without trailing punctuation, e.g. "%1"
            return trimmed.matches(".*%\\d+$");
            // Disallow patterns ending with a period or other punctuation (e.g. "%1.")
        }

        return true;
    }

    /**
     * Returns a human-friendly description of the numbering format (for reporting).
     * Example: "римські числа (верхній регістр)" for upperRoman.
     *
     * @return display name for the resolved numbering format
     */
    public String getFriendlyFormat() {
        return NumberingFormat.fromCode(format).getDisplayName();
    }
}
