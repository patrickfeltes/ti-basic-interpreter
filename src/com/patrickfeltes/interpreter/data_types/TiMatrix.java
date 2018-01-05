package com.patrickfeltes.interpreter.data_types;

import com.patrickfeltes.interpreter.errors.RuntimeError;
import com.patrickfeltes.interpreter.tokens.Token;

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

    public TiMatrix(double[][] matrix) {
        this.matrix = matrix;
        this.rows = matrix.length;
        this.cols = matrix[0].length;
    }

    public TiMatrix(TiMatrix matrix) {
        this.matrix = new double[matrix.rows][matrix.cols];
        this.rows = matrix.rows;
        this.cols = matrix.cols;

        for (int r = 0; r < matrix.rows; r++) {
            for (int c = 0; c < matrix.cols; c++) {
                this.matrix[r][c] = matrix.matrix[r][c];
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

    public static TiMatrix add(TiMatrix matrix1, TiMatrix matrix2, Token operator) {
        if (matrix1.rows != matrix2.rows || matrix1.cols != matrix2.cols) {
            throw new RuntimeError(operator, "The dimensions of these matrices are not the same.");
        }

        double[][] newMatrix = new double[matrix1.rows][matrix1.cols];

        for (int r = 0; r < matrix1.rows; r++) {
            for (int c = 0; c < matrix1.cols; c++) {
                newMatrix[r][c] = matrix1.matrix[r][c] + matrix2.matrix[r][c];
            }
        }

        return new TiMatrix(newMatrix);
    }

    public static TiMatrix sub(TiMatrix matrix1, TiMatrix matrix2, Token operator) {
        if (matrix1.rows != matrix2.rows || matrix1.cols != matrix2.cols) {
            throw new RuntimeError(operator, "The dimensions of these matrices are not the same.");
        }

        double[][] newMatrix = new double[matrix1.rows][matrix1.cols];

        for (int r = 0; r < matrix1.rows; r++) {
            for (int c = 0; c < matrix1.cols; c++) {
                newMatrix[r][c] = matrix1.matrix[r][c] - matrix2.matrix[r][c];
            }
        }

        return new TiMatrix(newMatrix);
    }

    public static TiMatrix mul(TiMatrix matrix1, TiMatrix matrix2, Token operator) {
        if (matrix1.cols != matrix2.rows) {
            throw new RuntimeError(operator, "The dimensions of these matrices are not the same.");
        }

        double[][] newMatrix = new double[matrix1.rows][matrix2.cols];

        for (int i = 0; i < matrix1.rows; i++) {
            for (int j = 0; j < matrix2.cols; j++) {
                double sum = 0;
                for (int k = 0; k < matrix1.cols; k++) {
                    sum += matrix1.matrix[i][k] * matrix2.matrix[k][j];
                }
                newMatrix[i][j] = sum;
            }
        }

        return new TiMatrix(newMatrix);
    }

    public static TiMatrix scale(TiMatrix matrix, double factor) {
        double[][] newMatrix = new double[matrix.rows][matrix.cols];
        for (int r = 0; r < matrix.rows; r++) {
            for (int c = 0; c < matrix.cols; c++) {
                newMatrix[r][c] = factor * matrix.matrix[r][c];
            }
        }
        return new TiMatrix(newMatrix);
    }

}
