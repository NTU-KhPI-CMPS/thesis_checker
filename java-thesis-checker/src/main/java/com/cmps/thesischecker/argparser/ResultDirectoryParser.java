package com.cmps.thesischecker.argparser;

public class ResultDirectoryParser implements Parser<String> {
    
    static final String RESULT_DIRECTORY_ARG = "-resultDirectory";
    static final String DEFAULT_RESULT_DIRECTORY = "reports";

    @Override
    public String parse(String[] args) {
        for (int i = 0; i < args.length; i++) {
            if (RESULT_DIRECTORY_ARG.equals(args[i])) {
                return args[i + 1];
            }
        }

        return DEFAULT_RESULT_DIRECTORY;
    }
}
