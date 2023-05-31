package com.test.opengl4android;


public class MatrixUtil {

    public static double[] calXY(double[] vector, double thetaX, double thetaY) {
        if (thetaX != 0) {
            double[][] rotationMatrix = getRotationMatrixX(thetaX);
            vector = multiplyMatrix(rotationMatrix, vector);
        }
        if (thetaY != 0) {
            double[][] rotationMatrix = getRotationMatrixY(thetaY);
            vector = multiplyMatrix(rotationMatrix, vector);
        }
        return vector;
    }

    private static double[][] getRotationMatrixX(double theta) {
        double thetaRad = Math.toRadians(theta);
        // 以立体圆心为原点， x轴的旋转矩阵
        double[][] rotationMatrix = {
                {1, 0, 0},
                {0, Math.cos(thetaRad), -Math.sin(thetaRad)},
                {0, Math.sin(thetaRad), Math.cos(thetaRad)}
        };
        return rotationMatrix;
    }

    private static double[][] getRotationMatrixY(double theta) {
        double thetaRad = Math.toRadians(theta);
        //以立体圆心为原点， y轴的旋转矩阵
        double[][] rotationMatrix = {
                {Math.cos(thetaRad), 0, Math.sin(thetaRad)},
                {0, 1, 0},
                {-Math.sin(thetaRad), 0, Math.cos(thetaRad)}
        };
        return rotationMatrix;
    }

    // 矩阵乘法
    public static double[] multiplyMatrix(double[][] matrix, double[] vector) {
        double[] result = new double[matrix.length];
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < vector.length; j++) {
                result[i] += matrix[i][j] * vector[j];
            }
        }
        return result;
    }
}
