package com.cmps.thesischecker.checker;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;

import com.cmps.thesischecker.model.ErrorCategory;
import com.cmps.thesischecker.model.FormatError;
import com.cmps.thesischecker.requirements.RequirementsHolder;

public class LineSpaceChecker implements Checker {

    public LineSpaceChecker() {
    }

    @Override
    public List<FormatError> check(String filePath) {
        List<FormatError> allErrors = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(filePath);
             XWPFDocument doc = new XWPFDocument(fis)) {
            for (XWPFParagraph paragraph : doc.getParagraphs()) {
                String paragraphText = paragraph.getText().trim();
                if (paragraphText.isEmpty()) continue;
                Set<String> incorrectLineSpacings = validate(paragraph);
                if (!incorrectLineSpacings.isEmpty()) {
                    FormatError error = new FormatError();
                    error.setId("err_linespace");
                    error.setCategory(ErrorCategory.LINE_SPACING);
                    error.setSeverity("error");
                    error.setTitle("Невірні інтервали між рядками у параграфі");
                    error.setParagraphText(paragraphText);
                    error.setFound(incorrectLineSpacings);
                    error.setExpected(RequirementsHolder.getLineSpacing());
                    allErrors.add(error);
                }
            }
        } catch (Exception e) {
            FormatError error = new FormatError();
            error.setId("err_000");
            error.setCategory(ErrorCategory.FILE);
            error.setSeverity("error");
            error.setTitle("Помилка відкриття файлу: " + e.getMessage());
            error.setParagraphText("");
            allErrors.add(error);
        }

        return allErrors;
    }
    
    private Set<String> validate(XWPFParagraph paragraph) {
        Set<String> incorrectLineSpacings = new HashSet<>();
        String expectedLineSpacing = RequirementsHolder.getLineSpacing();

        double paragraphSpacing = paragraph.getSpacingBetween();
        if (!String.valueOf(paragraphSpacing).equalsIgnoreCase(expectedLineSpacing)) {
            incorrectLineSpacings.add(String.valueOf(paragraphSpacing));
        }

        return incorrectLineSpacings;
    }
}
