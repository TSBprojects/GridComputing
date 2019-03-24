package ru.sstu.vak.gridComputing.dataFlow.entity;

import java.util.Arrays;

public class RouteData {

    private int routeLength;

    private int[][] adjMatrix;


    public RouteData(int[][] adjMatrix, int routeLength) {
        this.routeLength = routeLength;
        this.adjMatrix = adjMatrix;
    }

    public int getRouteLength() {
        return routeLength;
    }

    public int[][] getAdjMatrix() {
        return adjMatrix;
    }

    public String toFileFormat() {
        StringBuilder res = new StringBuilder("adjMatrix=\r\n");

        for (int i = 0; i < adjMatrix.length; i++) {
            for (int j = 0; j < adjMatrix[i].length - 1; j++) {
                res.append(adjMatrix[i][j]).append(" ");
            }
            res.append(adjMatrix[i][adjMatrix[i].length - 1]).append("\r\n");
        }
        res.append("routeLength=").append(routeLength);

        return res.toString();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RouteData)) return false;

        RouteData routeData = (RouteData) o;

        if (getRouteLength() != routeData.getRouteLength()) return false;
        return Arrays.deepEquals(getAdjMatrix(), routeData.getAdjMatrix());
    }

    @Override
    public int hashCode() {
        int result = getRouteLength();
        result = 31 * result + Arrays.deepHashCode(getAdjMatrix());
        return result;
    }

    @Override
    public String toString() {
        return "RouteData{" +
                "routeLength=" + routeLength +
                ", adjMatrix=" + Arrays.toString(adjMatrix) +
                '}';
    }
}
