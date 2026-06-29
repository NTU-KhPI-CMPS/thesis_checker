package com.cmps.thesischecker.model;

import lombok.Getter;

@Getter
public class ParagraphIndentation {
    private final Double leftSpacing;
    private final Double rightSpacing;

    public ParagraphIndentation(Double leftSpacing, Double rightSpacing) {
        this.leftSpacing = leftSpacing;
        this.rightSpacing = rightSpacing;
    }

    public boolean checkLeftRightSpacing() {
        return leftSpacing == null && rightSpacing == null;
    }
}
