package com.cmps.thesischecker.checker;

import com.cmps.thesischecker.argparser.ArgumentParser;
import com.cmps.thesischecker.argparser.FilePathParser;
import com.cmps.thesischecker.argparser.ChecksParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.poi.xwpf.usermodel.*;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Main {

    private static final String DEFAULT_FONT = "Times New Roman";
    private static final int DEFAULT_SIZE = 14;
    private static final double DEFAULT_LINE_SPACING = 1.5;
    private static final String DEFAULT_OUTPUT_DIR = "reports";

    public static void main(String[] args) {
        // Parse command line arguments
        ArgumentParser argParser = new ArgumentParser();
        argParser.parse(args);
        
        // Get file paths
        List<String> files = FilePathParser.parse(args);
        
        // Get check parameters with defaults
        String expectedFont = argParser.isFontSet() ? argParser.getFont() : DEFAULT_FONT;
        int expectedSize = argParser.isSizeSet() ? argParser.getSize() : DEFAULT_SIZE;
        double expectedLineSpacing = argParser.isLineSpacingSet() ? argParser.getLineSpacing() : DEFAULT_LINE_SPACING;
        String outputDir = argParser.isOutputDirSet() ? argParser.getOutputDir() : DEFAULT_OUTPUT_DIR;

        if (files.isEmpty()) {
            error("No input files specified.");
            return;
        }

        // Create checkers
        List<Checker> checkers = new ArrayList<>();
        checkers.add(new FontChecker(expectedFont));
        checkers.add(new SizeChecker(expectedSize));
        checkers.add(new LineSpacingChecker(expectedLineSpacing));

        for (String filePath : files) {
            List<Map<String, Object>> allErrors = new ArrayList<>();
            int[] errorIdCounter = new int[]{0};

            try (FileInputStream fis = new FileInputStream(filePath);
                 XWPFDocument doc = new XWPFDocument(fis)) {

                int paraIndex = 0;
                for (XWPFParagraph paragraph : doc.getParagraphs()) {
                    paraIndex++;
                    String paraText = paragraph.getText().trim();
                    if (paraText.isEmpty()) continue;
                    String shortParaText = paraText.length() > 200 ? paraText.substring(0, 200) + "..." : paraText;

                    for (Checker checker : checkers) {
                        List<Map<String, Object>> errors = checker.validate(paragraph, paraIndex, shortParaText, errorIdCounter);
                        allErrors.addAll(errors);
                    }
                }
            } catch (FileNotFoundException e) {
                allErrors.add(Collections.singletonMap("error", "Файл не знайдено: " + filePath));
            } catch (Exception e) {
                allErrors.add(Collections.singletonMap("error", "Помилка відкриття файлу: " + e.getMessage()));
            }

            Map<String, Object> report = generateJsonReport(filePath, allErrors, expectedFont, expectedSize, expectedLineSpacing);
            saveJsonReport(report, filePath, outputDir);
        }
    }

    private static void error(String msg) {
        System.err.println(msg);
        System.exit(1);
    }

    private static Map<String, Object> generateJsonReport(String filePath, List<Map<String, Object>> errors,
                                                          String expectedFont, int expectedSize, double expectedLineSpacing) {
        String filename = new File(filePath).getName();
        int totalErrors = 0;
        Map<String, Integer> bySeverity = new HashMap<>();
        Map<String, Integer> byCategory = new HashMap<>();

        for (Map<String, Object> err : errors) {
            if (err.containsKey("error")) continue;
            totalErrors++;
            String severity = (String) err.get("severity");
            bySeverity.put(severity, bySeverity.getOrDefault(severity, 0) + 1);
            String category = (String) err.get("category");
            byCategory.put(category, byCategory.getOrDefault(category, 0) + 1);
        }

        Map<String, Object> report = new LinkedHashMap<>();
        report.put("file_name", filename);
        report.put("analyzed_at", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("total_errors", totalErrors);
        summary.put("by_severity", bySeverity);
        summary.put("by_category", byCategory);
        report.put("summary", summary);

        List<Map<String, Object>> validErrors = new ArrayList<>();
        for (Map<String, Object> err : errors) {
            if (!err.containsKey("error")) validErrors.add(err);
        }
        report.put("errors", validErrors);
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
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            mapper.writeValue(outPath.toFile(), report);
        } catch (Exception e) {
            System.err.println("Помилка збереження JSON-звіту: " + e.getMessage());
        }
    }
}