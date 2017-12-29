package com.patrickfeltes.interpreter;

import com.patrickfeltes.interpreter.files.FileUtilities;

public class Main {

    private static boolean hadError = false;

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Invalid program arguments. Please provide the " +
                    "filepath to your file relative to this directory as an argument.");
        }

        System.out.println(FileUtilities.readFileToString(args[0]));
    }

    public static void error(int lineNumber, String message) {
        report(lineNumber, "", message);
    }

    private static void report(int lineNumber, String where, String message) {
        System.err.println(
                "[line " + lineNumber + "] Error" + where + ": " + message);
        hadError = true;
    }

}
