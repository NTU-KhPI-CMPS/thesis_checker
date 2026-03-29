package com.cmps.thesischecker.requirements;

import lombok.Getter;

/**
 * Contains requirement for thesis formatting.
 *
 * @author Mariia Borodin (HappyMary16)
 * @since 1.0
 */
public class RequirementsHolder {

    @Getter
    private static String font = "Times New Roman";
    @Getter
    private static float fontSize = 14;
    @Getter
    private static float lineSpacing = 1.5f;

    public void readRequirementsFromConfig(String configFile) {
        // here we can read some config file and update static variables.
    }
}
