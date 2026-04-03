package com.cmps.thesischecker.checker;

import com.cmps.thesischecker.model.FormatError;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import java.util.List;
import java.util.Map;

public interface Checker {

    List<FormatError> check(String filePath);
}
