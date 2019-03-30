package ru.sstu.vak.gridComputing.computeNode.core;

import java.math.BigInteger;

import static java.math.BigInteger.ONE;
import static java.math.BigInteger.valueOf;
import static ru.sstu.vak.gridComputing.dataFlow.utils.MathUtils.inverseFactorial;

/**
 * Позволяет получить перестановку по его порядковому номеру.
 */
public class NthPermutation {

    private int routeLength;
    private BigInteger[] factorials;

    /**
     * @param nodesCount количество всех узлов
     * @param routeLength длина маршрута
     */
    public NthPermutation(int nodesCount, int routeLength) {
        this.routeLength = routeLength;
        this.factorials =  createFactorialsArray(nodesCount, routeLength);
    }

    /**
     * @param routeIndex номер маршрута
     * @return перестановка по номеру {@code routeIndex} длиной {@code routeLength}
     */
    public int[] getPermutation(BigInteger routeIndex) {

        int[] permutation = new int[routeLength];

        for (int i = 0; i < routeLength; i++) {
            BigInteger part = factorials[i];
            permutation[i] = routeIndex.divide(part).intValue();
            routeIndex = routeIndex.mod(part);
        }

        for (int i = routeLength - 1; i > 0; i--) {
            for (int j = i - 1; j >= 0; j--) {
                if (permutation[j] <= permutation[i]) {
                    permutation[i]++;
                }
            }
        }

        return permutation;
    }

    /**
     * @param nodesCount количество всех узлов
     * @param routeIndex номер маршрута
     * @return перестановка по номеру {@code routeIndex} длиной {@code routeLength}
     * из всех узлов {@code nodesCount}
     * @deprecated вызов метода {@link NthPermutation#createFactorialsArray(int, int)}
     * каждую итерацию сильно ухудшает производительность.
     * Лучше использовать {@link NthPermutation#getPermutation(BigInteger)}
     */
    @Deprecated
    public int[] getPermutation(int nodesCount, BigInteger routeIndex) {

        int[] permutation = new int[routeLength];
        BigInteger[] factorials = createFactorialsArray(nodesCount, routeLength);

        for (int i = 0; i < routeLength; i++) {
            BigInteger part = factorials[i];
            permutation[i] = routeIndex.divide(part).intValue();
            routeIndex = routeIndex.mod(part);
        }

        for (int i = routeLength - 1; i > 0; i--) {
            for (int j = i - 1; j >= 0; j--) {
                if (permutation[j] <= permutation[i]) {
                    permutation[i]++;
                }
            }
        }

        return permutation;
    }

    private BigInteger[] createFactorialsArray(int nodesCount, int routeLength) {
        BigInteger[] factorials = new BigInteger[routeLength];

        int length = routeLength;
        for (int i = 0; i < length - 1; i++) {
            factorials[i] = inverseFactorial(nodesCount, routeLength).divide(valueOf(nodesCount));
            nodesCount--;
            routeLength--;
        }
        factorials[length - 1] = ONE;

        return factorials;
    }
}
