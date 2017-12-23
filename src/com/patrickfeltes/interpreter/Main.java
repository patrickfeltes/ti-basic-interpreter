package com.patrickfeltes.interpreter;

import com.patrickfeltes.interpreter.files.FileUtilities;

public class Main {

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Invalid program arguments. Please provide the " +
                    "filepath to your file relative to this directory as an argument.");
        }

        System.out.println(FileUtilities.readFileToString(args[0]));
    }

}
