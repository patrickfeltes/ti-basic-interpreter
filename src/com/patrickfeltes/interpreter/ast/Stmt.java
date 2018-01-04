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
        R visitIfStmt(If stmt);
        R visitWhileStmt(While stmt);
        R visitForStmt(For stmt);
        R visitLabelStmt(Label stmt);
        R visitGotoStmt(Goto stmt);
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

    public static class If extends Stmt {
        public final Expr condition;
        public final List<Stmt> thenBranch;
        public final List<Stmt> elseBranch;

        public If(Expr condition, List<Stmt> thenBranch, List<Stmt> elseBranch) {
            this.condition = condition;
            this.thenBranch = thenBranch;
            this.elseBranch = elseBranch;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitIfStmt(this);
        }
    }

    public static class While extends Stmt {
        public final Expr condition;
        public final List<Stmt> statements;

        public While(Expr condition, List<Stmt> statements) {
            this.condition = condition;
            this.statements = statements;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitWhileStmt(this);
        }
    }

    public static class For extends Stmt {
        public final Token name;
        public final Expr start;
        public final Expr end;
        public final Expr step;
        public final List<Stmt> statements;

        public For(Token name, Expr start, Expr end, Expr step, List<Stmt> statements) {
            this.name = name;
            this.start = start;
            this.end = end;
            this.step = step;
            this.statements = statements;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitForStmt(this);
        }
    }

    public static class Label extends Stmt {
        public final String label;

        public Label(String label) {
            this.label = label;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitLabelStmt(this);
        }
    }

    public static class Goto extends Stmt {
        public final String label;

        public Goto(String label) {
            this.label = label;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitGotoStmt(this);
        }
    }

}
