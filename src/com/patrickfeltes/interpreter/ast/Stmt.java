package com.patrickfeltes.interpreter.ast;

import com.patrickfeltes.interpreter.tokens.Token;

import java.util.List;

public abstract class Stmt {

    public abstract <R> R accept(Visitor<R> visitor);

    public interface Visitor<R> {
        R visitExpressionStmt(Expression stmt);
        R visitDispStmt(Disp stmt);
        R visitAssignStmt(Assign stmt);
    }

    public static class Expression extends Stmt {
        public final Expr expression;

        public Expression(Expr expression) {
            this.expression = expression;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitExpressionStmt(this);
        }
    }

    public static class Disp extends Stmt {
        public final List<Expr> expressions;

        public Disp(List<Expr> expressions) {
            this.expressions = expressions;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitDispStmt(this);
        }
    }

    public static class Assign extends Stmt {
        public final Expr expression;
        public final Token name;

        public Assign(Expr expression, Token name) {
            this.expression = expression;
            this.name = name;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitAssignStmt(this);
        }
    }


}
