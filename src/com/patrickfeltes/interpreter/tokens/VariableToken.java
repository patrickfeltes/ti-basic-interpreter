package com.patrickfeltes.interpreter.tokens;

/**
 * A token for variables, which are of the form A,B,C,...,Z
 */
public class VariableToken extends Token {

    private char variableName;

    public VariableToken(int lineNumber, char variableName) {
        super(lineNumber, TokenType.VARIABLE);
        this.variableName = variableName;
    }

    // generated for testing purposes
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VariableToken that = (VariableToken) o;

        return variableName == that.variableName;
    }
}
