package ru.sstu.vak.gridComputing.computeNode.core;

import ru.sstu.vak.gridComputing.computeNode.exception.NegativeEdgeWeightException;
import ru.sstu.vak.gridComputing.dataFlow.entity.Route;
import ru.sstu.vak.gridComputing.dataFlow.entity.RouteData;
import ru.sstu.vak.gridComputing.dataFlow.entity.RouteTask;
import ru.sstu.vak.gridComputing.dataFlow.entity.TaskResult;
import ru.sstu.vak.gridComputing.dataFlow.exception.RouteLengthException;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Path;
import java.util.function.Consumer;

import static java.math.BigInteger.ONE;
import static java.math.BigInteger.valueOf;
import static ru.sstu.vak.gridComputing.dataFlow.core.DataManager.readDataFile;
import static ru.sstu.vak.gridComputing.dataFlow.core.DataManager.readTaskFile;
import static ru.sstu.vak.gridComputing.dataFlow.utils.MathUtils.inverseFactorial;

public class RouteTaskExecutorBase implements RouteTaskExecutor {

    private int routeLength;
    private int[][] adjMatrix;
    private BigInteger[] factorials;

    private Consumer<Route> existRoutesListener;
    private Consumer<Route> allRoutesListener;


    public RouteTaskExecutorBase(Path dataFilePath) throws IOException {
        RouteData routeData = readDataFile(dataFilePath);
        this.adjMatrix = routeData.getAdjMatrix();
        this.routeLength = routeData.getRouteLength();
        this.factorials = createFactorialsArray(adjMatrix.length, routeLength);
    }

    public RouteTaskExecutorBase(RouteData routeData) throws IOException {
        this(routeData.getAdjMatrix(), routeData.getRouteLength());
    }

    public RouteTaskExecutorBase(int[][] adjMatrix, int routeLength) {
        if (routeLength > adjMatrix.length || routeLength <= 0) {
            throw new RouteLengthException();
        }

        this.adjMatrix = adjMatrix;
        this.routeLength = routeLength;
        this.factorials = createFactorialsArray(adjMatrix.length, routeLength);
    }


    @Override
    public void setExistRoutesListener(Consumer<Route> existRoutesListener) {
        this.existRoutesListener = existRoutesListener;
    }

    @Override
    public void setAllRoutesListener(Consumer<Route> allRoutesListener) {
        this.allRoutesListener = allRoutesListener;
    }


    @Override
    public TaskResult executeTask(Path filePath) throws IOException {
        return executeTask(readTaskFile(filePath));
    }

    @Override
    public TaskResult executeTask(RouteTask routeTask) {
        Route minRoute = initMinRoute();

        BigInteger start = routeTask.getRouteIndex();
        BigInteger end = start.add(routeTask.getIterationCount());
        for (BigInteger i = start; i.compareTo(end) < 0; i = i.add(ONE)) {
            int[] path = nthPermutationSorted(
                    routeLength,
                    i
            );

            Route route = initRoute(path);
            if (route != null) {
                existRoutesListener(route);
                if (route.compareTo(minRoute) < 0) {
                    minRoute = route;
                }
            }
        }

        return new TaskResult(minRoute, routeTask.getTaskIndex());
    }


    /**
     * @param routeLength длина маршрута
     * @param routeIndex  номер маршрута
     * @return маршрут по номеру {@code routeIndex} длиной {@code routeLength }
     */
    private int[] nthPermutationSorted(int routeLength, BigInteger routeIndex) {

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
     * @param nodesCount  количество всех узлов
     * @param routeLength длина маршрута
     * @param routeIndex  номер маршрута
     * @return маршрут по номеру {@code routeIndex} длиной {@code routeLength}
     * из всех узлов {@code nodesCount}
     * @deprecated вызов метода {@link RouteTaskExecutorBase#createFactorialsArray(int, int)}
     * каждую итерацию сильно ухудшает производительность.
     * Лучше использовать {@link RouteTaskExecutorBase#nthPermutationSorted(int, BigInteger)}
     */
    @Deprecated
    private int[] nthPermutationSorted(int nodesCount, int routeLength, BigInteger routeIndex) {

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


    private Route initRoute(int[] path) {
        int weightSum = 0;

        try {
            for (int i = 1; i < path.length; i++) {

                int weight = adjMatrix[path[i - 1]][path[i]];

                if (weight < 0) {
                    throw new NegativeEdgeWeightException();
                }

                if (weight == 0) {

                    allRoutesListener(new Route(path, weightSum));
                    return null;
                }
                weightSum = Math.addExact(weightSum, weight);

            }
        } catch (ArithmeticException e) {
            Route route = new Route(path, Integer.MAX_VALUE);
            allRoutesListener(route);
            return route;
        }

        Route route = new Route(path, weightSum);
        allRoutesListener(route);
        return route;
    }

    private Route initMinRoute() {
        return new Route(new int[]{}, Integer.MAX_VALUE);
    }


    private void allRoutesListener(Route route) {
        if (allRoutesListener != null) {
            allRoutesListener.accept(route.clone());
        }
    }

    private void existRoutesListener(Route route) {
        if (existRoutesListener != null) {
            existRoutesListener.accept(route.clone());
        }
    }


}

