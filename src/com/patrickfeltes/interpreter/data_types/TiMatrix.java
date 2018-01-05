package com.patrickfeltes.interpreter.data_types;

import java.util.List;

public class TiMatrix {

    private double[][] matrix;
    private int rows;
    private int cols;

    public TiMatrix(List<List<Double>> entries) {
        if (entries.size() == 0) {
            return;
        }

        this.rows = entries.size();
        this.cols = entries.get(0).size();
        matrix = new double[rows][cols];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                matrix[r][c] = entries.get(r).get(c);
            }
        }
    }

    public double get(double r, double c) {
        return matrix[(int)r - 1][(int)c - 1];
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        for (int r = 0; r < rows; r++) {
            builder.append("[");
            for (int c = 0; c < cols; c++) {
                builder.append(matrix[r][c]).append(" ");
            }
            builder.deleteCharAt(builder.length() - 1).append("]\n");
        }
        builder.deleteCharAt(builder.length() - 1).append("]");
        return builder.toString();
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public void setIndex(double row, double col, double value) {
        this.matrix[(int)row - 1][(int)col - 1] = value;
    }
}
