package com.patrickfeltes.interpreter.tokens;

/**
 * A token to designate the end of a line (i.e. the start of a new one)
 */
public class EOLToken extends Token {

    public EOLToken(int lineNumber) {
        super(lineNumber, TokenType.EOL);
    }

}
