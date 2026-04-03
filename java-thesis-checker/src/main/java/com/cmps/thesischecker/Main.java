package com.cmps.thesischecker;

import com.cmps.thesischecker.argparser.FilePathParser;
import com.cmps.thesischecker.checker.Checker;
import com.cmps.thesischecker.checker.FontChecker;
import com.cmps.thesischecker.model.FormatError;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationFeature;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
        // checkers.add(new SizeChecker());
        // checkers.add(new LineSpacingChecker());

        for (String filePath : files) {
            List<FormatError> allErrors = new ArrayList<>();

            for (Checker checker : checkers) {
                List<FormatError> errors = checker.check(filePath);
                allErrors.addAll(errors);
            }

            Map<String, Object> report = generateJsonReport(filePath, allErrors);
            saveJsonReport(report, filePath, outputDir);
        }
    }

    private static void error(String msg) {
        System.err.println(msg);
        System.exit(1);
    }

    private static Map<String, Object> generateJsonReport(String filePath, List<FormatError> errors) {
        String filename = new File(filePath).getName();

        Map<String, Object> report = new LinkedHashMap<>();
        report.put("file_name", filename);
        report.put("analyzed_at", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        report.put("errors", errors);
        return report;
    }

    private static void saveJsonReport(Map<String, Object> report, String baseFilename, String outputDir) {
        try {
            Path outputPath = Paths.get(outputDir);
            if (!Files.exists(outputPath)) Files.createDirectories(outputPath);

            String safeName = new File(baseFilename).getName();
            safeName = safeName.substring(0, safeName.lastIndexOf('.'));
            safeName = safeName.replaceAll("[^a-zA-Z0-9._-]", "_");
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String outFile = safeName + "_" + timestamp + ".json";
            Path outPath = outputPath.resolve(outFile);

            ObjectMapper mapper = new ObjectMapper();
            mapper.writer()
                    .with(SerializationFeature.INDENT_OUTPUT)
                    .writeValue(outPath.toFile(), report);
        } catch (Exception e) {
            System.err.println("Помилка збереження JSON-звіту: " + e.getMessage());
        }
    }
}
