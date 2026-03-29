package com.cmps.thesischecker.argparser;

import java.util.HashMap;
import java.util.Map;

public class ArgumentParser {
    
    private static final String ARG_FONT = "--font";
    private static final String ARG_SIZE = "--size";
    private static final String ARG_LINE_SPACING = "--line-spacing";
    private static final String ARG_OUTPUT_DIR = "--output-dir";
    
    private final Map<String, ProgramArg> arguments;
    
    public ArgumentParser() {
        arguments = new HashMap<>();
        arguments.put(ARG_FONT, new ProgramArg(ARG_FONT, true, "Expected font name"));
        arguments.put(ARG_SIZE, new ProgramArg(ARG_SIZE, true, "Expected font size"));
        arguments.put(ARG_LINE_SPACING, new ProgramArg(ARG_LINE_SPACING, true, "Expected line spacing"));
        arguments.put(ARG_OUTPUT_DIR, new ProgramArg(ARG_OUTPUT_DIR, true, "Output directory for reports"));
    }
    
    public void parse(String[] args) {
        for (int i = 0; i < args.length; i++) {
            ProgramArg arg = arguments.get(args[i]);
            if (arg != null && arg.hasValue() && i + 1 < args.length) {
                arg.setValue(args[i + 1]);
                i++;
            }
        }
    }
    
    public String getFont() {
        return arguments.get(ARG_FONT).getValue();
    }
    
    public Integer getSize() {
        String value = arguments.get(ARG_SIZE).getValue();
        return value != null ? Integer.parseInt(value) : null;
    }
    
    public Double getLineSpacing() {
        String value = arguments.get(ARG_LINE_SPACING).getValue();
        return value != null ? Double.parseDouble(value) : null;
    }
    
    public String getOutputDir() {
        return arguments.get(ARG_OUTPUT_DIR).getValue();
    }
    
    public boolean isFontSet() {
        return arguments.get(ARG_FONT).isSet();
    }
    
    public boolean isSizeSet() {
        return arguments.get(ARG_SIZE).isSet();
    }
    
    public boolean isLineSpacingSet() {
        return arguments.get(ARG_LINE_SPACING).isSet();
    }
    
    public boolean isOutputDirSet() {
        return arguments.get(ARG_OUTPUT_DIR).isSet();
    }
}