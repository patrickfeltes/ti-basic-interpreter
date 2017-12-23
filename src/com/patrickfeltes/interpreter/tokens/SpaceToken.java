package com.patrickfeltes.interpreter.tokens;

/**
 * A token that represents a space.
 */
public class SpaceToken extends Token {

    public SpaceToken(int lineNumber) {
        super(lineNumber, TokenType.SPACE);
    }
}
