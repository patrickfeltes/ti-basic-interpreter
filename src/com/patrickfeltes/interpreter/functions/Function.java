package com.patrickfeltes.interpreter.functions;

import com.patrickfeltes.interpreter.visitors.Interpreter;

import java.util.List;

public abstract class Function {

    protected abstract boolean hasCorrectArguments(List<Object> arguments);

    // this is what should be called when calling a function
    public final Object call(Interpreter interpreter, List<Object> arguments) {
        // TODO: throw runtime exception with first paren as token
        if (!hasCorrectArguments(arguments)) return null;

        return functionImplementation(interpreter, arguments);
    }

    // this is where the subclass function implementation will go
    protected Object functionImplementation(Interpreter interpreter, List<Object> arguments) {
        return null;
    }

}
