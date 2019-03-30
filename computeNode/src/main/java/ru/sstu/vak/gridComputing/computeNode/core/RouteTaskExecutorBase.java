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
    private NthPermutation nthPerm;


    private Consumer<Route> existRoutesListener;
    private Consumer<Route> allRoutesListener;


    public RouteTaskExecutorBase(Path dataFilePath) throws IOException {
        RouteData routeData = readDataFile(dataFilePath);
        this.adjMatrix = routeData.getAdjMatrix();
        this.routeLength = routeData.getRouteLength();
        this.nthPerm = new NthPermutation(adjMatrix.length, routeLength);
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
        this.nthPerm = new NthPermutation(adjMatrix.length, routeLength);
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
            int[] path = nthPerm.getPermutation(
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

