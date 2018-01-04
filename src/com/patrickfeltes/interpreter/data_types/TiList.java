package com.patrickfeltes.interpreter.data_types;

import java.util.Collections;
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
    public double get(int index) {
        return this.list.get(index - 1);
    }

    public void set(List<Double> list) {
        this.list = list;
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
