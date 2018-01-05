package com.patrickfeltes.interpreter.data_types;

import com.patrickfeltes.interpreter.errors.RuntimeError;
import com.patrickfeltes.interpreter.tokens.Token;

import java.util.List;

public class TiList {

    private List<Double> list;

    public TiList() {
        this.list = null;
    }

    public TiList(List<Double> list) {
        this.list = list;
    }

    // ti lists are one-indexed
    public double get(double index) {
        return this.list.get((int)index - 1);
    }

    public void setIndex(double index, double value) {
        this.list.set((int)index - 1, value);
    }

    public void add(double value) {
        this.list.add(value);
    }

    public int size() {
        return this.list.size();
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("{");

        for (double item : list) {
            builder.append(item).append(",");
        }

        builder.deleteCharAt(builder.length() - 1);
        builder.append("}");
        return builder.toString();
    }

}
