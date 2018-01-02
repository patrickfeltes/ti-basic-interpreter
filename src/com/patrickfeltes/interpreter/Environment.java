package com.patrickfeltes.interpreter;

import com.patrickfeltes.interpreter.errors.RuntimeError;
import com.patrickfeltes.interpreter.tokens.Token;

import java.util.HashMap;
import java.util.Map;

public class Environment {

    private final Map<String, Object> values = new HashMap<>();

    public Environment() {
        defineVariables();
    }

    public void defineVariables() {
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        for (int i = 0; i < alphabet.length(); i++) {
            values.put("" + alphabet.charAt(i), 0);
        }
    }

    public Object get(Token name) {
        if (values.containsKey(name.lexeme)) return values.get(name.lexeme);

        throw new RuntimeError(name, "Undefined variable '" + name.lexeme + "'.");
    }

    public void assign(Token name, Object value) {
        if (!(value instanceof Double)) throw new RuntimeError(name, "Cannot assign a non-number to a number variable.");

        values.put(name.lexeme, value);
    }

}
