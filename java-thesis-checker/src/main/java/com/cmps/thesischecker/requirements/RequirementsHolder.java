package com.cmps.thesischecker.requirements;

import com.cmps.thesischecker.model.DocumentHeader;
import com.cmps.thesischecker.model.NumberingFormat;
import lombok.Getter;

import java.util.List;

/**
 * Central holder for expected document-format values used by the checkers.
 */
public class RequirementsHolder {

    @Getter
    private static String font = "Times New Roman";

    @Getter
    private static String lineSpacing = "1.5";

    @Getter
    private static String mainTextAlignment = "BOTH";

    @Getter
    private static String headingAlignment = "CENTER";

    @Getter
    private static String fontSize = "14";

    @Getter
    private static String paragraphSpacing = "0";

    @Getter
    private static String paragraphIndentations = "0";

    @Getter
    private static String firstLineIndentation = "1.25";

    @Getter
    private static String listLevelStep = "1";

    @Getter
    private static String listFormattingConsistency = "consistent";

    @Getter
    private static String listAllowedMarkerFormats = String.join(", ",
            NumberingFormat.DECIMAL.getDisplayName(),
            NumberingFormat.LOWER_LETTER.getDisplayName(),
            NumberingFormat.BULLET.getDisplayName());

    @Getter
    private static String listMarkerClosingSymbol = ")";

    @Getter
    private static String listBulletChar = "–";

    @Getter
    private static String formulaAlignment = "CENTER або RIGHT";

    @Getter
    private static String formulaSpacing = "Порожній рядок";

    @Getter
    private static String formulaMultiplePerLine = "1 формула в рядку";

    @Getter
    private static List<DocumentHeader> orderStructuredElements = List.of(
            new DocumentHeader("РЕФЕРАТ", false),
            new DocumentHeader("ЗМІСТ", true),
            new DocumentHeader("ПЕРЕЛІК ПОЗНАК ТА СКОРОЧЕНЬ", false),
            new DocumentHeader("ВСТУП", true),
            new DocumentHeader("ВИСНОВКИ", true),
            new DocumentHeader("СПИСОК ДЖЕРЕЛ ІНФОРМАЦІЇ", true),
            new DocumentHeader("ДОДАТОК", false)
    );
}
