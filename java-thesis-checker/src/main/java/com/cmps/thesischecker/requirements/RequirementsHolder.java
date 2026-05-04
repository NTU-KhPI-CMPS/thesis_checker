package com.cmps.thesischecker.requirements;

import lombok.Getter;

import java.util.Map;

public class RequirementsHolder {

    @Getter
    private static String font = "Times New Roman";

    @Getter
    private static String lineSpacing = "1.5";

    @Getter
    private static Map<String, String> alignment = Map.of(
            "DISTRIBUTE", "По ширині",
            "BOTH", "По ширині"
    );
}
