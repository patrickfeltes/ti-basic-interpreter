package com.patrickfeltes.interpreter.tokens;

public class TokenFactory {

    public static Token createToken(int lineNumber, TokenType type) {
        switch(type) {
            case EOF:
                return new Token(lineNumber, type, "EOF");
            case EOL:
                return new Token(lineNumber, type, "EOL");
            case SPACE:
                return new Token(lineNumber, type, " ");
            case DISP:
                return new Token(lineNumber, type, "Disp");
            case PLUS:
                return new Token(lineNumber, type, "+");
            case MINUS:
                return new Token(lineNumber, type, "-");
            case MUL:
                return new Token(lineNumber, type, "*");
            case DIV:
                return new Token(lineNumber, type, "/");
            case LPAREN:
                return new Token(lineNumber, type, "(");
            case RPAREN:
                return new Token(lineNumber, type, ")");
            case LBRACKET:
                return new Token(lineNumber, type, "[");
            case RBRACKET:
                return new Token(lineNumber, type, "]");
            case LBRACE:
                return new Token(lineNumber, type, "{");
            case RBRACE:
                return new Token(lineNumber, type, "}");
            case QUOTE:
                return new Token(lineNumber, type, "\"");
            case STORE:
                return new Token(lineNumber, type, "->");
            case POW:
                return new Token(lineNumber, type, "^");
            case EXCLAMATION:
                return new Token(lineNumber, type, "!");
            case LT:
                return new Token(lineNumber, type, "<");
            case GT:
                return new Token(lineNumber, type, ">");
            case LTOE:
                return new Token(lineNumber, type, "≤");
            case GTOE:
                return new Token(lineNumber, type, "≥");
            case EQUAL:
                return new Token(lineNumber, type, "=");
            case NOT_EQUAL:
                return new Token(lineNumber, type, "≠");
            default:
                return null;
        }
    }

    public static Token createVariableToken(int lineNumber, char variableName) {
        return new Token(lineNumber, TokenType.VARIABLE, variableName);
    }

    public static Token createNumberToken(int lineNumber, double value) {
        return new Token(lineNumber, TokenType.NUMBER, value);
    }


}
