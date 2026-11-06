package com.cmps.thesischecker.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Bibliography section detection tests")
class BibliographyUtilsTest {

    @Test
    @DisplayName("Should recognise the bibliography heading")
    void isBibliographyHeading() {
        assertTrue(BibliographyUtils.isBibliographyHeading("СПИСОК ДЖЕРЕЛ ІНФОРМАЦІЇ"));
        assertTrue(BibliographyUtils.isBibliographyHeading("  СПИСОК ДЖЕРЕЛ ІНФОРМАЦІЇ. "));
    }

    @Test
    @DisplayName("Should not mistake table-of-contents entries or sentences for the heading")
    void isBibliographyHeading_notHeading() {
        assertFalse(BibliographyUtils.isBibliographyHeading("Список джерел інформації"));
        assertFalse(BibliographyUtils.isBibliographyHeading("СПИСОК ДЖЕРЕЛ ІНФОРМАЦІЇ 45"));
        assertFalse(BibliographyUtils.isBibliographyHeading("ВИСНОВКИ"));
    }

    @Test
    @DisplayName("Should recognise headings that end the bibliography section")
    void isOtherStructuralHeading() {
        assertTrue(BibliographyUtils.isOtherStructuralHeading("ДОДАТОК А"));
        assertTrue(BibliographyUtils.isOtherStructuralHeading("ДОДАТОК"));
        assertTrue(BibliographyUtils.isOtherStructuralHeading("ВИСНОВКИ"));
        assertFalse(BibliographyUtils.isOtherStructuralHeading("СПИСОК ДЖЕРЕЛ ІНФОРМАЦІЇ"));
        assertFalse(BibliographyUtils.isOtherStructuralHeading("Додаток до списку"));
        assertFalse(BibliographyUtils.isOtherStructuralHeading("1 Джерело, що починається з номера."));
    }
}
