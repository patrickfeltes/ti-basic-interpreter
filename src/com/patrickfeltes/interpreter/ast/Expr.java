package com.patrickfeltes.interpreter.ast;

import com.patrickfeltes.interpreter.tokens.Token;
import com.patrickfeltes.interpreter.util.Pair;

import java.util.List;

/**
 * Expr is a node of the syntax tree that evaluates to something.
 */
public abstract class Expr {

    /**
     * The accept method allows us to reroute back to the visit method defined in our visitor
     * @param visitor The class that calls this method
     * @param <R> The return type expected
     * @return The result of the visit method for the type of this expression defined in the visitor
     */
    public abstract <R> R accept(Visitor<R> visitor);

    /**
     * Visitor interface for the Visitor design pattern.
     * Allows us more flexibility in defining the operations that occur for these expression types
     * without having to add new methods to each expr.
     * @param <R> the return type expected from the visitor
     */
    public interface Visitor<R> {
        R visitBinaryExpr(Binary expr);
        R visitGroupingExpr(Grouping expr);
        R visitLiteralExpr(Literal expr);
        R visitUnaryExpr(Unary expr);
        R visitVariableExpr(Variable expr);
        R visitLogicalExpr(Logical expr);
        R visitCallExpr(Call expr);
    }

    /**
     * A class for binary operation expressions
     */
    public static class Binary extends Expr {
        public final Expr left;
        public final Token operator;
        public final Expr right;

        public Binary(Expr left, Token operator, Expr right) {
            this.left = left;
            this.operator = operator;
            this.right = right;
        }

        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitBinaryExpr(this);
        }
    }

    /**
     * A class to represent a grouping of an expression, such as with ( ), [ ], or { }
     */
    public static class Grouping extends Expr {
        public final Expr inside;

        public Grouping(Expr inside) {
            this.inside = inside;
        }

        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitGroupingExpr(this);
        }
    }

    /**
     * A class to represent a literal, basically holds value such as a number or string
     */
    public static class Literal extends Expr {
        public enum LiteralType {
            DOUBLE,
            STRING,
            LIST,
            MATRIX
        }

        public final Object value;
        public final LiteralType type;

        public Literal(Object value, LiteralType type) {
            this.value = value;
            this.type = type;
        }

        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitLiteralExpr(this);
        }
    }

    /**
     * A class to represent a unary expression (i.e. +4 or -3)
     */
    public static class Unary extends Expr {
        public final Token operator;
        public final Expr right;

        public Unary(Token operator, Expr right) {
            this.operator = operator;
            this.right = right;
        }

        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitUnaryExpr(this);
        }
    }

    /**
     * Class to represent a variable.
     */
    public static class Variable extends Expr {
        public final Token name;
        public final Expr listIndex;
        public final Pair<Expr, Expr> matrixIndex;

        public Variable(Token name, Expr listIndex, Pair<Expr, Expr> matrixIndex) {
            this.name = name;
            this.listIndex = listIndex;
            this.matrixIndex = matrixIndex;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitVariableExpr(this);
        }
    }

    /**
     * A class for logical operation expressions
     */
    public static class Logical extends Expr {
        public final Expr left;
        public final Token operator;
        public final Expr right;

        public Logical(Expr left, Token operator, Expr right) {
            this.left = left;
            this.operator = operator;
            this.right = right;
        }

        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitLogicalExpr(this);
        }
    }

    /**
     * A class for calls, specifically function calls
     */
    public static class Call extends Expr {
        public final Token callee;
        public final List<Expr> arguments;

        public Call(Token callee, List<Expr> arguments) {
            this.callee = callee;
            this.arguments = arguments;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitCallExpr(this);
        }
    }

}
