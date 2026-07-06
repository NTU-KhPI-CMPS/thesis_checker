package com.cmps.thesischecker.model;

import lombok.Getter;

/**
 * Enumeration of numbering formats discovered in OOXML numFmt values.
 * Each enum has a raw OOXML code and a human-readable display name used in reports.
 * Use fromCode(String) to map a raw numFmt value to a corresponding enum instance.
 */
@Getter
public enum NumberingFormat {
    DECIMAL("decimal", "арабські цифри"),
    LOWER_LETTER("lowerletter", "літери (нижній регістр)"),
    UPPER_LETTER("upperletter", "літери (верхній регістр)"),
    LOWER_ROMAN("lowerroman", "римські числа (нижній регістр)"),
    UPPER_ROMAN("upperroman", "римські числа (верхній регістр)"),
    BULLET("bullet", "позначка (маркер)"),
    OTHER("other", "інший формат");

    private final String code;
    private final String displayName;

    NumberingFormat(String code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    /**
     * Maps a raw OOXML numbering-format code to the corresponding enum value.
     *
     * @param code raw numFmt value from the document XML
     * @return the matching numbering format, or {@link #OTHER} if the code is unknown
     */
    public static NumberingFormat fromCode(String code) {
        if (code == null) return OTHER;
        return switch (code.toLowerCase()) {
            case "decimal" -> DECIMAL;
            case "lowerletter", "lower-letter" -> LOWER_LETTER;
            case "upperletter", "upper-letter" -> UPPER_LETTER;
            case "lowerroman", "lower-roman" -> LOWER_ROMAN;
            case "upperroman", "upper-roman" -> UPPER_ROMAN;
            case "bullet" -> BULLET;
            default -> OTHER;
        };
    }
}
