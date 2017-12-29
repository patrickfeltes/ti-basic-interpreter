package com.patrickfeltes.interpreter.tokens;

import java.util.Objects;

/**
 * Tokens are the discrete items that make up a line of code.
 * An example of a token is "+" or "if".
  */
public class Token {

    public final TokenType type;
    public final String lexeme;
    public final Object literal;
    public final int lineNumber;

    public Token(TokenType type, String lexeme, Object literal, int lineNumber) {
        this.type = type;
        this.lexeme = lexeme;
        this.literal = literal;
        this.lineNumber = lineNumber;
    }

    // generated for testing only
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Token token = (Token) o;
        return lineNumber == token.lineNumber &&
                type == token.type &&
                Objects.equals(lexeme, token.lexeme) &&
                Objects.equals(literal, token.literal);
    }

    @Override
    public String toString() {
        return "Token{" +
                "type=" + type +
                ", lexeme='" + lexeme + '\'' +
                ", literal=" + literal +
                ", lineNumber=" + lineNumber +
                '}';
    }
}
