package com.patrickfeltes.interpreter;

import com.patrickfeltes.interpreter.tokens.*;

import java.nio.charset.CharacterCodingException;

/**
 * The Lexer takes a program and splits it into tokens to be read by the parser.
 */
public class Lexer {

    private static final char EMPTY_CHAR = '\u0000';

    private String program;

    private int currentPosition;
    private char currentChar;

    private int lineNumber;

    public Lexer(String program) {
        this.program = program;
        lineNumber = 1;
        currentPosition = 0;
        currentChar = program.charAt(0);
    }

    /**
     * Gets the next token in the program
     * @return the next token in the program
     */
    public Token getNextToken() {
        while (currentChar != EMPTY_CHAR) {
            if (currentChar == '\n' || currentChar == ':') {
                advance();
                lineNumber++;
                return TokenFactory.createToken(lineNumber - 1, TokenType.EOL);
            }

            if (Character.isSpaceChar(currentChar)) {
                advance();
                return TokenFactory.createToken(lineNumber, TokenType.SPACE);
            }

            if (Character.isLetter(currentChar)) {
                int originalPosition = currentPosition;
                String possibleReserved = getPossibleKeyword();

                Token reservedToken = getReservedToken(possibleReserved);
                if (reservedToken == null) {
                    currentPosition = originalPosition;
                    currentChar = program.charAt(currentPosition);
                    Token token = TokenFactory.createVariableToken(lineNumber, currentChar);
                    advance();
                    return token;
                } else {
                    return reservedToken;
                }
            }

            // store token
            if ((currentChar == '-' && peek() == '>') || (currentChar == '→')) {
                return TokenFactory.createToken(lineNumber, TokenType.STORE);
            }

            // numbers
            if (Character.isDigit(currentChar)) {
                return getNumber();
            }

            return singleCharTokens();
        }

        return TokenFactory.createToken(lineNumber, TokenType.EOF);
    }

    /**
     * Advances to the next character in the String.
     */
    private void advance() {
        currentPosition++;
        if (currentPosition >= program.length()) {
            currentChar = EMPTY_CHAR;
        } else {
            currentChar = program.charAt(currentPosition);
        }
    }

    private char peek() {
        if (currentPosition < program.length() - 1) {
            return program.charAt(currentPosition + 1);
        } else {
            return EMPTY_CHAR;
        }
    }

    /**
     * Handles all the single character tokens.
     * @return a single character token if it exists
     */
    private Token singleCharTokens() {
        switch (currentChar) {
            case '+':
                advance();
                return TokenFactory.createToken(lineNumber, TokenType.PLUS);
            case '-':
                advance();
                return TokenFactory.createToken(lineNumber, TokenType.MINUS);
            case '*':
                advance();
                return TokenFactory.createToken(lineNumber, TokenType.MUL);
            case '/':
                advance();
                return TokenFactory.createToken(lineNumber, TokenType.DIV);
            case '(':
                advance();
                return TokenFactory.createToken(lineNumber, TokenType.LPAREN);
            case ')':
                advance();
                return TokenFactory.createToken(lineNumber, TokenType.RPAREN);
            case '[':
                advance();
                return TokenFactory.createToken(lineNumber, TokenType.LBRACKET);
            case ']':
                advance();
                return TokenFactory.createToken(lineNumber, TokenType.RBRACKET);
            case '{':
                advance();
                return TokenFactory.createToken(lineNumber, TokenType.LBRACE);
            case '}':
                advance();
                return TokenFactory.createToken(lineNumber, TokenType.RBRACE);
            case '"':
                advance();
                return TokenFactory.createToken(lineNumber, TokenType.QUOTE);
            case '^':
                advance();
                return TokenFactory.createToken(lineNumber, TokenType.POW);
            case '!':
                advance();
                return TokenFactory.createToken(lineNumber, TokenType.EXCLAMATION);
            case '<':
                advance();
                return TokenFactory.createToken(lineNumber, TokenType.LT);
            case '>':
                advance();
                return TokenFactory.createToken(lineNumber, TokenType.GT);
            case '≤':
                advance();
                return TokenFactory.createToken(lineNumber, TokenType.LTOE);
            case '≥':
                advance();
                return TokenFactory.createToken(lineNumber, TokenType.GTOE);
            case '=':
                advance();
                return TokenFactory.createToken(lineNumber, TokenType.EQUAL);
            case '≠':
                advance();
                return TokenFactory.createToken(lineNumber, TokenType.NOT_EQUAL);
            default:
                return null;
        }
    }

    /**
     * Gets a number at the current position.
     * @return The token holding the number.
     */
    private Token getNumber() {
        StringBuilder number = new StringBuilder();
        boolean hasDecimal = false;
        while (Character.isDigit(currentChar)) {
            number.append(currentChar);
            advance();
            if (currentChar == '.' && hasDecimal) {
                System.out.println("Syntax error: numbers can't have multiple decimal points.");
                System.exit(-1);
            } else if (currentChar == '.') {
                hasDecimal = true;
                number.append(currentChar);
                advance();
            }
        }

        double value = Double.parseDouble(number.toString());
        return TokenFactory.createNumberToken(lineNumber, value);
    }

    /**
     * Builds a String of all adjacent characters in the line
     * @return the String
     */
    private String getPossibleKeyword() {
        StringBuilder builder = new StringBuilder();
        while(Character.isLetter(currentChar)) {
            builder.append(currentChar);
            advance();
        }
        return builder.toString();
    }

    /**
     * Given a string determine if it is possibly a keyword
     * @param possibleReserved The String to check
     * @return the token of that keyword if it exists, else null
     */
    private Token getReservedToken(String possibleReserved) {
        switch (possibleReserved) {
            case "Disp":
                return TokenFactory.createToken(lineNumber, TokenType.DISP);
            default:
                return null;
        }
    }

}
