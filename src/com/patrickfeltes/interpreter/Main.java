package com.patrickfeltes.interpreter;

import com.patrickfeltes.interpreter.ast.Expr;
import com.patrickfeltes.interpreter.ast.Parser;
import com.patrickfeltes.interpreter.files.FileUtilities;
import com.patrickfeltes.interpreter.tokens.Token;
import com.patrickfeltes.interpreter.tokens.TokenType;

import java.util.List;
import java.util.Scanner;

public class Main {

    private static boolean hadError = false;

    public static void main(String[] args) {
        if (args.length == 0) {
            Scanner scanner = new Scanner(System.in);
            List<Token> tokens;
            while (true) {
                String expression = scanner.nextLine();
                tokens = new Lexer(expression).lexTokens();
                Expr expr = new Parser(tokens).expression();
                System.out.println(new Interpreter().interpret(expr));
            }
        } else if (args.length != 1) {
            System.out.println("Invalid program arguments. Please provide the " +
                    "filepath to your file relative to this directory as an argument.");
        }

        System.out.println(FileUtilities.readFileToString(args[0]));
    }

    public static void error(int lineNumber, String message) {
        report(lineNumber, "", message);
    }

    public static void error(Token token, String message) {
        if (token.type == TokenType.EOF) {
            report(token.lineNumber, " at end", message);
        } else {
            report(token.lineNumber, "at '" + token.lexeme + "'", message);
        }
    }

    private static void report(int lineNumber, String where, String message) {
        System.err.println(
                "[line " + lineNumber + "] Error" + where + ": " + message);
        hadError = true;
    }

}
