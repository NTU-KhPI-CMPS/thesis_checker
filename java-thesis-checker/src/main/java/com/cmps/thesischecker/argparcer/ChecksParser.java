package com.cmps.thesischecker.argparcer;

import com.cmps.thesischecker.checker.CheckType;

import java.util.List;

public class ChecksParser implements ArgumentParser<List<CheckType>> {

    @Override
    public List<CheckType> parse(String arg) {
        // TODO: implement parsing
        return List.of(CheckType.FONT);
    }
}
