package com.cmps.thesischecker.model;

import lombok.Getter;

import java.util.Set;

/**
 * Enum containing known document styles with their localized aliases.
 */
@Getter
public enum KnownStyle {
    NORMAL(Set.of("Normal", "a")),
    HEADING_1(Set.of("Heading1", "1"));

    private final Set<String> aliases;

    KnownStyle(Set<String> aliases) {
        this.aliases = aliases;
    }

    public boolean matches(String styleId) {
        if (styleId == null) {
            return false;
        }
        return aliases.stream().anyMatch(alias -> alias.equalsIgnoreCase(styleId));
    }
}
