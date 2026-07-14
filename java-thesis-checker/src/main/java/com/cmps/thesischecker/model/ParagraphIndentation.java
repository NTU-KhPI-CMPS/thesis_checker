package com.cmps.thesischecker.model;

import lombok.Getter;

@Getter
public class ParagraphIndentation {
    private final Double leftIndentation;
    private final Double rightIndentation;

    public ParagraphIndentation(Double leftSpacing, Double rightSpacing) {
        this.leftIndentation = leftSpacing;
        this.rightIndentation = rightSpacing;
    }

    public boolean checkLeftRightIndentation() {
        return leftIndentation == null && rightIndentation == null;
    }
}
