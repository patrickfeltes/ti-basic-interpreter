package com.patrickfeltes.interpreter.tokens.reserved;

import com.patrickfeltes.interpreter.tokens.Token;
import com.patrickfeltes.interpreter.tokens.TokenType;

/**
 * A token that represents a Disp command.
 */
public class DispToken extends Token {

    public DispToken(int lineNumber) {
        super(lineNumber, TokenType.DISP);
    }

}
