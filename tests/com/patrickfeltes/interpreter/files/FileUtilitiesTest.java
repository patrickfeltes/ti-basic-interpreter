package com.patrickfeltes.interpreter.files;

import org.junit.Test;

import static org.junit.Assert.*;

public class FileUtilitiesTest {

    @Test
    public void readFileToString() throws Exception {
        String expected = "1->A\nDisp A";
        assertEquals(expected, FileUtilities.readFileToString("programs/test1.bas"));
    }

}