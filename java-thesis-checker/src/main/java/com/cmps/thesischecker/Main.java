package com.cmps.thesischecker;

import com.cmps.thesischecker.argparser.FilePathParser;
import com.cmps.thesischecker.checker.Checker;
import com.cmps.thesischecker.checker.FontChecker;
import com.cmps.thesischecker.model.FormatError;
import com.cmps.thesischecker.model.Report;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationFeature;

import java.io.File;
import java.nio.file.*;
import java.util.*;

public class Main {

    private static final String DEFAULT_OUTPUT_DIR = "reports";

    public static void main(String[] args) {

        List<String> files = FilePathParser.parse(args);
        String outputDir = DEFAULT_OUTPUT_DIR;

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
            Report report = new Report();
            report.setErrors(allErrors);
            saveJsonReport(report, outputDir);
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
                String foundStr = String.join(", ", error.getFound());
                System.out.println("Тут шрифт не правильний (використані шрифти: " + foundStr + ")");
            }
        }
        System.out.println();
    }

    private static void saveJsonReport(Report report, String outputDir) {
        try {
            Path outputPath = Paths.get(outputDir);
            if (!Files.exists(outputPath)) Files.createDirectories(outputPath);

            Path outPath = outputPath.resolve("result.json");

            ObjectMapper mapper = new ObjectMapper();
            mapper.writer()
                    .with(SerializationFeature.INDENT_OUTPUT)
                    .writeValue(outPath.toFile(), report);
        } catch (Exception e) {
            System.err.println("Помилка збереження JSON-звіту: " + e.getMessage());
        }
    }
}
