package com.cmps.thesischecker.argparser;

import java.util.ArrayList;
import java.util.List;

public class ResultDirectoryParser implements Parser {
    
    static final String RESULT_DIRECTORY_ARG = "-resultDirectory";

    @Override
    public List<String> parse(String[] args) {
        List<String> resultDirectory = new ArrayList<>();

        for (int i = 0; i < args.length; i++) {
            if (RESULT_DIRECTORY_ARG.equals(args[i]) && i + 1 < args.length) {
                resultDirectory.add(args[i + 1]);
                return resultDirectory;
            }
        }
        
        return resultDirectory;
    }

    public static boolean hasInvalidResultDirectory(String[] args) {
        boolean hasResultDirectoryArg = false;

        for (int i = 0; i < args.length; i++) {
            if (RESULT_DIRECTORY_ARG.equals(args[i])) {
                hasResultDirectoryArg = true;

                if (i + 1 >= args.length) {
                    return true;
                }

                String value = args[i + 1];
                if (value == null || value.trim().isEmpty() || value.startsWith("-")) {
                    return true;
                }
            }
        }

        return !hasResultDirectoryArg;
    }

}
