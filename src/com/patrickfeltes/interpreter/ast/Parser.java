package com.patrickfeltes.interpreter.ast;

import com.patrickfeltes.interpreter.Main;
import com.patrickfeltes.interpreter.errors.ParseError;
import com.patrickfeltes.interpreter.tokens.Token;
import com.patrickfeltes.interpreter.tokens.TokenType;
import com.patrickfeltes.interpreter.util.Pair;

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
                                    | ifStatement
                                    | whileStatement
                                    | forStatement
                                    | gotoStatement
                                    | labelStatement
                                    | repeatStatement
                                    | returnStatement
                                    | stopStatement
                                    | menuStatement ;

            exprOrAssignStatement   : expression (STO (IDENTIFIER | STRING_IDENTIFIER | (MATRIX_IDENTIFIER ("(" expression "," expression ")")? | (LIST_IDENTIFIER ("(" expression ")")?))? (EOL | EOF) ;
            dispStatement           : "Disp" expression ("," expression) (EOL | EOF) ;
            promptStatement         : "Prompt" IDENTIFIER ("," IDENTIFIER)* (EOL | EOF) ;
            inputStatement          : "Input" (STRING ",")? IDENTIFIER? (EOL | EOF);
            ifStatement             : "If" expression EOL (("Then" EOL statement* ("Else" EOL statement*)? "End") | statement) (EOL | EOF) ;
            whileStatement          : "While" expression EOL statement* "END" (EOL | EOF)
            forStatement            : "For" "(" IDENTIFIER "," expression "," expression ("," expression)? ")" statement* "End" (EOL | EOF) ;
            labelStatement          : "Lbl" labelIdentifier (EOL | EOF) ;
            gotoStatement           : "Goto" labelIdentifier (EOL | EOF) ;
            repeatStatement         : "Repeat" expression EOL statement* "End" (EOL | EOF) ;
            returnStatement         : "Return" (EOL | EOF) ;
            stopStatement           : "Stop" (EOL | EOF) ;
            menuStatement           :

            labelIdentifier         : (IDENTIFIER IDENTIFIER?)
                                    | (NUMBER IDENTIFIER?)
                                    | (IDENTIFIER NUMBER?) ;

            // Expressions
            expression              : logic_or ;
            logic_or                : logic_and ("or" logic_and)* ;
            logic_and               : equality ("and" equality)* ;
            equality                : comparison (("=" | "≠") comparison)* ;
            comparison              : addition ((">" | ">=" | "<" | "<=" addition)* ;
            addition                : multiplication (("+" | "-") multiplication)* ;
            multiplication          : unary (("*" | "/") unary)* ;
            unary                   : (("+" | "-") unary) | exponent ;
            exponent                : (call "^" exponent) | unary ;
            call                    : FUNCTION_IDENTIFIER "(" arguments? ")"
                                    | primary;
            arguments               : expression ( "," expression )* ;
            primary                 : NUMBER
                                    | STRING
                                    | IDENTIFIER
                                    | (LIST_IDENTIFIER ("(" expression ")")?
                                    | (MATRIX_IDENTIFIER ("(" expression "," expression ")")?)
                                    | STRING_IDENTIFIER
                                    | ("(" expression ")")
                                    | list
                                    | matrix ;
            list                    : "{" expression ("," expression)* "}" ;
            matrix                  : "[" matrix_list* "]" ;
            matrix_list             : "[" expression ("," expression)* "]" ;
     */

    public Stmt parse() {
        Stmt head = null;
        Stmt tail = null;
        while (!atEnd()) {
            if (head == null) {
                head = statement();
                tail = head;
            } else {
                tail.setNext(statement());
                tail = tail.next();
            }
        }

        return head;
    }

    private Stmt statement() {
        if (match(DISP)) return dispStatement();
        if (match(PROMPT)) return promptStatement();
        if (match(INPUT)) return inputStatement();
        if (match(IF)) return ifStatement();
        if (match(WHILE)) return whileStatement();
        if (match(FOR)) return forStatement();
        if (match(GOTO)) return gotoStatement();
        if (match(LBL)) return labelStatement();
        if (match(REPEAT)) return repeatStatement();
        if (match(RETURN)) return returnStatement();
        if (match(STOP)) return stopStatement();
        if (match(MENU)) return menuStatement();

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
            Token name;
            Expr listIndex = null;
            Pair<Expr, Expr> matrixIndex = null;
            if (match(IDENTIFIER, LIST_IDENTIFIER, MATRIX_IDENTIFIER, STRING_IDENTIFIER)) {
                name = previous();
                if (previous().type == LIST_IDENTIFIER) {
                    if (match(LPAREN)) {
                        listIndex = expression();
                        eat(RPAREN, "Expect ')' after index expression.");
                    }
                } else if (previous().type == MATRIX_IDENTIFIER) {
                    if (match(LPAREN)) {
                        Expr row = expression();
                        eat(COMMA, "Expect comma between row and column.");
                        Expr col = expression();
                        matrixIndex = new Pair<>(row, col);
                        eat(RPAREN, "Expect ')' after row and col expressions.");
                    }
                }
            } else {
                throw error(peek(), "Expect identifier.");
            }

            if (!atEnd()) {
                eat(EOL, "Expect a new line after assign statement");
            }
            return new Stmt.Assign(expr, name, listIndex, matrixIndex);
        }

        // no need for an end of line if it is the end of the file
        if (!atEnd()) {
            eat(EOL, "Expect a new line after expression");
        }

        return new Stmt.Expression(expr);
    }

    private Stmt ifStatement() {
        Expr condition = expression();
        eat(EOL, "Expect new line after condition.");

        Stmt thenHead = null;
        Stmt thenTail = null;
        Stmt elseHead = null;
        Stmt elseTail = null;

        if (match(THEN)) {
            eat(EOL, "Expect new line after then.");
            while (!match(END, ELSE)) {
                if (thenHead == null) {
                    thenHead = statement();
                    thenTail = thenHead;
                } else {
                    thenTail.setNext(statement());
                    thenTail = thenTail.next();
                }
            }

            if (previous().type == ELSE) {
                eat(EOL, "Expect new line after else");
                while (!match(END)) {
                    if (elseHead == null) {
                        elseHead = statement();
                        elseTail = elseHead;
                    } else {
                        elseTail.setNext(statement());
                        elseTail = elseTail.next();
                    }
                }
            }
        } else {
            thenHead = statement();
        }

        if (!atEnd()) {
            eat(EOL, "Expect new line after END");
        }

        return new Stmt.If(condition, thenHead, elseHead);
    }

    private Stmt whileStatement() {
        Expr condition = expression();
        eat(EOL, "Expect new line after condition.");

        Stmt head = null;
        Stmt tail = null;
        while (!match(END)) {
            if (head == null) {
                head = statement();
                tail = head;
            } else {
                tail.setNext(statement());
                tail = tail.next();
            }
        }

        if (!atEnd()) {
            eat(EOL, "Expect new line after END");
        }

        return new Stmt.While(condition, head);
    }

    private Stmt forStatement() {
        eat(LPAREN, "Expect '(' after For.");
        Token name = eat(IDENTIFIER, "Expect an identifier after '('");
        eat(COMMA, "Expect a comma after identifier.");
        Expr start = expression();
        eat(COMMA, "Expect a comma after start argument.");
        Expr end = expression();
        Expr step = new Expr.Literal(1.0, Expr.Literal.LiteralType.DOUBLE);
        if (match(COMMA)) {
            step = expression();
        }
        eat(RPAREN, "Expect ')' after for statement");
        eat(EOL, "Expect new line after For statement.");

        Stmt head = null;
        Stmt tail = null;
        while (!match(END)) {
            if (head == null) {
                head = statement();
                tail = head;
            } else {
                tail.setNext(statement());
                tail = tail.next();
            }
        }

        if (!atEnd()) {
            eat(EOL, "Expect new line after End");
        }

        return new Stmt.For(name, start, end, step, head);
    }

    private Stmt gotoStatement() {
        String label = labelIdentifier();

        if (!atEnd()) {
            eat(EOL, "Expect a new line after a Goto statement.");
        }

        return new Stmt.Goto(label);
    }

    private Stmt labelStatement() {
        String label = labelIdentifier();

        if (!atEnd()) {
            eat(EOL, "Expect a new line after a Lbl statement.");
        }

        return new Stmt.Label(label);
    }

    private Stmt repeatStatement() {
        Expr condition = expression();
        eat(EOL, "Expect new line after condition.");

        Stmt head = null;
        Stmt tail = null;
        while (!match(END)) {
            if (head == null) {
                head = statement();
                tail = head;
            } else {
                tail.setNext(statement());
                tail = tail.next();
            }
        }

        if (!atEnd()) {
            eat(EOL, "Expect new line after End.");
        }

        return new Stmt.Repeat(condition, head);
    }

    private Stmt returnStatement() {
        eat(EOL, "Expect new line after Return.");
        return new Stmt.Return();
    }

    private Stmt stopStatement() {
        eat(EOL, "Expect new line after Stop.");
        return new Stmt.Stop();
    }

    private Stmt menuStatement() {
        eat(LPAREN, "Expect '(' after Menu.");
        String title = (String)eat(STRING, "Expect a string for the title.").literal;
        eat(COMMA, "Expect comma after title.");
        List<String> options = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        options.add((String)eat(STRING, "Expect a string for an option.").literal);
        eat(COMMA, "Expect comma after option string.");
        labels.add(labelIdentifier());
        int count = 1;
        while (match(COMMA)) {
            count++;
            if (count > 7) {
                throw error(peek(), "Can't have more than 7 menu items.");
            } else {
                String option = (String)eat(STRING, "Expect a string for an option.").literal;
                options.add(option);
                eat(COMMA, "Expect comma after option string.");
                labels.add(labelIdentifier());
            }
        }
        eat(RPAREN, "Expect ')' after Menu declaration.");
        if (!atEnd()) {
            eat(EOL, "Expect new line after Menu declaration.");
        }
        return new Stmt.Menu(title, options, labels);
    }

    private String labelIdentifier() {
        StringBuilder builder = new StringBuilder();
        if (match(IDENTIFIER)) {
            builder.append(previous().lexeme);
            if (match(IDENTIFIER)) {
                builder.append(previous().lexeme);
            } else if (match(NUMBER)) {
                builder.append(previous().lexeme);
            }
        } else if (match(NUMBER)) {
            builder.append(previous().lexeme);
            if (match(NUMBER)) {
                builder.append(previous().lexeme);
            } else if (match(IDENTIFIER)) {
                builder.append(previous().lexeme);
            }
        }

        String label = builder.toString();
        // label identifiers must be at most two characters
        if (label.length() == 0 || label.length() > 2) {
            throw error(previous(), "Invalid label");
        }

        return label;
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
        Expr expr = call();

        // if there is a power, we need to call exponent again for right-associativity
        while (match(POW)) {
            Token operator = previous();
            Expr right = unary();
            return new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr call() {
        if (match(FUNCTION_IDENTIFIER)) {
            Token callee = previous();
            eat(LPAREN, "Expect '(' after function name.");
            List<Expr> arguments = null;
            if (!match(RPAREN)) {
                arguments = arguments();
                eat(RPAREN, "Expect ')' after function arguments.");
            } else {
                arguments = new ArrayList<>();
            }
            return new Expr.Call(callee, arguments);
        } else {
            return primary();
        }
    }

    private List<Expr> arguments() {
        List<Expr> arguments = new ArrayList<>();
        arguments.add(expression());
        while (match(COMMA)) {
            arguments.add(expression());
        }
        return arguments;
    }

    private Expr primary() {
        if (match(NUMBER)) {
            return new Expr.Literal(previous().literal, Expr.Literal.LiteralType.DOUBLE);
        }

        if (match(STRING)) {
            return new Expr.Literal(previous().literal, Expr.Literal.LiteralType.STRING);
        }

        if (match(LBRACE)) {
            return list();
        }

        if (match(LBRACKET)) {
            return matrix();
        }

        if (match(LPAREN)) {
            Expr expr = expression();
            eat(RPAREN, "Expect ')' after expression.");
            return new Expr.Grouping(expr);
        }

        if (match(IDENTIFIER, STRING_IDENTIFIER)) {
            return new Expr.Variable(previous(), null, null);
        }

        if (match(LIST_IDENTIFIER)) {
            Expr listIndex = null;
            Token name = previous();
            if (match(LPAREN)) {
                listIndex = expression();
                eat(RPAREN, "Expect ')' after index expression.");
            }
            return new Expr.Variable(name, listIndex, null);
        }

        if (match(MATRIX_IDENTIFIER)) {
            Pair<Expr, Expr> matrixIndex = null;
            Token name = previous();
            if (match(LPAREN)) {
                Expr row = expression();
                eat(COMMA, "Expect comma between row and column.");
                Expr col = expression();
                matrixIndex = new Pair<>(row, col);
                eat(RPAREN, "Expect ')' after row and col expressions.");
            }
            return new Expr.Variable(name, null, matrixIndex);
        }

        throw error(peek(), "Expect expression.");
    }

    private Expr list() {
        List<Expr> elements = new ArrayList<>();
        elements.add(expression());
        while (match(COMMA)) {
            elements.add(expression());
        }
        eat(RBRACE, "Expect right brace after list.");
        return new Expr.Literal(elements, Expr.Literal.LiteralType.LIST);
    }

    private Expr matrix() {
        List<List<Expr>> matrix = new ArrayList<>();
        while (match(LBRACKET)) {
            matrix.add(matrixList());
        }
        eat(RBRACKET, "Expect closing right bracket after matrix literal.");
        if (matrix.size() == 0) throw error(previous(), "Can't initialize empty matrix.");
        return new Expr.Literal(matrix, Expr.Literal.LiteralType.MATRIX);
    }

    private List<Expr> matrixList() {
        List<Expr> expressions = new ArrayList<>();
        expressions.add(expression());
        while (match(COMMA)) {
            expressions.add(expression());
        }
        eat(RBRACKET, "Expect closing right bracket for each row.");
        return expressions;
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
