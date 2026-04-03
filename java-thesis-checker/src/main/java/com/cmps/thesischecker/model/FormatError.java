package com.cmps.thesischecker.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

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
    private String category;
    private String severity;
    private String title;
    private int paragraphIndex;
    private String paragraphText;
    private int highlightStart;
    private int highlightEnd;
    private String found;
    private String expected;
}
