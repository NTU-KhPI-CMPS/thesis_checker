package com.cmps.thesischecker.argparcer;

public enum ProgramArg {

    FILE_PATH("-filePath", new FilePathParser()),
    CHEKS("-checks", new ChecksParser());

    final String argName;
    final ArgumentParser<?> parser;

    ProgramArg(String argName, ArgumentParser<?> parser) {
        this.argName = argName;
        this.parser = parser;
    }

    public static ProgramArg findByArgName(String argName) {
        for (ProgramArg programArg : ProgramArg.values()) {
            if (programArg.argName.equals(argName)) {
                return programArg;
            }
        }

        return null;
    }

    public Object parse(String arg) {
        return parser.parse(arg);
    }
}
