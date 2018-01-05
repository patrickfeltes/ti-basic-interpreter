package com.patrickfeltes.interpreter.ast;

import com.patrickfeltes.interpreter.tokens.Token;

import java.util.List;

public abstract class Stmt {

    private Stmt next;

    public abstract <R> R accept(Visitor<R> visitor);

    public void setNext(Stmt stmt) {
        this.next = stmt;
    }

    public Stmt next() {
        return next;
    }

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
        R visitRepeatStmt(Repeat stmt);
        R visitReturnStmt(Return stmt);
        R visitStopStmt(Stop stmt);
        R visitMenuStmt(Menu stmt);
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
        public final Expr index;

        public Assign(Expr expression, Token name, Expr index) {
            this.expression = expression;
            this.name = name;
            this.index = index;
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
        public final Stmt thenHead;
        public final Stmt elseHead;

        public If(Expr condition, Stmt thenHead, Stmt elseHead) {
            this.condition = condition;
            this.thenHead = thenHead;
            this.elseHead = elseHead;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitIfStmt(this);
        }
    }

    public static class While extends Stmt {
        public final Expr condition;
        public final Stmt head;

        public While(Expr condition, Stmt head) {
            this.condition = condition;
            this.head = head;
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
        public final Stmt head;

        public For(Token name, Expr start, Expr end, Expr step, Stmt head) {
            this.name = name;
            this.start = start;
            this.end = end;
            this.step = step;
            this.head = head;
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

    public static class Repeat extends Stmt {
        public final Expr condition;
        public final Stmt head;

        public Repeat(Expr condition, Stmt head) {
            this.condition = condition;
            this.head = head;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitRepeatStmt(this);
        }
    }

    public static class Return extends Stmt {
        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitReturnStmt(this);
        }
    }

    public static class Stop extends Stmt {
        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitStopStmt(this);
        }
    }

    public static class Menu extends Stmt {
        public final String title;
        public final List<String> options;
        public final List<String> labels;

        public Menu(String title, List<String> options, List<String> labels) {
            this.title = title;
            this.options = options;
            this.labels = labels;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitMenuStmt(this);
        }
    }
}
