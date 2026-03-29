package com.cmps.thesischecker.argparser;

import java.util.ArrayList;
import java.util.List;

public class FilePathParser {
    
    public static List<String> parse(String[] args) {
        List<String> filePaths = new ArrayList<>();
        
        for (String arg : args) {
            if (!arg.startsWith("--")) {
                filePaths.add(arg);
            }
        }
        
        return filePaths;
    }
    
    public static boolean hasFiles(String[] args) {
        for (String arg : args) {
            if (!arg.startsWith("--")) {
                return true;
            }
        }
        return false;
    }
}