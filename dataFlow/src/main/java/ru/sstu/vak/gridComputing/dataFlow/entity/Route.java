package ru.sstu.vak.gridComputing.dataFlow.entity;

import java.util.Arrays;
import java.util.Collection;

public class Route implements Comparable<Route>, Cloneable {

    private int weight;

    private int[] nodes;


    public Route(int[] nodes, int weight) {
        this.nodes = nodes;
        this.weight = weight;
    }

    public Route(Collection<Integer> nodes, int weight) {
        this(nodes.stream().mapToInt(i -> i).toArray(), weight);
    }

    public int[] getNodes() {
        return nodes;
    }

    public void setNodes(int[] nodes) {
        this.nodes = nodes;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public String toFileFormat() {
        return "weight=" + weight +
                "\r\nnodes=" + Arrays.toString(nodes);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Route)) return false;
        Route route = (Route) o;
        return getWeight() == route.getWeight() && Arrays.equals(getNodes(), route.getNodes());
    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(getNodes());
        result = 31 * result + getWeight();
        return result;
    }

    @Override
    public String toString() {
        return "Route{" +
                "nodes=" + Arrays.toString(nodes) +
                ", weight=" + weight +
                '}';
    }

    @Override
    public int compareTo(Route route) {
        return Integer.compare(this.weight, route.getWeight());
    }

    @Override
    public Route clone() {
        try {
            return (Route) super.clone();
        } catch (CloneNotSupportedException ex) {
            throw new InternalError();
        }
    }

}


