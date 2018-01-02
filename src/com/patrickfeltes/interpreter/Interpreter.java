package com.patrickfeltes.interpreter;

import com.patrickfeltes.interpreter.ast.Expr;
import com.patrickfeltes.interpreter.ast.Stmt;
import com.patrickfeltes.interpreter.errors.RuntimeError;

import java.util.List;

public class Interpreter implements Expr.Visitor<Object>, Stmt.Visitor<Void> {

    private final Environment environment = new Environment();

    public void interpret(List<Stmt> statements) {
        for (Stmt stmt : statements) {
            try {
                execute(stmt);
            } catch (RuntimeError error) {
                Main.runtimeError(error);
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
        }

        return null;
    }

    @Override
    public Object visitGroupingExpr(Expr.Grouping expr) {
        return evaluate(expr.inside);
    }

    @Override
    public Object visitLiteralExpr(Expr.Literal expr) {
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
        return environment.get(expr.name);
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
        environment.assign(stmt.name, value);
        return null;
    }

    private void execute(Stmt stmt) {
        stmt.accept(this);
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
}
