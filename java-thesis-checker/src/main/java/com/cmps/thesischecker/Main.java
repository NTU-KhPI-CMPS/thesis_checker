package com.cmps.thesischecker;

import com.cmps.thesischecker.argparser.FilePathParser;
import com.cmps.thesischecker.argparser.Parser;
import com.cmps.thesischecker.argparser.ResultDirectoryParser;
import com.cmps.thesischecker.checker.AlignmentChecker;
import com.cmps.thesischecker.checker.Checker;
import com.cmps.thesischecker.checker.FontChecker;
import com.cmps.thesischecker.checker.LineSpaceChecker;
import com.cmps.thesischecker.checker.ListChecker;
import com.cmps.thesischecker.checker.ParagraphSpacingChecker;
import com.cmps.thesischecker.checker.SizeChecker;
import com.cmps.thesischecker.model.ErrorCategory;
import com.cmps.thesischecker.model.FormatError;
import com.cmps.thesischecker.model.Report;
import org.graalvm.nativeimage.IsolateThread;
import org.graalvm.nativeimage.c.function.CEntryPoint;
import org.graalvm.nativeimage.c.type.CCharPointer;
import org.graalvm.nativeimage.c.type.CCharPointerPointer;
import org.graalvm.nativeimage.c.type.CTypeConversion;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationFeature;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Collection;

public class Main {

    private static final List<Checker> CHECKERS = List.of(
            new FontChecker(),
            new LineSpaceChecker(),
            new AlignmentChecker(),
            new ListChecker(),
            new ParagraphSpacingChecker(),
            new SizeChecker()
    );

    static void main(String[] args) {
        Parser<List<String>> filePathParser = new FilePathParser();
        List<String> files = filePathParser.parse(args);

        Parser<String> resultDirectoryParser = new ResultDirectoryParser();
        String outputDir = resultDirectoryParser.parse(args);

        if (files.isEmpty()) {
            System.err.println("No input files specified.");
            System.exit(1);
        }

        processFiles(files, outputDir, CHECKERS);
    }

    @CEntryPoint(name = "run_thesis_checks")
    @SuppressWarnings("unused")
    public static int runThesisChecks(IsolateThread thread,
                                      int numberOfFiles,
                                      CCharPointerPointer filePathsPtr,
                                      CCharPointer resultDirPtr,
                                      int numberOfChecks,
                                      CCharPointerPointer checkCodesPtr) {
        List<String> files = parseCArray(filePathsPtr, numberOfFiles);
        String outputDir = CTypeConversion.toJavaString(resultDirPtr);
        List<String> checkCodes = parseCArray(checkCodesPtr, numberOfChecks);

        if (files.isEmpty()) {
            System.err.println("No input files specified.");
            return 1;
        }

        List<Checker> activeCheckers = resolveCheckers(checkCodes);
        processFiles(files, outputDir, activeCheckers);

        return 0;
    }

    private static List<String> parseCArray(CCharPointerPointer fileNamesPtr, int length) {
        List<String> result = new ArrayList<>();

        for (int i = 0; i < length; i++) {
            CCharPointer cString = fileNamesPtr.read(i);
            String javaString = CTypeConversion.toJavaString(cString);

            result.add(javaString);
        }

        return result;
    }

    private static void processFiles(List<String> files, String outputDir, Collection<Checker> checkers) {
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

    private static List<Checker> resolveCheckers(List<String> checkCodes) {
        if (checkCodes == null || checkCodes.isEmpty()) {
            return CHECKERS;
        }

        List<Checker> resolved = new ArrayList<>();
        for (String code : checkCodes) {
            if (code == null) {
                continue;
            }
            ErrorCategory category = parseCategory(code);
            if (category == null) {
                continue;
            }
            Checker mapped = parseCheckerByError(category);
            if (mapped != null && !resolved.contains(mapped)) {
                resolved.add(mapped);
            }
        }

        return resolved.isEmpty() ? CHECKERS : resolved;
    }

    private static ErrorCategory parseCategory(String rawCode) {
        String normalized = rawCode.trim().toUpperCase();

        try {
            return ErrorCategory.valueOf(normalized);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private static Checker parseCheckerByError(ErrorCategory category) {
        for (Checker checker : CHECKERS) {
            if (checker.getErrorCategory() == category) {
                return checker;
            }
        }

        return null;
    }

    private static void printReport(String filePath, List<FormatError> errors) {
        String filename = new File(filePath).getName();
        System.out.println("Файл: " + filename);

        if (errors.isEmpty()) {
            System.out.println("Помилок немає, всі шрифти правильні");
        } else {
            for (FormatError error : errors) {
                Set<String> found = error.getFound();
                String title = error.getTitle();

                if (found == null) found = Set.of();

                String foundStr = String.join(", ", found);
                System.out.println("Помилка: " + title + ". Знайдено -> " + foundStr);
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
