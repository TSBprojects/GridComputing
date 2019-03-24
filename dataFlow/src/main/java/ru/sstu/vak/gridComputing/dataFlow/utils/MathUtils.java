package ru.sstu.vak.gridComputing.dataFlow.utils;

import ru.sstu.vak.gridComputing.dataFlow.exception.AdjacencyMatrixSizeException;

import java.math.BigInteger;
import java.util.concurrent.ThreadLocalRandom;

import static java.math.BigInteger.*;

public class MathUtils {

    private MathUtils() {

    }

    public static BigInteger inverseFactorial(int n, int depth) {
        int count = 0;
        BigInteger result = ONE;
        for (BigInteger i = valueOf(n); i.compareTo(ZERO) > 0; i = i.subtract(ONE)) {
            if (depth == count) {
                break;
            }
            count++;
            result = result.multiply(i);
        }
        return result;
    }

    public static int[][] genAdjMatrix(int size, int origin, int bound) {
        if (size < 2) {
            throw new AdjacencyMatrixSizeException();
        }

        int[][] adjMatrix = new int[size][size];

        for (int i = 0; i < adjMatrix.length; i++) {
            for (int j = i; j < adjMatrix[i].length; j++) {
                if (i != j) {
                    int n = ThreadLocalRandom.current().nextInt(origin, bound);
                    adjMatrix[i][j] = n;
                    adjMatrix[j][i] = n;
                } else {
                    adjMatrix[i][j] = 0;
                }
            }
        }

        return adjMatrix;
    }

    public static int[][] genAdjMatrix(int size) {
        if (size < 2) {
            throw new AdjacencyMatrixSizeException();
        }

        int[][] adjMatrix = new int[size][size];

        for (int i = 0; i < adjMatrix.length; i++) {
            for (int j = i; j < adjMatrix[i].length; j++) {
                if (i != j) {
                    adjMatrix[i][j] = 0;
                    adjMatrix[j][i] = 0;
                } else {
                    adjMatrix[i][j] = 0;
                }
            }
        }

        return adjMatrix;
    }

}
