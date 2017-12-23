package com.patrickfeltes.interpreter.files;

import org.junit.Test;

import static org.junit.Assert.*;

public class FileUtilitiesTest {

    @Test
    public void readFileToString() throws Exception {
        String expected = "1->A\nDisp A";
        String value = FileUtilities.readFileToString("programs/test1.bas");
        for (int i = 0; i < value.length(); i++) {
            if (value.charAt(i) == '\n') {
                System.out.println("\\n");
            } else {
                System.out.println(value.charAt(i));
            }
        }
        System.out.println(value.length());
        System.out.println(expected.equals(FileUtilities.readFileToString("programs/test1.bas")));
        assertEquals(expected, FileUtilities.readFileToString("programs/test1.bas"));
    }

}