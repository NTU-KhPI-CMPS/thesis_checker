package com.cmps.thesischecker.model;

import lombok.Getter;

import java.util.Set;

/**
 * Enum containing known document styles with their localized aliases.
 */
@Getter
public enum Style {
    NORMAL(Set.of("Normal", "a")),
    HEADING_1(Set.of("Heading1", "1"));

    private final Set<String> aliases;

    Style(Set<String> aliases) {
        this.aliases = aliases;
    }

    /**
     * Checks if the given style ID matches any of the known aliases for this style, ignoring case.
     *
     * @param styleId the style ID to check
     * @return true if the style ID matches any alias, false otherwise
     */
    public boolean matches(String styleId) {
        if (styleId == null) {
            return false;
        }
        return aliases.stream().anyMatch(alias -> alias.equalsIgnoreCase(styleId));
    }
}
