package com.cmps.thesischecker.model;

import lombok.Getter;

@Getter
public class ParagraphSpacing {
    private final Double topSpacing;
    private final Double bottomSpacing;

    public ParagraphSpacing(Double topSpacing, Double bottomSpacing) {
        this.topSpacing = topSpacing;
        this.bottomSpacing = bottomSpacing;
    }

    public boolean checkTopBottomSpacing() {
        return topSpacing == null && bottomSpacing == null;
    }
}
