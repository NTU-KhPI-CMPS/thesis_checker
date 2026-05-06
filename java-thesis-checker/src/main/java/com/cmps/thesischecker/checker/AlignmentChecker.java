package com.cmps.thesischecker.checker;

import com.cmps.thesischecker.model.ErrorCategory;
import com.cmps.thesischecker.model.FormatError;
import com.cmps.thesischecker.model.Style;
import com.cmps.thesischecker.utils.StyleUtils;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFStyle;
import org.apache.poi.xwpf.usermodel.XWPFStyles;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPPrBase;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPPrGeneral;
import com.cmps.thesischecker.requirements.RequirementsHolder;

import java.io.FileInputStream;
import java.util.*;

public class AlignmentChecker implements Checker {

    /**
     * Enum containing basic paragraph text alignment values with their localized names.
     */
    public enum Alignment {

        LEFT("По лівому краю"),
        RIGHT("По правому краю"),
        CENTER("По центру"),
        BOTH("По ширині");

        final String name;

        Alignment(String name) {
            this.name = name;
        }
    }

    /**
     * Checks the document for text alignment violations and returns all found format errors.
     *
     * @param filePath path to the DOCX file
     * @return list of detected formatting errors
     */
    @Override
    public List<FormatError> check(String filePath) {
        List<FormatError> allErrors = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(filePath);
             XWPFDocument doc = new XWPFDocument(fis)) {
            for (XWPFParagraph paragraph : doc.getParagraphs()) {
                String paragraphText = paragraph.getText().trim();
                if (paragraphText.isEmpty()) {
                    continue;
                }
                Optional<String> incorrectAlignment = validate(paragraph);
                if (incorrectAlignment.isPresent()) {
                    String expectedRaw = isHeading(paragraph)
                            ? RequirementsHolder.getHeadingAlignment()
                            : RequirementsHolder.getMainTextAlignment();

                    String expectedLocalized = mapToLocalized(expectedRaw);
                    allErrors.add(buildAlignmentError(paragraphText, incorrectAlignment.get(), expectedLocalized));
                }
            }
        } catch (Exception e) {
            allErrors.add(buildException(e));
        }

