package com.cmps.thesischecker.argparser;

public class ProgramArg {
    private final String name;
    private final boolean hasValue;
    private final String description;
    private String value;

    public ProgramArg(String name, boolean hasValue, String description) {
        this.name = name;
        this.hasValue = hasValue;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public boolean hasValue() {
        return hasValue;
    }

    public String getDescription() {
        return description;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isSet() {
        return value != null;
    }
}