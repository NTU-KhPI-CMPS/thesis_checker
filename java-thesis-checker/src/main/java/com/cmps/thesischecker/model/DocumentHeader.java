package com.cmps.thesischecker.model;

import lombok.Getter;

@Getter
public class DocumentHeader {

    final private String title;
    final private boolean isRequired;

    public DocumentHeader(String title, boolean isRequired) {
        this.title = title;
        this.isRequired = isRequired;
    }
}
