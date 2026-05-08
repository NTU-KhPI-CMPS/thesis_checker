package com.cmps.thesischecker.model;

import lombok.Getter;

@Getter
public class ParagraphSpacing {
    private final Double upSpacing;
    private final Double bottomSpacing;

    public ParagraphSpacing(Double upSpacing, Double bottomSpacing) {
        this.upSpacing = upSpacing;
        this.bottomSpacing = bottomSpacing;
    }

    public boolean checkSpacing() {
        return upSpacing == null && bottomSpacing == null;
    }
}
