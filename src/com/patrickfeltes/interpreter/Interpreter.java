package com.patrickfeltes.interpreter;

import com.patrickfeltes.interpreter.ast.Expr;
import com.patrickfeltes.interpreter.ast.Stmt;

import java.util.List;

public class Interpreter implements Expr.Visitor<Object>, Stmt.Visitor<Object> {

    public void interpret(List<Stmt> statements) {
        for (Stmt stmt : statements) {
            execute(stmt);
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

    @Override
    public Object visitExpressionStmt(Stmt.Expression stmt) {
        evaluate(stmt.expression);
        return null;
    }

    @Override
    public Object visitDispStmt(Stmt.Disp stmt) {
        Object value = evaluate(stmt.expression);
        System.out.println(value.toString());
        return null;
    }
}
