package com.patrickfeltes.interpreter.errors;

import com.patrickfeltes.interpreter.tokens.Token;

public class RuntimeError extends RuntimeException {
    public final Token token;

    public RuntimeError(Token token, String message) {
        super(message);
        this.token = token;
    }
}