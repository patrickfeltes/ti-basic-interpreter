package com.patrickfeltes.interpreter.visitors;

import com.patrickfeltes.interpreter.Lexer;
import com.patrickfeltes.interpreter.Main;
import com.patrickfeltes.interpreter.ast.Expr;
import com.patrickfeltes.interpreter.ast.Stmt;
import com.patrickfeltes.interpreter.ast.Parser;
import com.patrickfeltes.interpreter.data_types.TiList;
import com.patrickfeltes.interpreter.data_types.TiMatrix;
import com.patrickfeltes.interpreter.errors.RuntimeError;
import com.patrickfeltes.interpreter.exceptions.GotoException;
import com.patrickfeltes.interpreter.exceptions.ReturnException;
import com.patrickfeltes.interpreter.exceptions.StopException;
import com.patrickfeltes.interpreter.tokens.Token;

import java.util.*;

import static com.patrickfeltes.interpreter.tokens.TokenType.AND;
import static com.patrickfeltes.interpreter.tokens.TokenType.OR;

public class Interpreter implements Expr.Visitor<Object>, Stmt.Visitor<Void> {

    private Scanner userInput = new Scanner(System.in);

    private final double TRUE = 1.0;
    private final double FALSE = 0.0;

    private Environment environment = new Environment();

    public void interpret(Map<String, Stmt> labels, Stmt head) {
        Stmt statement = head;
        while (statement != null) {
            try {
                executeSingleStatement(statement);
                statement = statement.next();
            } catch (RuntimeError error) {
                Main.runtimeError(error);
                statement = null;
            } catch (GotoException ex) {
                Stmt target = labels.get(ex.label);
                if (target == null) throw ex;
                statement = target;
            } catch (ReturnException ex) {
                // TODO: should leave subprogram ONLY. once subprogram implemented
                statement = null;
            } catch (StopException ex) {
                statement = null;
            }
        }
    }

    @Override
    public Object visitBinaryExpr(Expr.Binary expr) {
        Object left = evaluate(expr.left);
        Object right = evaluate(expr.right);

        switch (expr.operator.type) {
            case PLUS:
                return (double)left + (double)right;
            case MINUS:
                return (double)left - (double)right;
            case MUL:
                return (double)left * (double)right;
            case DIV:
                return (double)left / (double)right;
            case POW:
                return Math.pow((double)left, (double)right);
            case GT:
                return ((double)left > (double)right) ? TRUE : FALSE;
            case GTOE:
                return ((double)left >= (double)right) ? TRUE : FALSE;
            case LT:
                return ((double)left < (double)right) ? TRUE : FALSE;
            case LTOE:
                return ((double)left <= (double)right) ? TRUE : FALSE;
            case EQUAL:
                return ((double)(left) == (double)right) ? TRUE : FALSE;
            case NOT_EQUAL:
                return ((double)left != (double)right) ? TRUE : FALSE;
        }

        return null;
    }

    @Override
    public Object visitGroupingExpr(Expr.Grouping expr) {
        return evaluate(expr.inside);
    }

    @Override
    public Object visitLiteralExpr(Expr.Literal expr) {
        // TODO: throw error if expressions don't eval to doubles
        if (expr.type == Expr.Literal.LiteralType.LIST) {
            List<Double> list = new ArrayList<>();
            for (Expr expression : (List<Expr>)expr.value) {
                list.add((double)evaluate(expression));
            }

            return new TiList(list);
        } else if (expr.type == Expr.Literal.LiteralType.MATRIX) {
            // TODO: throw error if rows have different lengths
            List<List<Double>> list = new ArrayList<>();
            for (List<Expr> row : (List<List<Expr>>)expr.value) {
                List<Double> temp = new ArrayList<>();
                for (Expr expression : row) {
                    temp.add((double)evaluate(expression));
                }
                list.add(temp);
            }
            return new TiMatrix(list);
        }

        return expr.value;
    }

    @Override
    public Object visitUnaryExpr(Expr.Unary expr) {
        Object right = evaluate(expr.right);

        switch (expr.operator.type) {
            case PLUS:
                return right;
            case MINUS:
                return -(double)right;
        }

        return null;
    }

    @Override
    public Object visitVariableExpr(Expr.Variable expr) {
        if (expr.listIndex != null) {
            // a list index is specified
            return environment.getListIndex(expr.name, evaluate(expr.listIndex));
        } else if(expr.matrixIndex != null) {
            // a matrix index is specified
            return environment.getMatrixIndex(expr.name, evaluate(expr.matrixIndex.first), evaluate(expr.matrixIndex.second));
        } else {
            return environment.get(expr.name);
        }
    }

    @Override
    public Object visitLogicalExpr(Expr.Logical expr) {
        Object left = evaluate(expr.left);
        Object right = evaluate(expr.right);

        if (!(left instanceof Double && right instanceof Double)) {
            throw new RuntimeError(expr.operator, "Expect numbers in logical operation");
        }

        double leftValue = (double)left;
        double rightValue = (double)right;

        if (expr.operator.type == AND) {
            return (leftValue != FALSE && rightValue != FALSE) ? TRUE : FALSE;
        } else if (expr.operator.type == OR) {
            return (leftValue != FALSE || rightValue != FALSE) ? TRUE : FALSE;
        }

        throw new RuntimeError(expr.operator, "Unexpected logical operator.");
    }

