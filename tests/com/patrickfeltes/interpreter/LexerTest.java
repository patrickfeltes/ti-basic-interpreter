package com.patrickfeltes.interpreter;

import com.patrickfeltes.interpreter.tokens.*;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static com.patrickfeltes.interpreter.tokens.TokenType.*;

public class LexerTest {

    // commands
    @Test
    public void scanTokens_singleDisp() throws Exception {
        String program = "Disp";
        Lexer lexer = new Lexer(program);
        List<Token> expected = new ArrayList<>();
        expected.add(new Token(DISP, "Disp", null, 1));
        expected.add(new Token(EOF, "", null, 1));
        assertEquals(expected, lexer.lexTokens());
    }

    // variables
    @Test
    public void scanTokens_singleVariable() throws Exception {
        String program = "A";
        List<Token> expected = new ArrayList<>();
        expected.add(new Token(IDENTIFIER, "A", null, 1));
        expected.add(new Token(EOF, "", null, 1));
        assertEquals(expected, new Lexer(program).lexTokens());
    }

    @Test
    public void scanTokens_multipleDiscreteVariables() throws Exception {
        String program = "Z A B";
        Lexer lexer = new Lexer(program);
        List<Token> expected = new ArrayList<>();
        expected.add(new Token(IDENTIFIER, "Z", null, 1));
        expected.add(new Token(IDENTIFIER, "A", null, 1));
        expected.add(new Token(IDENTIFIER, "B", null, 1));
        expected.add(new Token(EOF, "", null, 1));
        assertEquals(expected, lexer.lexTokens());
    }

    @Test
    public void scanTokens_adjacentVariables() throws Exception {
        String program = "ZAB";
        Lexer lexer = new Lexer(program);
        List<Token> expected = new ArrayList<>();
        expected.add(new Token(IDENTIFIER, "Z", null, 1));
        expected.add(new Token(IDENTIFIER, "A", null, 1));
        expected.add(new Token(IDENTIFIER, "B", null, 1));
        expected.add(new Token(EOF, "", null, 1));
        assertEquals(expected, lexer.lexTokens());
    }


    // mixture
    @Test
    public void scanTokens_dispWithVariables() throws Exception {
        String program = "Disp A";
        Lexer lexer = new Lexer(program);
        List<Token> expected = new ArrayList<>();
        expected.add(new Token(DISP, "Disp", null, 1));
        expected.add(new Token(IDENTIFIER, "A", null, 1));
        expected.add(new Token(EOF, "", null, 1));
        assertEquals(expected, lexer.lexTokens());
    }

    // new lines
    @Test
    public void scanTokens_multiline() throws Exception {
        String program = "Disp\nA";
        Lexer lexer = new Lexer(program);
        List<Token> expected = new ArrayList<>();
        expected.add(new Token(DISP, "Disp", null, 1));
        expected.add(new Token(EOL, "\n", null, 1));
        expected.add(new Token(IDENTIFIER, "A", null, 2));
        expected.add(new Token(EOF, "", null, 2));
        assertEquals(expected, lexer.lexTokens());
    }

    @Test
    public void scanTokens_multilineColon() throws Exception {
        String program = "Disp:A";
        Lexer lexer = new Lexer(program);
        List<Token> expected = new ArrayList<>();
        expected.add(new Token(DISP, "Disp", null, 1));
        expected.add(new Token(EOL, ":", null, 1));
        expected.add(new Token(IDENTIFIER, "A", null, 1));
        expected.add(new Token(EOF, "", null, 1));
        assertEquals(expected, lexer.lexTokens());
    }

    @Test
    public void scanTokens_store() throws Exception {
        String program = "->";
        Lexer lexer = new Lexer(program);
        List<Token> expected = new ArrayList<>();
        expected.add(new Token(STORE, "->", null, 1));
        expected.add(new Token(EOF, "", null, 1));
        assertEquals(expected, lexer.lexTokens());
        program = "→";
        lexer = new Lexer(program);
        expected = new ArrayList<>();
        expected.add(new Token(STORE, "→", null, 1));
        expected.add(new Token(EOF, "", null, 1));
        assertEquals(expected, lexer.lexTokens());
    }

    @Test
    public void scanTokens_operations() throws Exception {
        String program = "+-*/^";
        Lexer lexer = new Lexer(program);
        List<Token> expected = new ArrayList<>();
        expected.add(new Token(PLUS, "+", null, 1));
        expected.add(new Token(MINUS, "-", null, 1));
        expected.add(new Token(MUL, "*", null, 1));
        expected.add(new Token(DIV, "/", null, 1));
        expected.add(new Token(POW, "^", null, 1));
        expected.add(new Token(EOF, "", null, 1));
        assertEquals(expected, lexer.lexTokens());
    }

    @Test
    public void scanTokens_groupingSymbols() throws Exception {
        String program = "()[]{}";
        Lexer lexer = new Lexer(program);
        List<Token> expected = new ArrayList<>();
        expected.add(new Token(LPAREN, "(", null, 1));
        expected.add(new Token(RPAREN, ")", null, 1));
        expected.add(new Token(LBRACKET, "[", null, 1));
        expected.add(new Token(RBRACKET, "]", null, 1));
        expected.add(new Token(LBRACE, "{", null, 1));
        expected.add(new Token(RBRACE, "}", null, 1));
        expected.add(new Token(EOF, "", null, 1));
        assertEquals(expected, lexer.lexTokens());
    }

    @Test
    public void scanTokens_comparisonSymbols() throws Exception {
        String program = "<>≤≥=≠";
        Lexer lexer = new Lexer(program);
        List<Token> expected = new ArrayList<>();
        expected.add(new Token(LT, "<", null, 1));
        expected.add(new Token(GT, ">", null, 1));
        expected.add(new Token(LTOE, "≤", null, 1));
        expected.add(new Token(GTOE, "≥", null, 1));
        expected.add(new Token(EQUAL, "=", null, 1));
        expected.add(new Token(NOT_EQUAL, "≠", null, 1));
        expected.add(new Token(EOF, "", null, 1));
        assertEquals(expected, lexer.lexTokens());
    }

    @Test
    public void scanTokens_miscSymbols() throws Exception {
        String program = "!";
        Lexer lexer = new Lexer(program);
        List<Token> expected = new ArrayList<>();
        expected.add(new Token(EXCLAMATION, "!", null, 1));
        expected.add(new Token(EOF, "", null, 1));
        assertEquals(expected, lexer.lexTokens());
    }

    // numbers

    @Test
    public void scanTokens_integer() throws Exception {
        String program = "123";
        Lexer lexer = new Lexer(program);
        List<Token> expected = new ArrayList<>();
        expected.add(new Token(NUMBER, "123", 123.0, 1));
        expected.add(new Token(EOF, "", null, 1));
        assertEquals(expected, lexer.lexTokens());
    }

    @Test
    public void scanTokens_double() throws Exception {
        String program = "123.123";
        Lexer lexer = new Lexer(program);
        List<Token> expected = new ArrayList<>();
        expected.add(new Token(NUMBER, "123.123", 123.123, 1));
        expected.add(new Token(EOF, "", null, 1));
        assertEquals(expected, lexer.lexTokens());
    }

    @Test
    public void scanTokens_string() {
        String program = "\"HELLO\"";
        Lexer lexer = new Lexer(program);
        List<Token> expected = new ArrayList<>();
        expected.add(new Token(STRING, "\"HELLO\"", "HELLO", 1));
        expected.add(new Token(EOF, "", null, 1));
        assertEquals(expected, lexer.lexTokens());
    }
}