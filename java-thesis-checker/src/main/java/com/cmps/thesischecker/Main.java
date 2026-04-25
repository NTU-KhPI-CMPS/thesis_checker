package com.cmps.thesischecker;

import org.graalvm.nativeimage.IsolateThread;
import org.graalvm.nativeimage.c.function.CEntryPoint;
import org.graalvm.nativeimage.c.type.CCharPointer;
import org.graalvm.nativeimage.c.type.CTypeConversion;
import com.cmps.thesischecker.argparser.FilePathParser;
import com.cmps.thesischecker.argparser.Parser;
import com.cmps.thesischecker.argparser.ResultDirectoryParser;
import com.cmps.thesischecker.checker.Checker;
import com.cmps.thesischecker.checker.FontChecker;
import com.cmps.thesischecker.model.FormatError;
import com.cmps.thesischecker.model.Report;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationFeature;

import java.io.File;
import java.nio.file.*;
import java.util.*;
import java.util.Set;

public class Main {

    private static final List<Checker> CHECKERS = List.of(new FontChecker());

    static void main(String[] args) {
        Parser<List<String>> filePathParser = new FilePathParser();
        List<String> files = filePathParser.parse(args);

        Parser<String> resultDirectoryParser = new ResultDirectoryParser();
        String outputDir = resultDirectoryParser.parse(args);

        if (files.isEmpty()) {
            System.err.println("No input files specified.");
            System.exit(1);
        }

        processFiles(files, outputDir);
    }

    @CEntryPoint(name = "run_thesis_checks")
    @SuppressWarnings("unused")
    public static int runThesisChecks(IsolateThread thread,
                                      CCharPointer filePathPtr,
                                      CCharPointer resultDirPtr) {
        String inputFiles = CTypeConversion.toJavaString(filePathPtr);
        List<String> files = List.of(inputFiles.split(","));

        String outputDir = CTypeConversion.toJavaString(resultDirPtr);

        if (files.isEmpty()) {
            System.err.println("No input files specified.");
            return 1;
        }

        processFiles(files, outputDir);

        return 0;
    }

    private static void processFiles(List<String> files, String outputDir) {
        for (String filePath : files) {
            List<FormatError> allErrors = new ArrayList<>();

            for (Checker checker : CHECKERS) {
                List<FormatError> errors = checker.check(filePath);
                allErrors.addAll(errors);
            }

            printReport(filePath, allErrors);
            Report report = new Report();
            report.setErrors(allErrors);
            saveJsonReport(report, outputDir);
        }
    }

    private static void printReport(String filePath, List<FormatError> errors) {
        String filename = new File(filePath).getName();
        System.out.println("Файл: " + filename);

        if (errors.isEmpty()) {
            System.out.println("Помилок немає, всі шрифти правильні");
        } else {
            for (FormatError error : errors) {
                Set<String> found = error.getFound();
                if (found == null) found = Set.of();
                String foundStr = String.join(", ", found);
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
