package com.cmps.thesischecker.checker;

import com.cmps.thesischecker.model.FormatError;
import java.util.List;

public interface Checker {

    List<FormatError> check(String filePath);
}
