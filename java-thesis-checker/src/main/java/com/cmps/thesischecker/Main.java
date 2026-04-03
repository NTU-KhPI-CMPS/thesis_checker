package com.cmps.thesischecker;

import com.cmps.thesischecker.argparser.FilePathParser;
import com.cmps.thesischecker.checker.Checker;
import com.cmps.thesischecker.checker.FontChecker;
import com.cmps.thesischecker.model.FormatError;

import java.io.File;
import java.util.*;

public class Main {

    public static void main(String[] args) {

        List<String> files = FilePathParser.parse(args);

        if (files.isEmpty()) {
            error("No input files specified.");
            return;
        }

        List<Checker> checkers = new ArrayList<>();
        checkers.add(new FontChecker());

        for (String filePath : files) {
            List<FormatError> allErrors = new ArrayList<>();

            for (Checker checker : checkers) {
                List<FormatError> errors = checker.check(filePath);
                allErrors.addAll(errors);
            }

            printReport(filePath, allErrors);
        }
    }

    private static void error(String msg) {
        System.err.println(msg);
        System.exit(1);
    }

    private static void printReport(String filePath, List<FormatError> errors) {
        String filename = new File(filePath).getName();
        System.out.println("Файл: " + filename);

        if (errors.isEmpty()) {
            System.out.println("Помилок немає, всі шрифти правильні");
        } else {
            for (FormatError error : errors) {
                System.out.println("Тут шрифт не правильний (використаний: " + error.getFound() + ")");
            }
        }
        System.out.println();
    }
}
