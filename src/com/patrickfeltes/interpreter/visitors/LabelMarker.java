package com.patrickfeltes.interpreter.visitors;

import com.patrickfeltes.interpreter.ast.Expr;
import com.patrickfeltes.interpreter.ast.Stmt;

import java.util.HashMap;
import java.util.Map;

/**
 * Label marker is a class that walks an AST to find where all Lbl commands are located
 */
public class LabelMarker implements Expr.Visitor<Void>, Stmt.Visitor<Void> {

    private Map<String, Stmt> labels = new HashMap<>();

    public Map<String, Stmt> getLabels(Stmt head) {
        markAll(head);
        return labels;
    }

    @Override
    public Void visitBinaryExpr(Expr.Binary expr) {
        return null;
    }

    @Override
    public Void visitGroupingExpr(Expr.Grouping expr) {
        return null;
    }

    @Override
    public Void visitLiteralExpr(Expr.Literal expr) {
        return null;
    }

    @Override
    public Void visitUnaryExpr(Expr.Unary expr) {
        return null;
    }

    @Override
    public Void visitVariableExpr(Expr.Variable expr) {
        return null;
    }

    @Override
    public Void visitLogicalExpr(Expr.Logical expr) {
        return null;
    }

    @Override
    public Void visitExpressionStmt(Stmt.Expression stmt) {
        return null;
    }

    @Override
    public Void visitDispStmt(Stmt.Disp stmt) {
        return null;
    }

    @Override
    public Void visitAssignStmt(Stmt.Assign stmt) {
        return null;
    }

    @Override
    public Void visitPromptStmt(Stmt.Prompt stmt) {
        return null;
    }

    @Override
    public Void visitInputStmt(Stmt.Input stmt) {
        return null;
    }

    @Override
    public Void visitIfStmt(Stmt.If stmt) {
        markAll(stmt.thenHead);
        markAll(stmt.elseHead);
        return null;
    }

    @Override
    public Void visitWhileStmt(Stmt.While stmt) {
        markAll(stmt.head);
        return null;
    }

    @Override
    public Void visitForStmt(Stmt.For stmt) {
        markAll(stmt.head);
        return null;
    }

    @Override
    public Void visitLabelStmt(Stmt.Label stmt) {
        if (!labels.containsKey(stmt.label)) {
            labels.put(stmt.label, stmt);
        }

        return null;
    }

    @Override
    public Void visitGotoStmt(Stmt.Goto stmt) {
        return null;
    }

    @Override
    public Void visitRepeatStmt(Stmt.Repeat stmt) {
        markAll(stmt.head);
        return null;
    }

    @Override
    public Void visitReturnStmt(Stmt.Return stmt) {
        return null;
    }

    @Override
    public Void visitStopStmt(Stmt.Stop stmt) {
        return null;
    }

    @Override
    public Void visitMenuStmt(Stmt.Menu stmt) {
        return null;
    }

    private void markSingle(Stmt stmt) {
        stmt.accept(this);
    }

    private void markAll(Stmt stmt) {
        Stmt statement = stmt;
        while (statement != null) {
            markSingle(statement);
            statement = statement.next();
        }
    }
}
