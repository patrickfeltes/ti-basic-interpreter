package com.patrickfeltes.interpreter.ast;

import com.patrickfeltes.interpreter.Main;
import com.patrickfeltes.interpreter.errors.ParseError;
import com.patrickfeltes.interpreter.tokens.Token;
import com.patrickfeltes.interpreter.tokens.TokenType;

import static com.patrickfeltes.interpreter.tokens.TokenType.*;

import java.util.ArrayList;
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

            // Statements
            program                 : statement* EOF ;
            statement               : exprStatement
                                    | dispStatement
                                    | promptStatement
                                    | inputStatement
                                    | assignStatement
                                    | ifStatement ;

            exprOrAssignStatement   : expression (STO IDENTIFIER)? (EOL | EOF) ;
            dispStatement           : "Disp" expression ("," expression) (EOL | EOF) ;
            promptStatement         : "Prompt" IDENTIFIER ("," IDENTIFIER)* (EOL | EOF) ;
            inputStatement          : "Input" (STRING ",")? IDENTIFIER? (EOL | EOF);
            ifStatement             : "If" expression EOL (("Then" EOL statement* ("Else" EOL statement*)? "End") | statement) (EOL | EOF) ;

            // Expressions
            expression              : logic_or ;
            logic_or                : logic_and ("or" logic_and)* ;
            logic_and               : equality ("and" equality)* ;
            equality                : comparison (("=" | "≠") comparison)* ;
            comparison              : addition ((">" | ">=" | "<" | "<=" addition)* ;
            addition                : multiplication (("+" | "-") multiplication)* ;
            multiplication          : unary (("*" | "/") unary)* ;
            unary                   : (("+" | "-") unary) | exponent ;
            exponent                : (primary "^" exponent) | unary ;
            primary                 : NUMBER | STRING | IDENTIFIER | ("(" expression ")") ;
     */

    private Stmt ifStatement() {
        Expr condition = expression();
        eat(EOL, "Expect new line after condition.");

        List<Stmt> thenBranch = new ArrayList<>();
        List<Stmt> elseBranch = new ArrayList<>();

        if (match(THEN)) {
            eat(EOL, "Expect new line after then.");
            while (!match(END, ELSE)) {
                thenBranch.add(statement());
            }

            if (previous().type == ELSE) {
                eat(EOL, "Expect new line after else");
                while (!match(END)) {
                    elseBranch.add(statement());
                }
            }
        } else {
            thenBranch.add(statement());
        }

        if (!atEnd()) {
            eat(EOL, "Expect new line after END");
        }

        return new Stmt.If(condition, thenBranch, elseBranch);
    }



    public List<Stmt> parse() {
        List<Stmt> statements = new ArrayList<>();
        while (!atEnd()) {
            statements.add(statement());
        }

        return statements;
    }

    private Stmt statement() {
        if (match(DISP)) return dispStatement();
        if (match(PROMPT)) return promptStatement();
        if (match(INPUT)) return inputStatement();
        if (match(IF)) return ifStatement();

        return exprOrAssignStatement();
    }

    private Stmt dispStatement() {
        List<Expr> expressions = new ArrayList<>();
        expressions.add(expression());

        while (match(COMMA)) {
            expressions.add(expression());
        }

        // no need for an end of line if it is the end of the file
        if (!atEnd()) {
            eat(EOL, "Expect a new line after value(s)");
        }

        return new Stmt.Disp(expressions);
    }

    private Stmt promptStatement() {
        List<Token> names = new ArrayList<>();
        names.add(eat(IDENTIFIER, "Expect an identifier."));
        while (!match(EOL) && !atEnd()) {
            eat(COMMA, "Expect a comma separating identifiers.");
            names.add(eat(IDENTIFIER, "Expect an identifier."));
        }

        if (!atEnd()) {
            eat(EOL, "Expect new line after Prompt statement.");
        }

        return new Stmt.Prompt(names);
    }

    private Stmt inputStatement() {
        Stmt toReturn = null;

        if (match(STRING)) {
            String prompt = (String)previous().literal;
            eat(COMMA, "Expect a comma after message.");
            Token name = eat(IDENTIFIER, "Expect an identifier after comma.");
            toReturn = new Stmt.Input(prompt, name);
        } else if (match(IDENTIFIER)) {
            toReturn = new Stmt.Input(previous());
        } else if (match(EOL) || atEnd()) {
            return new Stmt.Input();
        }

        if (atEnd()) {
            return toReturn;
        } else {
            eat(EOL, "Expect new line after input statement");
            return toReturn;
        }
    }

    private Stmt exprOrAssignStatement() {
        Expr expr = expression();

        if (match(STORE)) {
            Token name = eat(IDENTIFIER, "Expect an identifier after store.");
            eat(EOL, "Expect a new line after assign statement");
            return new Stmt.Assign(expr, name);
        }

        // no need for an end of line if it is the end of the file
        if (!atEnd()) {
            eat(EOL, "Expect a new line after expression");
        }

        return new Stmt.Expression(expr);
    }

    public Expr expression() {
        return or();
    }

    private Expr or() {
        Expr expr = and();

        while (match(OR)) {
            Token operator = previous();
            Expr right = and();
            expr = new Expr.Logical(expr, operator, right);
        }

        return expr;
    }

    private Expr and() {
        Expr expr = equality();

        while (match(AND)) {
            Token operator = previous();
            Expr right = equality();
            expr = new Expr.Logical(expr, operator, right);
        }

        return expr;
    }

    private Expr equality() {
        Expr expr = comparison();

        while (match(TokenType.NOT_EQUAL, TokenType.EQUAL)) {
            Token operator = previous();
            Expr right = comparison();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr comparison() {
        Expr expr = addition();

        while (match(TokenType.GT, TokenType.GTOE, TokenType.LT, TokenType.LTOE)) {
            Token operator = previous();
            Expr right = addition();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
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

        if (match(IDENTIFIER)) {
            return new Expr.Variable(previous());
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
