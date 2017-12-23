package com.patrickfeltes.interpreter.files;

import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * FileUtilities is a utility class for file-related matters.
 */
public class FileUtilities {

    /**
     * A method to take a filepath with root in the current directory a read its contents to a String.
     * @param filepath the path from the current directory
     * @return the contents of the file at filepath as a String
     */
    public static String readFileToString(String filepath) {
        try {
            InputStream is = new FileInputStream(filepath);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));

            String line = reader.readLine();
            StringBuilder builder = new StringBuilder();

            while (line != null) {
                builder.append(line).append("\n");
                line = reader.readLine();
            }

            // need to delete extra newline character
            return builder.deleteCharAt(builder.length() - 1).toString();
        } catch (FileNotFoundException e) {
            System.out.println("Unable to locate file.");
            System.exit(-1);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("IO Error.");
            System.exit(-1);
        }

        return null;
    }
}