        return allErrors;
    }

    /**
     * Determines whether the given paragraph is formatted as a heading.
     * Validates against standard English ("Heading1") style names.
     *
     * @param paragraph the paragraph to inspect
     * @return true if the paragraph is a heading, false otherwise
     */
    private boolean isHeading(XWPFParagraph paragraph) {
        String styleId = paragraph.getStyle();
        return Style.HEADING_1.matches(styleId);
    }

    /**
     * Creates and returns a file-level error when the document cannot be opened or read.
     *
     * @param e the exception that was thrown
     * @return the created format error
     */
    private static FormatError buildException(Exception e) {
        FormatError error = new FormatError();
        error.setId("err_000");
        error.setCategory(ErrorCategory.FILE);
        error.setSeverity("error");
        error.setTitle("Помилка відкриття файлу: " + e.getMessage());
        error.setParagraphText("");
        return error;
    }

    /**
     * Creates and returns an alignment error for a paragraph with invalid alignment values.
     *
     * @param paragraphText the paragraph text
     * @param incorrectAlignment the detected incorrect alignment value
     * @param expected localized string specifying the expected alignment
     * @return the created format error
     */
    private static FormatError buildAlignmentError(String paragraphText, String incorrectAlignment, String expected) {
        FormatError error = new FormatError();
        error.setId("err_alignment");
        error.setCategory(ErrorCategory.ALIGNMENT);
        error.setSeverity("error");
        error.setTitle("Невірне вирівнювання тексту");
        error.setParagraphText(paragraphText);

        // Map raw alignment string to localized name for output
        error.setFound(Set.of(mapToLocalized(incorrectAlignment)));
        error.setExpected(expected);
        return error;
    }

    /**
     * Maps raw POI alignment strings to readable, localized names.
     *
     * @param align raw alignment string (e.g., "LEFT", "CENTER")
     * @return the localized alignment name, or the raw string if unknown
     */
    private static String mapToLocalized(String align) {
        return switch (align) {
            case "LEFT" -> Alignment.LEFT.name;
            case "RIGHT" -> Alignment.RIGHT.name;
            case "CENTER" -> Alignment.CENTER.name;
            case "BOTH" -> Alignment.BOTH.name;
            default -> align;
        };
    }

    /**
     * Validates a paragraph against the expected alignment.
     *
     * @param paragraph the paragraph to validate
     * @return an Optional containing the detected alignment value if the paragraph is invalid, or empty otherwise
     */
    Optional<String> validate(XWPFParagraph paragraph) {
        String actualAlignment = getAlignment(paragraph);

        if (isHeading(paragraph)) {
            if (!actualAlignment.equals(RequirementsHolder.getHeadingAlignment())) {
                return Optional.of(actualAlignment);
            }
            return Optional.empty();
        }

        if (!actualAlignment.equals(RequirementsHolder.getMainTextAlignment())) {
            return Optional.of(actualAlignment);
        }

        return Optional.empty();
    }

    /**
     * Resolves the paragraph alignment using paragraph properties or styles.
     * Priority order: <p>
     * 1. Explicit alignment in paragraph properties (PPr) <p>
     * 2. Alignment from paragraph style chain (style → base styles → default style) <p>
     * <p>
     * If alignment is not configured anywhere, "LEFT" is returned as default.
     *
     * @param paragraph the paragraph to inspect
     * @return the resolved alignment value (e.g., "LEFT", "RIGHT", "CENTER", "BOTH", "DISTRIBUTE")
     */
    String getAlignment(XWPFParagraph paragraph) {
        String alignment = getAlignmentFromPPr(paragraph.getCTP().getPPr());
        if (alignment != null) {
            return alignment;
        }

        alignment = getAlignmentFromStyles(paragraph);
        return Objects.requireNonNullElse(alignment, "LEFT");

    }

    /**
     * Resolves alignment from the paragraph style chain, including the default paragraph style.
     *
     * @param paragraph the paragraph to inspect
     * @return the resolved alignment value, or {@code null} if none is available
     */
    String getAlignmentFromStyles(XWPFParagraph paragraph) {
        XWPFStyles styles = paragraph.getDocument().getStyles();
        if (styles == null) {
            return null;
        }

        String styleId = paragraph.getStyle();
        if (styleId == null) {
            styleId = StyleUtils.getNormalStyleId(styles, "Normal");
        }
        while (styleId != null) {
            XWPFStyle style = styles.getStyle(styleId);
            if (style == null || style.getCTStyle() == null) {
                break;
            }

            String fromStyle = getAlignmentFromPPr(style.getCTStyle().getPPr());
            if (fromStyle != null) {
                return fromStyle;
            }

            if (!style.getCTStyle().isSetBasedOn() || style.getCTStyle().getBasedOn() == null) {
                break;
            }

            styleId = style.getCTStyle().getBasedOn().getVal();
        }

        if (styles.getDefaultParagraphStyle() != null) {
            return getAlignmentFromPPr(styles.getDefaultParagraphStyle().getPPr());
        }

        return null;
    }

    /**
     * Checks if the paragraph properties or its alignment (Jc) component are missing.
     *
     * @param pPr the base paragraph properties
     * @return true if alignment is absent, false otherwise
     */
    boolean isAlignmentMissing(CTPPrBase pPr) {
        return pPr == null || !pPr.isSetJc() || pPr.getJc() == null || pPr.getJc().getVal() == null;
    }

    /**
     * Reads alignment from paragraph properties.
     *
     * @param pPr the paragraph properties
     * @return the resolved alignment value, or {@code null} if not present
     */
    String getAlignmentFromPPr(CTPPr pPr) {
        if (isAlignmentMissing(pPr)) {
            return null;
        }
        return pPr.getJc().getVal().toString().toUpperCase();
    }

    /**
     * Reads alignment from general paragraph properties.
     *
     * @param pPr the general paragraph properties
     * @return the resolved alignment value, or {@code null} if not present
     */
    String getAlignmentFromPPr(CTPPrGeneral pPr) {
        if (isAlignmentMissing(pPr)) {
            return null;
        }
        return pPr.getJc().getVal().toString().toUpperCase();
    }
}
