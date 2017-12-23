package com.patrickfeltes.interpreter;

import com.patrickfeltes.interpreter.tokens.*;
import com.patrickfeltes.interpreter.tokens.reserved.DispToken;
import org.junit.Test;

import static org.junit.Assert.*;

public class LexerTest {

    // commands
    @Test
    public void getNextToken_singleDisp() throws Exception {
        String program = "Disp";
        Lexer lexer = new Lexer(program);
        Token expected = new DispToken(1);
        assertEquals(expected, lexer.getNextToken());
    }

    // variables
    @Test
    public void getNextToken_singleVariable() throws Exception {
        String program = "A";
        Token expected = new VariableToken(1, 'A');
        assertEquals(expected, new Lexer(program).getNextToken());
    }

    @Test
    public void getNextToken_multipleDiscreteVariables() throws Exception {
        String program = "Z A B";
        Lexer lexer = new Lexer(program);
        Token expected = new VariableToken(1, 'Z');
        assertEquals(expected, lexer.getNextToken());
        lexer.getNextToken();
        expected = new VariableToken(1, 'A');
        assertEquals(expected, lexer.getNextToken());
        lexer.getNextToken();
        expected = new VariableToken(1, 'B');
        assertEquals(expected, lexer.getNextToken());
    }

    @Test
    public void getNextToken_adjacentVariables() throws Exception {
        String program = "ZAB";
        Lexer lexer = new Lexer(program);
        Token expected = new VariableToken(1, 'Z');
        assertEquals(expected, lexer.getNextToken());
        expected = new VariableToken(1, 'A');
        assertEquals(expected, lexer.getNextToken());
        expected = new VariableToken(1, 'B');
        assertEquals(expected, lexer.getNextToken());
    }

    // spaces
    @Test
    public void getNextToken_space() throws Exception {
        String program = " ";
        Token expected = new SpaceToken(1);
        assertEquals(expected, new Lexer(program).getNextToken());
    }

    // mixture
    @Test
    public void getNextToken_dispWithVariables() throws Exception {
        String program = "Disp A";
        Lexer lexer = new Lexer(program);
        Token expected = new DispToken(1);
        assertEquals(expected, lexer.getNextToken());
        expected = new SpaceToken(1);
        assertEquals(expected, lexer.getNextToken());
        expected = new VariableToken(1, 'A');
        assertEquals(expected, lexer.getNextToken());
    }

    // new lines
    @Test
    public void getNextToken_multiline() throws Exception {
        String program = "Disp\nA";
        Lexer lexer = new Lexer(program);
        Token expected = new DispToken(1);
        assertEquals(expected, lexer.getNextToken());
        expected = new EOLToken(1);
        assertEquals(expected, lexer.getNextToken());
        expected = new VariableToken(2, 'A');
        assertEquals(expected, lexer.getNextToken());
        expected = new EOFToken(2);
        assertEquals(expected, lexer.getNextToken());
    }

    @Test
    public void getNextToken_multilineColon() throws Exception {
        String program = "Disp:A";
        Lexer lexer = new Lexer(program);
        Token expected = new DispToken(1);
        assertEquals(expected, lexer.getNextToken());
        expected = new EOLToken(1);
        assertEquals(expected, lexer.getNextToken());
        expected = new VariableToken(2, 'A');
        assertEquals(expected, lexer.getNextToken());
        expected = new EOFToken(2);
        assertEquals(expected, lexer.getNextToken());
    }
}