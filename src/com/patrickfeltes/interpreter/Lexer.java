package com.patrickfeltes.interpreter;

import com.patrickfeltes.interpreter.tokens.*;

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
