package com.patrickfeltes.interpreter.tokens;

/**
 * Tokens are the discrete items that make up a line of code.
 * An example of a token is "+" or "if".
  */
public abstract class Token {

    protected int lineNumber;
    protected TokenType type;

    public Token(int lineNumber, TokenType type) {
        this.lineNumber = lineNumber;
        this.type = type;
    }

    public TokenType getType() {
        return type;
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
}