    @Override
    public Void visitExpressionStmt(Stmt.Expression stmt) {
        evaluate(stmt.expression);
        return null;
    }

    @Override
    public Void visitDispStmt(Stmt.Disp stmt) {
        for (Expr expression : stmt.expressions) {
            System.out.println(evaluate(expression).toString());
        }
        return null;
    }

    @Override
    public Void visitAssignStmt(Stmt.Assign stmt) {
        Object value = evaluate(stmt.expression);
        // an index was specified
        if (stmt.listIndex != null) {
            environment.assignListIndex(stmt.name, value, evaluate(stmt.listIndex));
        } else if (stmt.matrixIndex != null) {
            environment.assignMatrixIndex(stmt.name, value, evaluate(stmt.matrixIndex.first), evaluate(stmt.matrixIndex.second));
        } else {
            environment.assign(stmt.name, value);
        }
        return null;
    }

    @Override
    public Void visitPromptStmt(Stmt.Prompt stmt) {
        for (Token name : stmt.names) {
            handleUserInput(name.lexeme + "?", name);
        }

        return null;
    }

    @Override
    public Void visitInputStmt(Stmt.Input stmt) {
        handleUserInput(stmt.prompt, stmt.name);
        return null;
    }

    @Override
    public Void visitIfStmt(Stmt.If stmt) {
        Object value = evaluate(stmt.condition);
        // TODO: throw error if this is not a double, need a token to throw with it, so maybe pass the if token into Stmt.If
        double doubleValue = (double)value;
        if (isTrue(doubleValue)) {
            executeAllStatements(stmt.thenHead);
        } else {
            executeAllStatements(stmt.elseHead);
        }

        return null;
    }

    @Override
    public Void visitWhileStmt(Stmt.While stmt) {
        // TODO: throw error if this is not a double
        double conditionValue = (double)evaluate(stmt.condition);

        while (isTrue(conditionValue)) {
            executeAllStatements(stmt.head);
            conditionValue = (double)evaluate(stmt.condition);
        }

        return null;
    }

    @Override
    public Void visitForStmt(Stmt.For stmt) {
        /*
            Odd behavior of for loop: both the end and step variables are determined before
            start is assigned to the loop variable
            They are only determined once, before the loop starts
         */
        double end = (double)evaluate(stmt.end);
        double step = (double)evaluate(stmt.step);
        // assign starting value to the loop variable
        environment.assign(stmt.name, evaluate(stmt.start));

        if (step > 0) {
            for (double i = (double)environment.get(stmt.name); i <= end; i += step) {
                environment.assign(stmt.name, i);
                executeAllStatements(stmt.head);
            }
        } else if (step < 0) {
            for (double i = (double)environment.get(stmt.name); i >= end; i += step) {
                environment.assign(stmt.name, i);
                executeAllStatements(stmt.head);
            }
        } else {
            // TODO: figure out how to throw runtime exceptions for statements?
            // need a token to throw?
            //throw new RuntimeError()
        }

        return null;
    }

    @Override
    public Void visitLabelStmt(Stmt.Label stmt) {
        // no behavior needed; marking is handled in pre-interpreter pass
        return null;
    }

    @Override
    public Void visitGotoStmt(Stmt.Goto stmt) {
        throw new GotoException(stmt.label);
    }

    @Override
    public Void visitRepeatStmt(Stmt.Repeat stmt) {
        do {
            executeAllStatements(stmt.head);
            // execute code until condition is true
        } while ((double)evaluate(stmt.condition) == FALSE);

        return null;
    }

    @Override
    public Void visitReturnStmt(Stmt.Return stmt) {
        throw new ReturnException();
    }

    @Override
    public Void visitStopStmt(Stmt.Stop stmt) {
        throw new StopException();
    }

    @Override
    public Void visitMenuStmt(Stmt.Menu stmt) {
        int input;
        do {
            printMenu(stmt);
            System.out.print("Enter a number: ");
            input = userInput.nextInt();
        } while(input <= 0 || input > stmt.options.size());

        throw new GotoException(stmt.labels.get(input - 1));
    }

    private void printMenu(Stmt.Menu stmt) {
        System.out.println(stmt.title);
        for (int i = 0; i < stmt.options.size(); i++) {
            System.out.println((i + 1) + ":" + stmt.options.get(i));
        }
    }

    private void handleUserInput(String prompt, Token name) {
        String input;
        do {
            System.out.print(prompt);
            input = userInput.nextLine();
        } while (input.length() == 0);

        Expr expression = new Parser(new Lexer(input).lexTokens()).expression();
        environment.assign(name, evaluate(expression));
    }

    private boolean isTrue(double value) {
        return value != FALSE;
    }

    /**
     * Calls the accept method passing this as the parameter, so that the accept method can call
     * the proper method back here to visit the correct type of expression
     * @param expr The expr to evaluate
     * @return The evaluated value of the expression
     */
    private Object evaluate(Expr expr) {
        return expr.accept(this);
    }

    private void executeAllStatements(Stmt stmt) {
        Stmt curStmt = stmt;
        while (curStmt != null) {
            curStmt.accept(this);
            curStmt = curStmt.next();
        }
    }

    private void executeSingleStatement(Stmt stmt) {
        stmt.accept(this);
    }
}
