package com.patrickfeltes.interpreter.ast;

import com.patrickfeltes.interpreter.Main;
import com.patrickfeltes.interpreter.errors.ParseError;
import com.patrickfeltes.interpreter.tokens.Token;
import com.patrickfeltes.interpreter.tokens.TokenType;

import static com.patrickfeltes.interpreter.tokens.TokenType.*;

import java.util.List;

/**
 * The parser class takes a list of tokens and parses it into a meaningful syntax tree, which can then be evaluated by
 * the interpreter.
 */
public class Parser {

    private final List<Token> tokens;
    private int currentPosition = 0;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    /*
        Explanation of Precedence:
            Precedence is defined by location within the grammar.
            Rules that are defined lower in the syntax tree have higher precedence because the interpreter will
            evaluate the lower nodes and work its way back up.

        Format:
            rule-name : rule definition ;

        Legend:
            *   = zero or more times
            "x" = token with x as its lexeme
            ?   = optional
            |   = exclusive or

        Current Grammar:
            expression     : addition ;
            addition       : multiplication (("+" | "-") multiplication)* ;
            multiplication : unary (("*" | "/") unary)* ;
            unary          : (("+" | "-") unary) | exponent ;
            exponent       : (primary "^" exponent) | unary ;
            primary        : NUMBER | STRING | ("(" expression ")") ;
     */


    public Expr expression() {
        return addition();
    }

    private Expr addition() {
        Expr expr = multiplication();

        while (match(PLUS, MINUS)) {
            Token operator = previous();
            Expr right = multiplication();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr multiplication() {
        Expr expr = unary();

        while (match(MUL, DIV)) {
            Token operator = previous();
            Expr right = unary();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr unary() {
        if (match(PLUS, MINUS)) {
            Token operator = previous();
            Expr right = unary();
            return new Expr.Unary(operator, right);
        }

        return exponent();
    }

    private Expr exponent() {
        Expr expr = primary();

        // if there is a power, we need to call exponent again for right-associativity
        while (match(POW)) {
            Token operator = previous();
            Expr right = unary();
            return new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr primary() {
        if (match(NUMBER, STRING)) {
            return new Expr.Literal(previous().literal);
        }

        if (match(LPAREN)) {
            Expr expr = expression();
            eat(RPAREN, "Expect ')' after expression.");
            return new Expr.Grouping(expr);
        }

        throw error(peek(), "Expect expression.");
    }

    // helper methods
    private Token eat(TokenType type, String message) {
        if (check(type)) return advance();

        throw error(peek(), message);
    }

    private ParseError error(Token token, String message) {
        Main.error(token, message);
        return new ParseError();
    }

    private Token peek() {
        return tokens.get(currentPosition);
    }

    private Token previous() {
        return tokens.get(currentPosition - 1);
    }

    private boolean atEnd() {
        return peek().type == TokenType.EOF;
    }

    private boolean check(TokenType type) {
        if (atEnd()) return false;
        return peek().type == type;
    }

    private boolean match(TokenType... types) {
        for (TokenType type: types) {
            if (check(type)) {
                advance();
                return true;
            }
        }

        return false;
    }

    private Token advance() {
        if (!atEnd()) currentPosition++;
        return previous();
    }
}
