package com.patrickfeltes.interpreter.exceptions;

/**
 * A goto exception is used to come out the deep recursive call when traversing the AST
 * This allows us to jump to a new node of the AST more easily, specifically in a catch block
 * for the GotoException.
 */
public class GotoException extends RuntimeException {

    public final String label;

    public GotoException(String label) {
        this.label = label;
    }

}
