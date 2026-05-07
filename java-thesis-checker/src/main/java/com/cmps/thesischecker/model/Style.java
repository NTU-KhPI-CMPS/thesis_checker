package com.cmps.thesischecker.model;

import lombok.Getter;

import java.util.Set;

/**
 * Enum containing known document styles with their localized aliases.
 */
@Getter
public enum Style {
    NORMAL("Normal", Set.of("a")),
    HEADING_1("Heading1", Set.of("1"));

    @Getter
    private final String primaryId;
    private final Set<String> aliases;

    Style(String primaryId, Set<String> aliases) {
        this.primaryId = primaryId;
        this.aliases = aliases;
    }

    /**
     * Checks if the given style ID matches this style's primary ID or any of its aliases, ignoring case.
     *
     * @param styleId the style ID to check
     * @return true if the style ID matches any alias, false otherwise
     */
    public boolean matches(String styleId) {
        if (styleId == null) {
            return false;
        }
        return styleId.equalsIgnoreCase(primaryId) ||
                aliases.stream().anyMatch(alias -> alias.equalsIgnoreCase(styleId));
    }
}
