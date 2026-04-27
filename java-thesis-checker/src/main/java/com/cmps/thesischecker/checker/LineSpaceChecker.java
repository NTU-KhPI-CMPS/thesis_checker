package com.cmps.thesischecker.checker;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFStyle;
import org.apache.poi.xwpf.usermodel.XWPFStyles;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPPrGeneral;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSpacing;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STLineSpacingRule;

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

        double spacing = getSpacing(paragraph);
        double finalValue = (spacing == -1.0) ? 1.0 : spacing;

        if (Math.abs(finalValue - Double.parseDouble(expectedLineSpacing)) > 0.01) {
            incorrectLineSpacings.add(String.format("%.2f", finalValue));
        }

        return incorrectLineSpacings;
    }

    private double getSpacing(XWPFParagraph paragraph) {
        double spacing = getSpacingFromPPr(paragraph.getCTP().getPPr());
        if (spacing > 0) {
            return spacing;
        }

        spacing = getSpacingFromStyles(paragraph);
        if (spacing > 0) {
            return spacing;
        }

        spacing = paragraph.getSpacingBetween();
        return spacing > 0 ? spacing : -1.0;
    }

    private double getSpacingFromStyles(XWPFParagraph paragraph) {
        XWPFStyles styles = paragraph.getDocument().getStyles();
        if (styles == null) {
            return -1.0;
        }

        String styleId = paragraph.getStyle();
        while (styleId != null) {
            XWPFStyle style = styles.getStyle(styleId);
            if (style == null || style.getCTStyle() == null) {
                break;
            }

            double fromStyle = getSpacingFromPPr(style.getCTStyle().getPPr());
            if (fromStyle != -1.0) {
                return fromStyle;
            }

            if (!style.getCTStyle().isSetBasedOn() || style.getCTStyle().getBasedOn() == null) {
                break;
            }

            styleId = style.getCTStyle().getBasedOn().getVal();
        }

        if (styles.getDefaultParagraphStyle() != null) {
            return getSpacingFromPPr(styles.getDefaultParagraphStyle().getPPr());
        }

        return -1.0;
    }

    private double getSpacingFromPPr(CTPPr pPr) {
        if (pPr == null) {
            return -1.0;
        }
        return getSpacingFromCTSpacing(pPr.getSpacing());
    }

    private double getSpacingFromPPr(CTPPrGeneral pPr) {
        if (pPr == null) {
            return -1.0;
        }
        return getSpacingFromCTSpacing(pPr.getSpacing());
    }

    private double getSpacingFromCTSpacing(CTSpacing spacing) {
        if (spacing == null || spacing.getLine() == null) {
            return -1.0;
        }

        try {
            double lineVal = Double.parseDouble(spacing.getLine().toString());
            if (lineVal <= 0) {
                return -1.0;
            }

            STLineSpacingRule.Enum rule = spacing.getLineRule();

            if (rule == null || rule.equals(STLineSpacingRule.AUTO)) {
                return lineVal / 240.0;
            } else {
                return lineVal / 20.0;
            }
        } catch (NumberFormatException e) {
            return -1.0;
        }
    }
}
