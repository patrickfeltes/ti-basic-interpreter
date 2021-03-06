package com.patrickfeltes.interpreter;

import com.patrickfeltes.interpreter.ast.Parser;
import com.patrickfeltes.interpreter.ast.Stmt;
import com.patrickfeltes.interpreter.errors.RuntimeError;
import com.patrickfeltes.interpreter.files.FileUtilities;
import com.patrickfeltes.interpreter.visitors.Interpreter;
import com.patrickfeltes.interpreter.tokens.Token;
import com.patrickfeltes.interpreter.tokens.TokenType;
import com.patrickfeltes.interpreter.visitors.LabelMarker;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Main {

    private static boolean hadError = false;
    private static boolean hadRuntimeError = false;

    private static Interpreter interpreter = new Interpreter();

    public static void main(String[] args) {
        if (args.length == 0) {
            Scanner scanner = new Scanner(System.in);
            while (true) {
                execute(scanner.nextLine());
            }
        } else if (args.length == 1) {
            execute(FileUtilities.readFileToString(args[0]));
        } else {
            System.out.println("Invalid program arguments. Please provide the " +
                    "filepath to your file relative to this directory as an argument.");
        }
    }

    public static void execute(String program) {
        List<Token> tokens = new Lexer(program).lexTokens();
        System.out.println(tokens);
        Stmt head = new Parser(tokens).parse();
        Map<String, Stmt> labels = new LabelMarker().getLabels(head);
        interpreter.interpret(labels, head);
    }

    public static void error(int lineNumber, String message) {
        report(lineNumber, "", message);
    }

    public static void error(Token token, String message) {
        if (token.type == TokenType.EOF) {
            report(token.lineNumber, " at end", message);
        } else {
            report(token.lineNumber, " at '" + token.lexeme + "'", message);
        }
    }

    private static void report(int lineNumber, String where, String message) {
        System.err.println(
                "[line " + lineNumber + "] Error" + where + ": " + message);
        hadError = true;
    }

    public static void runtimeError(RuntimeError error) {
        System.err.println(error.getMessage() +
                "\n[line " + error.token.lineNumber + "]");
        hadRuntimeError = true;
    }

}
