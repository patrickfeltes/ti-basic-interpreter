package com.patrickfeltes.interpreter;

import com.patrickfeltes.interpreter.tokens.*;

import java.util.*;

import static com.patrickfeltes.interpreter.tokens.TokenType.*;

/**
 * The Lexer takes a source and splits it into tokens to be read by the parser.
 */
public class Lexer {

    private String source;

    // variable to store the start location of each token
    private int startPosition;
    private int currentPosition;
    private int lineNumber;

    private final List<Token> tokens = new ArrayList<>();

    private static Map<String, TokenType> keywords = new HashMap<>();

    static {
        keywords.put("and", AND);
        keywords.put("or", OR);
        keywords.put("xor", XOR);
        keywords.put("Disp", DISP);
        keywords.put("If", IF);
        keywords.put("Then", THEN);
        keywords.put("Else", ELSE);
        keywords.put("For", FOR);
        keywords.put("While", WHILE);
        keywords.put("Prompt", PROMPT);
        keywords.put("Input", INPUT);
        keywords.put("End", END);
        keywords.put("Lbl", LBL);
        keywords.put("Goto", GOTO);
        keywords.put("Repeat", REPEAT);
        keywords.put("Return", RETURN);
        keywords.put("Stop", STOP);
        keywords.put("Menu", MENU);

        keywords.put("Str0", STRING_IDENTIFIER);
        keywords.put("Str1", STRING_IDENTIFIER);
        keywords.put("Str2", STRING_IDENTIFIER);
        keywords.put("Str3", STRING_IDENTIFIER);
        keywords.put("Str4", STRING_IDENTIFIER);
        keywords.put("Str5", STRING_IDENTIFIER);
        keywords.put("Str6", STRING_IDENTIFIER);
        keywords.put("Str7", STRING_IDENTIFIER);
        keywords.put("Str8", STRING_IDENTIFIER);
        keywords.put("Str9", STRING_IDENTIFIER);
        keywords.put("abs", FUNCTION_IDENTIFIER);
    }

    public Lexer(String program) {
        this.source = program;
        lineNumber = 1;
        currentPosition = 0;
    }

    /**
     * Lexes all the tokens in the program.
     * @return A list of all of the tokens of the program, in order.
     */
    public List<Token> lexTokens() {
        while (!atEnd()) {
            startPosition = currentPosition;
            lexToken();
        }

        tokens.add(new Token(TokenType.EOF, "", null, lineNumber));

        return tokens;
    }

    /**
     * Lexes the next token.
     */
    private void lexToken() {
        // get the current token, and move the position pointer to the next token
        char c = advance();

        switch (c) {
            case 'L':
                if (match('₁','₂','₃','₄','₅','₆')) {
                    addToken(LIST_IDENTIFIER);
                    break;
                }
            case '(': addToken(LPAREN); break;
            case ')': addToken(RPAREN); break;
            case '[':
                if (match('A','B','C','D','E','F','G','H','I','J')) {
                    if (match(']')) {
                        addToken(MATRIX_IDENTIFIER);
                    } else {
                        currentPosition--;
                        addToken(LBRACKET);
                    }
                } else {
                    addToken(LBRACKET);
                }
                break;
            case ']': addToken(RBRACKET); break;
            case '{': addToken(LBRACE); break;
            case '}': addToken(RBRACE); break;
            case ',': addToken(COMMA); break;
            case '+': addToken(PLUS); break;
            case '-': addToken(match('>') ? STORE : MINUS); break;
            case '*': addToken(MUL); break;
            case '/': addToken(DIV); break;
            case ':':
                addToken(EOL);
                // handle multiple new lines in a row
                while (peek() == ':' || peek() == '\n') {
                    if (peek() == '\n') lineNumber++;
                    advance();
                }
                break;
            case '\n':
                addToken(EOL);
                // handle multiple new lines in a row
                while (peek() == ':' || peek() == '\n') {
                    if (peek() == '\n') lineNumber++;
                    advance();
                }
                lineNumber++;
                break;
            case '=': addToken(EQUAL); break;
            case '≠': addToken(NOT_EQUAL); break;
            case '>': addToken(match('=') ? GTOE : GT); break;
            case '<': addToken(match('=') ? LTOE : LT); break;
            case '≤': addToken(LTOE); break;
            case '≥': addToken(GTOE); break;
            case '^': addToken(POW); break;
            case '!': addToken(match('=') ? NOT_EQUAL : EXCLAMATION); break;
            case '"':
                string();
                break;
            case '→': addToken(STORE); break;
            case ' ':
            case '\t':
            case '\r':
                break;
            default:
                if (Character.isDigit(c)) {
                    number();
                } else if (Character.isAlphabetic(c)) {
                    identifier();
                } else {
                    Main.error(lineNumber, "Unexpected character.");
                }
        }
    }

