package com.patrickfeltes.interpreter.tokens;

/**
 * A token to signify the end of a file.
 */
public class EOFToken extends Token {

    public EOFToken(int lineNumber) {
        super(lineNumber, TokenType.EOF);
    }
}
