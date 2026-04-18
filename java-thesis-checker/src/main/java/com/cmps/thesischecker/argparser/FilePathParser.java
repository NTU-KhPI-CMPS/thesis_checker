package com.cmps.thesischecker.argparser;

import java.util.ArrayList;
import java.util.List;

public class FilePathParser implements Parser {

    private static final String FILE_PATH_ARG = "-filePath";

    @Override
    public List<String> parse(String[] args) {
        List<String> filePaths = new ArrayList<>();

        for (int i = 0; i < args.length; i++) {
            if (FILE_PATH_ARG.equals(args[i]) && i + 1 < args.length) {
                filePaths.add(args[i + 1]);
            }
        }

        return filePaths;
    }

    public static boolean hasFiles(String[] args) {
        for (String arg : args) {
            if (FILE_PATH_ARG.equals(arg)) {
                return true;
            }
        }
        return false;
    }
}
