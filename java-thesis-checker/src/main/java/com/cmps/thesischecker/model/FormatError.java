package com.cmps.thesischecker.model;

import lombok.Builder;

/**
 * Describes moder with validation error info.
 *
 * @author Mariia Borodin (HappyMary16)
 * @since 1.0
 */
@Builder // generates builder https://refactoring.guru/uk/design-patterns/builder
public record FormatError(String id, String severity, String title) {
}
