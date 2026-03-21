package com.cmps.thesischecker.checker;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * TODO: add description.
 *
 * @author Mariia Borodin (HappyMary16)
 * @since 1.0
 */
public class FontChecker implements Checker {

    @Override
    public void check(String filePath) {
        System.out.println("Checking font for file: " + filePath);

        try (FileInputStream fis = new FileInputStream(filePath);
             XWPFDocument document = new XWPFDocument(fis)) {

            // Loop through all paragraphs in the Word document
            List<String> fontsUsed = new ArrayList<>();
            for (XWPFParagraph paragraph : document.getParagraphs()) {

                // A "Run" is a contiguous segment of text with the same formatting
                for (XWPFRun run : paragraph.getRuns()) {
                    String fontName = run.getFontFamily();

                    // If fontName is null, Word is using the document's default theme font
                    if (fontName != null) {
                        fontsUsed.add(fontName);
                    }
                }
            }

            // Print the results so Flutter can capture them in stdout
            System.out.println("Fonts found in the document:");
            System.out.println(fontsUsed);

            // Note: If you want to check if a *specific* font exists, you could do:
            // if (fontsUsed.contains("Comic Sans MS")) { System.out.println("Warning: Comic Sans detected!"); }

        } catch (Exception e) {
            System.err.println("Java Error reading document: " + e.getMessage());
            System.exit(1);
        }
    }
}
