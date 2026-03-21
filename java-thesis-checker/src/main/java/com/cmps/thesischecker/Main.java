package com.cmps.thesischecker;

import com.cmps.thesischecker.argparcer.ProgramArg;
import com.cmps.thesischecker.checker.CheckType;
import com.cmps.thesischecker.checker.FontChecker;
import org.apache.commons.lang3.exception.UncheckedException;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.cmps.thesischecker.argparcer.ProgramArg.CHEKS;
import static com.cmps.thesischecker.argparcer.ProgramArg.FILE_PATH;
import static com.cmps.thesischecker.checker.CheckType.FONT;

public class Main {

    public static void main(String[] args) {
        System.out.println(Arrays.toString(args));
        System.out.println("Checking thesis. File: ");


        String filePath = null;
        List<CheckType> checkTypes = List.of(CheckType.values());

        for (int i = 0; i < args.length; i += 2) {
            ProgramArg programArg = ProgramArg.findByArgName(args[i]);

            switch (programArg) {
                case FILE_PATH -> filePath = (String) FILE_PATH.parse(args[i + 1]);
                case CHEKS -> checkTypes = (List<CheckType>) CHEKS.parse(args[i + 1]);
            }
        }

        if (filePath == null) {
            System.err.println("Error: No file path provided");
            System.exit(1);
        }

        new FontChecker().check(filePath);
        System.out.println(FONT + " check is finished.");
    }
}
