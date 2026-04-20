package com.cmps.thesischecker.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.Set;

/**
 * Represents a formatting error found in the thesis document.
 *
 * @author Mariia Borodin (HappyMary16)
 * @since 1.0
 */
@Getter
@Setter
@RequiredArgsConstructor
public class FormatError {

    private String id;
    private ErrorCategory category;
    private String severity;
    private String title;
    private String paragraphText;
    private Set<String> found;
    private String expected;
}
