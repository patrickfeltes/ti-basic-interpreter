package com.patrickfeltes.interpreter.data_types;

import com.patrickfeltes.interpreter.errors.RuntimeError;
import com.patrickfeltes.interpreter.tokens.Token;

import java.util.ArrayList;
import java.util.List;

public class TiList {

    private List<Double> list;

    public TiList() {
        this.list = null;
    }

    public TiList(List<Double> list) {
        this.list = new ArrayList<>();
        // to avoid reference issues if storing one list into another
        for (Double item : list) {
            this.list.add(item.doubleValue());
        }
    }

    public TiList(TiList list) {
        this.list = new ArrayList<>();
        // to avoid reference issues if storing one list into another
        for (Double item : list.list) {
            this.list.add(item.doubleValue());
        }
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

    public static TiList add(TiList list1, TiList list2, Token operator) {
        if (list1.list.size() != list2.list.size()) {
            throw new RuntimeError(operator, "The dimensions of these lists are not the same.");
        }

        List<Double> newList = new ArrayList<>();

        for (int i = 0; i < list1.list.size(); i++) {
            newList.add(list1.list.get(i) + list2.list.get(i));
        }

        return new TiList(newList);
    }

    public static TiList sub(TiList list1, TiList list2, Token operator) {
        if (list1.list.size() != list2.list.size()) {
            throw new RuntimeError(operator, "The dimensions of these lists are not the same.");
        }

        List<Double> newList = new ArrayList<>();

        for (int i = 0; i < list1.list.size(); i++) {
            newList.add(list1.list.get(i) - list2.list.get(i));
        }

        return new TiList(newList);
    }

    public static TiList mul(TiList list1, TiList list2, Token operator) {
        if (list1.list.size() != list2.list.size()) {
            throw new RuntimeError(operator, "The dimensions of these lists are not the same.");
        }

        List<Double> newList = new ArrayList<>();

        for (int i = 0; i < list1.list.size(); i++) {
            newList.add(list1.list.get(i) * list2.list.get(i));
        }

        return new TiList(newList);
    }

    public static TiList div(TiList list1, TiList list2, Token operator) {
        if (list1.list.size() != list2.list.size()) {
            throw new RuntimeError(operator, "The dimensions of these lists are not the same.");
        }

        List<Double> newList = new ArrayList<>();

        for (int i = 0; i < list1.list.size(); i++) {
            newList.add(list1.list.get(i) / list2.list.get(i));
        }

        return new TiList(newList);
    }

    public static TiList pow(TiList list1, TiList list2, Token operator) {
        if (list1.list.size() != list2.list.size()) {
            throw new RuntimeError(operator, "The dimensions of these lists are not the same.");
        }

        List<Double> newList = new ArrayList<>();

        for (int i = 0; i < list1.list.size(); i++) {
            newList.add(Math.pow(list1.list.get(i), list2.list.get(i)));
        }

        return new TiList(newList);
    }

    public static TiList scale(TiList list, double factor) {
        List<Double> newList = new ArrayList<>();

        for (int i = 0; i < list.list.size(); i++) {
            newList.add(factor * list.list.get(i));
        }

        return new TiList(newList);
    }

    public static TiList addScalar(TiList list, double number) {
        List<Double> newList = new ArrayList<>();

        for (int i = 0; i < list.list.size(); i++) {
            newList.add(number + list.list.get(i));
        }

        return new TiList(newList);
    }

    public static TiList subScalar(TiList list, double number) {
        List<Double> newList = new ArrayList<>();

        for (int i = 0; i < list.list.size(); i++) {
            newList.add(list.list.get(i) - number);
        }

        return new TiList(newList);
    }

    public static TiList subList(TiList list, double number) {
        List<Double> newList = new ArrayList<>();

        for (int i = 0; i < list.list.size(); i++) {
            newList.add(number - list.list.get(i));
        }

        return new TiList(newList);
    }

    public static TiList divScalar(TiList list, double number) {
        List<Double> newList = new ArrayList<>();

        for (int i = 0; i < list.list.size(); i++) {
            newList.add(list.list.get(i) / number);
        }

        return new TiList(newList);
    }

    public static TiList divList(TiList list, double number) {
        List<Double> newList = new ArrayList<>();

        for (int i = 0; i < list.list.size(); i++) {
            newList.add(number / list.list.get(i));
        }

        return new TiList(newList);
    }

    public static TiList powScalar(TiList list, double number) {
        List<Double> newList = new ArrayList<>();

        for (int i = 0; i < list.list.size(); i++) {
            newList.add(Math.pow(list.list.get(i), number));
        }

        return new TiList(newList);
    }

    public static TiList powList(TiList list, double number) {
        List<Double> newList = new ArrayList<>();

        for (int i = 0; i < list.list.size(); i++) {
            newList.add(Math.pow(number, list.list.get(i)));
        }

        return new TiList(newList);
    }
}
