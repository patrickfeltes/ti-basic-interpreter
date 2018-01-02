package com.patrickfeltes.interpreter.ast;

import com.patrickfeltes.interpreter.tokens.Token;

import java.util.List;

public abstract class Stmt {

    public abstract <R> R accept(Visitor<R> visitor);

    public interface Visitor<R> {
        R visitExpressionStmt(Expression stmt);
        R visitDispStmt(Disp stmt);
        R visitAssignStmt(Assign stmt);
        R visitPromptStmt(Prompt stmt);
        R visitInputStmt(Input stmt);
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

    public static class Prompt extends Stmt {

        public final List<Token> names;

        public Prompt(List<Token> names) {
            this.names = names;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitPromptStmt(this);
        }
    }

    public static class Input extends Stmt {

        public final String prompt;
        public final Token name;

        // this is currently undefined: should ask the user to select coordinates on the graph screen
        public Input() {
            this.prompt = "?";
            this.name = null;
        }

        public Input(Token name) {
            this.prompt = "?";
            this.name = name;
        }

        public Input(String prompt, Token name) {
            this.prompt = prompt;
            this.name = name;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitInputStmt(this);
        }
    }


}
