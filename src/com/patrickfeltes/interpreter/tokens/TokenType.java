package com.patrickfeltes.interpreter.tokens;

/**
 * Enum used for different types of tokens
 */
public enum TokenType {

    // "single character" tokens
    PLUS, MINUS, MUL, DIV, POW, EXCLAMATION,
    EQUAL, NOT_EQUAL, GT, LT, GTOE, LTOE,
    LPAREN, RPAREN, LBRACKET, RBRACKET,
    LBRACE, RBRACE, STORE, COMMA,

    // different types
    NUMBER,
    IDENTIFIER,
    STRING,

    // keywords
    DISP, AND, OR, XOR, WHILE, FOR, IF,
    ELSE, THEN, PROMPT, INPUT, END, GOTO, LBL,
    REPEAT, RETURN, STOP,

    // ends
    EOL, EOF

}
