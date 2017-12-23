package com.patrickfeltes.interpreter.tokens;

/**
 * Tokens are the discrete items that make up a line of code.
 * An example of a token is "+" or "if".
  */
public class Token {

    private int lineNumber;
    private TokenType type;

    private Object value;

    public Token(int lineNumber, TokenType type, Object value) {
        this.lineNumber = lineNumber;
        this.type = type;
        this.value = value;
    }

    public TokenType getType() {
        return type;
    }

    public Object getValue() {
        return value;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    // generated for testing purposes
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Token token = (Token) o;

        if (lineNumber != token.lineNumber) return false;
        return type == token.type;
    }

    @Override
    public String toString() {
        return lineNumber + " " + type + " " + value;
    }
}
