package com.cmps.thesischecker.argparser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FilePathParser implements Parser<List<String>> {

    private static final String FILE_PATH_ARG = "-filePath";
    /**
     * Flag to check all files in a directory.
     */
    private static final String DIRECTORY_PATH_ARG = "-directory";

    @Override
    public List<String> parse(String[] args) {
        List<String> filePaths = new ArrayList<>();

        for (int i = 0; i < args.length; i++) {
            if (FILE_PATH_ARG.equals(args[i]) && i + 1 < args.length) {
                filePaths.add(args[i + 1]);
            }
        }

        for (int i = 0; i < args.length; i++) {
            if (DIRECTORY_PATH_ARG.equals(args[i]) && i + 1 < args.length) {
                try (var files = Files.walk(Path.of(args[i + 1]))) {
                    files.filter(file -> file.toFile().isFile())
                            .forEach(file -> filePaths.add(file.toString()));

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
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
