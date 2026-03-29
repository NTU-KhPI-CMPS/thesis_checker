package com.cmps.thesischecker.argparser;

public class ChecksParser {
    
    public static String parse(String[] args, String key, String defaultValue) {
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals(key) && i + 1 < args.length) {
                return args[i + 1];
            }
        }
        return defaultValue;
    }
    
    public static Integer parseInt(String[] args, String key, Integer defaultValue) {
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals(key) && i + 1 < args.length) {
                try {
                    return Integer.parseInt(args[i + 1]);
                } catch (NumberFormatException e) {
                    return defaultValue;
                }
            }
        }
        return defaultValue;
    }
    
    public static Double parseDouble(String[] args, String key, Double defaultValue) {
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals(key) && i + 1 < args.length) {
                try {
                    return Double.parseDouble(args[i + 1]);
                } catch (NumberFormatException e) {
                    return defaultValue;
                }
            }
        }
        return defaultValue;
    }
}