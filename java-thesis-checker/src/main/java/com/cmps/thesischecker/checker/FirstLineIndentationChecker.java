package com.cmps.thesischecker.checker;

import com.cmps.thesischecker.model.ErrorCategory;
import com.cmps.thesischecker.model.FormatError;
import com.cmps.thesischecker.requirements.RequirementsHolder;
import com.cmps.thesischecker.utils.MainContentUtils;
import com.cmps.thesischecker.utils.StyleUtils;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFStyle;
import org.apache.poi.xwpf.usermodel.XWPFStyles;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTInd;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPPrGeneral;

import java.io.FileInputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class FirstLineIndentationChecker implements Checker {

    @Override
    public ErrorCategory getErrorCategory() {
        return ErrorCategory.FIRST_LINE_INDENTATION;
    }

    @Override
    public List<FormatError> check(String filePath) {
        List<FormatError> allErrors = new ArrayList<>();
        try (FileInputStream fis = new FileInputStream(filePath);
             XWPFDocument doc = new XWPFDocument(fis)) {
            for (XWPFParagraph paragraph : MainContentUtils.getMainContentParagraphs(doc)) {
                if (paragraph.getText().trim().isEmpty()) {
                    continue;
                }
                if (com.cmps.thesischecker.utils.FormulaUtils.paragraphIsFormula(paragraph)) {
                    continue;
                }
                if (paragraph.getText().trim().equals("де")) {
                    continue;
                }
                Double found = getFirstLineIndentation(paragraph);
                double expected = Double.parseDouble(RequirementsHolder.getFirstLineIndentation());
                if (Math.abs(found - expected) > 0.001) {
                    allErrors.add(buildError(paragraph.getText().trim(), found));
                }
            }
        } catch (Exception e) {
            allErrors.add(buildException(e));
        }
        return allErrors;
    }

    private Double getFirstLineIndentation(XWPFParagraph paragraph) {
        Double value = getFromPPr(paragraph.getCTP().getPPr());
        if (value != null) return value;
        value = getFromStyles(paragraph);
        if (value != null) return value;
        return 0.0;
    }

    private Double getFromStyles(XWPFParagraph paragraph) {
        XWPFStyles styles = paragraph.getDocument().getStyles();
        if (styles == null) return null;
        String styleId = paragraph.getStyle();
        if (styleId == null) styleId = StyleUtils.getNormalStyleId(styles);
        while (styleId != null) {
            XWPFStyle style = styles.getStyle(styleId);
            if (style == null || style.getCTStyle() == null) break;
            Double v = getFromPPr(style.getCTStyle().getPPr());
            if (v != null) return v;
            if (!style.getCTStyle().isSetBasedOn() || style.getCTStyle().getBasedOn() == null) break;
            styleId = style.getCTStyle().getBasedOn().getVal();
        }
        if (styles.getDefaultParagraphStyle() != null) {
            return getFromPPr(styles.getDefaultParagraphStyle().getPPr());
        }
        return null;
    }

    private Double getFromPPr(CTPPr pPr) {
        if (pPr == null) return null;
        return getFromInd(pPr.getInd());
    }

    private Double getFromPPr(CTPPrGeneral pPr) {
        if (pPr == null) return null;
        return getFromInd(pPr.getInd());
    }

    private Double getFromInd(CTInd ind) {
        if (ind == null) return null;
        if (ind.getFirstLine() != null) {
            return ((BigInteger) ind.getFirstLine()).doubleValue() / 567.0;
        }
        if (ind.getLeft() != null) {
            double left = ((BigInteger) ind.getLeft()).doubleValue();
            double hanging = ind.getHanging() != null ? ((BigInteger) ind.getHanging()).doubleValue() : 0.0;
            return (left - hanging) / 567.0;
        }
        return null;
    }

    private static FormatError buildError(String text, Double found) {
        FormatError error = new FormatError();
        error.setId("err_first_line_indentation");
        error.setCategory(ErrorCategory.FIRST_LINE_INDENTATION);
        error.setSeverity("error");
        error.setTitle("Невірний відступ першого рядка абзацу");
        error.setParagraphText(text);
        error.setFound(Set.of(format(found)));
        error.setExpected(RequirementsHolder.getFirstLineIndentation());
        return error;
    }

    private static String format(Double v) {
        String s = String.format(Locale.US, "%.2f", v);
        if (s.equals("0.00")) return "0";
        if (s.endsWith("0")) {
            s = s.substring(0, s.length() - 1);
            if (s.endsWith(".")) s = s.substring(0, s.length() - 1);
        }
        if (s.equals("2.0")) s = "2";
        return s;
    }

    private static FormatError buildException(Exception e) {
        FormatError error = new FormatError();
        error.setId("err_000");
        error.setCategory(ErrorCategory.FILE);
        error.setSeverity("error");
        error.setTitle("Помилка відкриття файлу: " + e.getMessage());
        error.setParagraphText("");
        return error;
    }
}