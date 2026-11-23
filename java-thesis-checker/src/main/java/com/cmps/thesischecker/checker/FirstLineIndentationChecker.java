package com.cmps.thesischecker.checker;

import com.cmps.thesischecker.model.DocumentHeader;
import com.cmps.thesischecker.model.ErrorCategory;
import com.cmps.thesischecker.model.FormatError;
import com.cmps.thesischecker.requirements.RequirementsHolder;
import com.cmps.thesischecker.utils.FormulaUtils;
import com.cmps.thesischecker.utils.MainContentUtils;
import com.cmps.thesischecker.utils.StyleUtils;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFStyle;
import org.apache.poi.xwpf.usermodel.XWPFStyles;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTInd;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPPrGeneral;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTStyles;

import java.io.FileInputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;
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
                if (FormulaUtils.paragraphIsFormula(paragraph)) {
                    continue;
                }
                if (paragraph.getText().trim().equals("де")) {
                    continue;
                }
                if (FormulaUtils.isNotationParagraph(paragraph)) {
                    continue;
                }
                if (isIgnored(paragraph)) {
                    continue;
                }
                Double found = getFirstLineIndentation(paragraph);
                if (found != null && found < 0) {
                    allErrors.add(buildHangingWarning(paragraph.getText().trim(), found));
                    continue;
                }
                double expected = Double.parseDouble(RequirementsHolder.getFirstLineIndentation());
                if (Math.abs(found - expected) > 0.02) {
                    allErrors.add(buildError(paragraph.getText().trim(), found));
                }
            }
        } catch (Exception e) {
            allErrors.add(buildException(e));
        }
        return allErrors;
    }

    private boolean isIgnored(XWPFParagraph paragraph) {
        if (StyleUtils.isHeading1(paragraph)) {
            return true;
        }
        if (isHeading2Plus(paragraph)) {
            return true;
        }
        if (isList(paragraph)) {
            return true;
        }
        String trimmed = paragraph.getText().trim();
        if (trimmed.startsWith("Рисунок") || trimmed.startsWith("Рис.")) {
            return true;
        }
        for (DocumentHeader header : RequirementsHolder.getOrderStructuredElements()) {
            if (trimmed.equals(header.getTitle())) {
                return true;
            }
        }
        if (isInsideTable(paragraph)) {
            return true;
        }
        return false;
    }

    private boolean isHeading2Plus(XWPFParagraph paragraph) {
        String styleId = paragraph.getStyle();
        if (styleId != null) {
            String lower = styleId.toLowerCase();
            if (lower.startsWith("heading") && !lower.equals("heading1")) {
                return true;
            }
        }
        if (paragraph.getDocument() != null && styleId != null) {
            XWPFStyles styles = paragraph.getDocument().getStyles();
            if (styles != null) {
                XWPFStyle style = styles.getStyle(styleId);
                if (style != null && style.getName() != null) {
                    String name = style.getName().trim().toLowerCase();
                    if (name.startsWith("heading") && !name.equals("heading 1")) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean isList(XWPFParagraph paragraph) {
        if (paragraph.getNumID() != null) {
            return true;
        }
        CTPPr pPr = paragraph.getCTP().getPPr();
        if (pPr != null && pPr.isSetNumPr()) {
            return true;
        }
        return false;
    }

    private boolean isInsideTable(XWPFParagraph paragraph) {
        org.w3c.dom.Node parent = paragraph.getCTP().getDomNode().getParentNode();
        while (parent != null) {
            String name = parent.getLocalName();
            if (name == null) {
                name = parent.getNodeName();
            }
            if (name != null && name.toLowerCase().endsWith("tbl")) {
                return true;
            }
            parent = parent.getParentNode();
        }
        return false;
    }

    private Double getFirstLineIndentation(XWPFParagraph paragraph) {
        Double value = getFromPPr(paragraph.getCTP().getPPr());
        if (value != null) {
            return value;
        }
        value = getFromStyles(paragraph);
        if (value != null) {
            return value;
        }
        value = getFromDocDefaults(paragraph);
        if (value != null) {
            return value;
        }
        return 0.0;
    }

    private Double getFromStyles(XWPFParagraph paragraph) {
        XWPFStyles styles = paragraph.getDocument().getStyles();
        if (styles == null) {
            return null;
        }
        String styleId = paragraph.getStyle();
        if (styleId == null) {
            styleId = StyleUtils.getNormalStyleId(styles);
        }
        Set<String> visited = new HashSet<>();
        while (styleId != null) {
            if (!visited.add(styleId)) {
                break;
            }
            XWPFStyle style = styles.getStyle(styleId);
            if (style == null || style.getCTStyle() == null) {
                break;
            }
            Double v = getFromPPr(style.getCTStyle().getPPr());
            if (v != null) {
                return v;
            }
            if (!style.getCTStyle().isSetBasedOn() || style.getCTStyle().getBasedOn() == null) {
                break;
            }
            styleId = style.getCTStyle().getBasedOn().getVal();
        }
        if (styles.getDefaultParagraphStyle() != null) {
            return getFromPPr(styles.getDefaultParagraphStyle().getPPr());
        }
        return null;
    }

    private Double getFromPPr(CTPPr pPr) {
        if (pPr == null) {
            return null;
        }
        return getFromInd(pPr.getInd());
    }

    private Double getFromPPr(CTPPrGeneral pPr) {
        if (pPr == null) {
            return null;
        }
        return getFromInd(pPr.getInd());
    }

    private Double getFromDocDefaults(XWPFParagraph paragraph) {
        if (paragraph == null || paragraph.getDocument() == null) {
            return null;
        }
        XWPFStyles styles = paragraph.getDocument().getStyles();
        if (styles == null) {
            return null;
        }
        CTStyles ctStyles = styles.getCtStyles();
        if (ctStyles == null) {
            return null;
        }
        if (ctStyles.getDocDefaults() == null) {
            return null;
        }
        if (ctStyles.getDocDefaults().getPPrDefault() == null) {
            return null;
        }
        if (ctStyles.getDocDefaults().getPPrDefault().getPPr() == null) {
            return null;
        }
        CTInd ind = ctStyles.getDocDefaults().getPPrDefault().getPPr().getInd();
        if (ind == null) {
            return null;
        }
        return getFromInd(ind);
    }

    private Double getFromInd(CTInd ind) {
        if (ind == null) {
            return null;
        }
        if (ind.isSetFirstLine()) {
            Object fl = ind.getFirstLine();
            double twips = (fl instanceof BigInteger) ? ((BigInteger) fl).doubleValue() : Double.parseDouble(String.valueOf(fl));
            return twips / 566.929;
        }
        if (ind.isSetFirstLineChars()) {
            BigInteger flc = ind.getFirstLineChars();
            if (flc != null) {
                return flc.doubleValue() / 100.0;
            }
        }
        if (ind.isSetHanging()) {
            Object h = ind.getHanging();
            double twips = (h instanceof BigInteger) ? ((BigInteger) h).doubleValue() : Double.parseDouble(String.valueOf(h));
            return -(twips / 566.929);
        }
        if (ind.isSetHangingChars()) {
            BigInteger hc = ind.getHangingChars();
            if (hc != null) {
                return -(hc.doubleValue() / 100.0);
            }
        }
        if (ind.getLeft() != null) {
            double left = ((BigInteger) ind.getLeft()).doubleValue();
            double hanging = 0.0;
            if (ind.isSetHanging()) {
                Object h = ind.getHanging();
                hanging = (h instanceof BigInteger) ? ((BigInteger) h).doubleValue() : Double.parseDouble(String.valueOf(h));
            }
            return (left - hanging) / 566.929;
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

    private static FormatError buildHangingWarning(String text, Double found) {
        FormatError error = new FormatError();
        error.setId("warn_first_line_hanging");
        error.setCategory(ErrorCategory.FIRST_LINE_INDENTATION);
        error.setSeverity("warning");
        error.setTitle("Виявлено виступ (hanging indent)");
        error.setParagraphText(text);
        error.setFound(Set.of(format(found)));
        error.setExpected(RequirementsHolder.getFirstLineIndentation());
        return error;
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

    private static String format(Double v) {
        boolean negative = v < 0;
        double a = Math.abs(v);
        String s = String.format(Locale.US, "%.2f", a);
        if (s.equals("0.00")) {
            s = "0";
        } else {
            if (s.endsWith("0")) {
                s = s.substring(0, s.length() - 1);
                if (s.endsWith(".")) {
                    s = s.substring(0, s.length() - 1);
                }
            }
        }
        if (s.equals("2.0")) {
            s = "2";
        }
        if (negative && !s.equals("0")) {
            s = "-" + s;
        }
        return s;
    }
}
