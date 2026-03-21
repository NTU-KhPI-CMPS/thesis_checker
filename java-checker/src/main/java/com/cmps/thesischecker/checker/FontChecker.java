package com.cmps.thesischecker.checker;

/**
 * TODO: add description.
 *
 * @author Mariia Borodin (HappyMary16)
 * @since 1.0
 */
public class FontChecker implements Checker {

    @Override
    public void validate(String filePath) {
        System.out.println("Validating " + filePath);
    }
}
