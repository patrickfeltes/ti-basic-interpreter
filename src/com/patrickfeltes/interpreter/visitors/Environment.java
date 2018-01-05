package com.patrickfeltes.interpreter.visitors;

import com.patrickfeltes.interpreter.data_types.TiList;
import com.patrickfeltes.interpreter.data_types.TiMatrix;
import com.patrickfeltes.interpreter.errors.RuntimeError;
import com.patrickfeltes.interpreter.tokens.Token;

import java.util.*;

public class Environment {

    private final Map<String, Object> values = new HashMap<>();
    private final Set<String> numberVariables = new HashSet<>();
    private final Set<String> listVariables = new HashSet<>();
    private final Set<String> matrixVariables = new HashSet<>();
    private final Set<String> stringNames = new HashSet<>();

    public Environment() {
        defineVariables();
    }

    public void defineVariables() {
        // letter variables
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        for (int i = 0; i < alphabet.length(); i++) {
            numberVariables.add("" + alphabet.charAt(i));
            values.put("" + alphabet.charAt(i), 0.0);
        }

        // matrices
        String matrixNames = "ABCDEFGHIJ";
        for (int i = 0; i < matrixNames.length(); i++) {
            String name = "[" + matrixNames.charAt(i) + "]";
            matrixVariables.add(name);
            values.put(name, null);
        }

        // lists
        listVariables.add("L₁");
        listVariables.add("L₂");
        listVariables.add("L₃");
        listVariables.add("L₄");
        listVariables.add("L₅");
        listVariables.add("L₆");
        for (String string : listVariables) {
            values.put(string, null);
        }

        // strings
        for (int i = 0; i < 10; i++) {
            String name = "Str" + i;
            stringNames.add(name);
            values.put(name, null);
        }
    }

    public Object get(Token name) {
        if (values.containsKey(name.lexeme)) return values.get(name.lexeme);

        throw new RuntimeError(name, "Undefined variable '" + name.lexeme + "'.");
    }

    public Object getListIndex(Token name, Object index) {
        if (!(index instanceof Double)) throw new RuntimeError(name, "Index must be a number.");
        double indexNumber = (double)index;
        TiList list = ((TiList) values.get(name.lexeme));
        if (indexNumber <= 0 || indexNumber > list.size()) {
            throw new RuntimeError(name, "Index is out of range for this list.");
        }

        return list.get(indexNumber);
    }

    public Object getMatrixIndex(Token name, Object row, Object col) {
        if (!(row instanceof Double)) throw new RuntimeError(name, "Col must be a number.");
        if (!(col instanceof Double)) throw new RuntimeError(name, "Row must be a number.");

        double rowNumber = (double)row;
        double colNumber = (double)col;
        TiMatrix matrix = ((TiMatrix)values.get(name.lexeme));

        if (rowNumber <= 0 || colNumber <= 0 || rowNumber > matrix.getRows() || colNumber > matrix.getCols()) {
            throw new RuntimeError(name, "Row and/or column is out of range for this matrix.");
        }

        return matrix.get(rowNumber, colNumber);
    }

    public void assign(Token name, Object value) {
        if (numberVariables.contains(name.lexeme)) {
            if (!(value instanceof Double)) throw new RuntimeError(name, "Cannot assign a non-number to a number variable.");
            values.put(name.lexeme, value);
            return;
        }

        if (listVariables.contains(name.lexeme)) {
            if (!(value instanceof TiList)) throw new RuntimeError(name, "Cannot assign a non-list to a list variable.");
            // need to copy to avoid same reference across lists
            values.put(name.lexeme, new TiList((TiList)value));
            return;
        }

        if (matrixVariables.contains(name.lexeme)) {
            if (!(value instanceof TiMatrix)) throw new RuntimeError(name, "Cannot assign a non-matrix to a matrix variable.");
            // need to copy to avoid same reference across matrices
            values.put(name.lexeme, new TiMatrix((TiMatrix)value));
            return;
        }

        if (stringNames.contains(name.lexeme)) {
            if (!(value instanceof String)) throw new RuntimeError(name, "Cannot assign a non-string to a string variable.");
            values.put(name.lexeme, value.toString());
            return;
        }

        throw new RuntimeError(name, "Invalid type.");
    }

    public void assignListIndex(Token name, Object value, Object index) {
        if (!(value instanceof Double)) throw new RuntimeError(name, "Cannot assign a non-number to a list element.");
        if (!(index instanceof Double)) throw new RuntimeError(name, "Index must be a number.");

        double indexNumber = (double)index;
        TiList list = ((TiList) values.get(name.lexeme));
        if (indexNumber <= 0 || indexNumber > list.size() + 1) {
            throw new RuntimeError(name, "Index is out of range for this list.");
        }

        if (indexNumber == list.size() + 1) {
            list.add((double)value);
        } else {
            list.setIndex(indexNumber, (double)value);
        }
    }

    public void assignMatrixIndex(Token name, Object value, Object row, Object col) {
        if (!(value instanceof Double)) throw new RuntimeError(name, "Cannot assign a non-number to a matrix element.");
        if (!(row instanceof Double)) throw new RuntimeError(name, "Row must be a number.");
        if (!(col instanceof Double)) throw new RuntimeError(name, "Col must be a number.");

        double rowNumber = (double)row;
        double colNumber = (double)col;
        TiMatrix matrix = ((TiMatrix) values.get(name.lexeme));

        if (rowNumber <= 0 || colNumber <= 0 || rowNumber > matrix.getRows() || colNumber > matrix.getCols()) {
            throw new RuntimeError(name, "Row and/or column is out of range for this matrix.");
        }

        matrix.setIndex(rowNumber, colNumber, (double)value);
    }

}