    /**
     * Lexes a string into a token
     */
    private void string() {
        while (peek() != '"' && peek() != '\n' && !atEnd()) {
            advance();
        }

        if (atEnd() || peek() == '\n') {
            Main.error(lineNumber, "Unterminated String.");
            return;
        }

        // move past the ending quote
        advance();

        // trim off quotes
        String value = source.substring(startPosition + 1, currentPosition - 1);
        addToken(TokenType.STRING, value);
    }

    /**
     * Lexes an identifier(a variable name or a keyword) and adds it to the token list.
     */
    private void identifier() {
        while (Character.isAlphabetic(peek()) || Character.isDigit(peek())) advance();

        String text = source.substring(startPosition, currentPosition);

        TokenType type = keywords.get(text);
        if (type == null) {
            type = IDENTIFIER;
            // set the current position to 1 after the start, so that an identifier can only be one character long
            currentPosition = startPosition + 1;
            if (currentPosition >= source.length()) currentPosition = source.length();
        }
        addToken(type);
    }

    /**
     * Lexes a number and adds it as a token to the token list.
     */
    private void number() {
        while(Character.isDigit(peek())) advance();

        if (peek() == '.' && Character.isDigit(peekNext())) {
            advance();

            while (Character.isDigit(peek())) advance();
        }

        addToken(NUMBER, Double.parseDouble(source.substring(startPosition, currentPosition)));
    }

    /**
     * Peek at the current char if it's not at the end of the source.
     * @return the current char.
     */
    private char peek() {
        if (atEnd()) return '\0';
        return source.charAt(currentPosition);
    }

    /**
     * Peeks at the next char if it's not the end of the source.
     * @return the next char.
     */
    private char peekNext() {
        if (currentPosition + 1 >= source.length()) return '\0';
        return source.charAt(currentPosition + 1);
    }

    /**
     * Helper method to determine if the current character matches the desired character.
     * If that is true, move the next character and return true, else return false.
     * @param expected the expected character to match.
     * @return true if the current character matches the expected, false otherwise.
     */
    private boolean match(char expected) {
        if (atEnd()) return false;
        if (source.charAt(currentPosition) != expected) return false;

        currentPosition++;
        return true;
    }

    private boolean match(char... expected) {
        if (atEnd()) return false;
        for (char c : expected) {
            if (source.charAt(currentPosition) == c) {
                currentPosition++;
                return true;
            }
        }

        return false;
    }

    /**
     * Helper method to determine if the current position is at the end of the source
     * @return true if at end, false otherwise
     */
    private boolean atEnd() {
        return currentPosition >= source.length();
    }

    /**
     * Advances to the next char and gives the previous char.
     * @return the previous char after the advance.
     */
    private char advance() {
        currentPosition++;
        return source.charAt(currentPosition - 1);
    }

    /**
     * Adds a token of the current type to the list of tokens.
     * @param type the type of token to add.
     */
    private void addToken(TokenType type) {
        addToken(type, null);
    }

    private void addToken(TokenType type, Object literal) {
        String text = source.substring(startPosition, currentPosition);
        tokens.add(new Token(type, text, literal, lineNumber));
    }

}
