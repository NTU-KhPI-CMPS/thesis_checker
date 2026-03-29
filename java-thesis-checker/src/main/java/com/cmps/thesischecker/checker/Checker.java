package com.cmps.thesischecker.checker;

import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import java.util.List;
import java.util.Map;

public interface Checker {
    /**
     * Validates a paragraph and returns a list of error maps.
     * @param paragraph the paragraph to validate
     * @param paraIndex 1-based index of the paragraph in the document
     * @param shortParaText trimmed and possibly shortened text of the paragraph
     * @param errorIdCounter a mutable counter to assign unique error IDs
     * @return list of error maps (empty if no issues)
     */
    List<Map<String, Object>> validate(XWPFParagraph paragraph, int paraIndex, String shortParaText, int[] errorIdCounter);
}