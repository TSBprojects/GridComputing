package ru.sstu.vak.gridComputing.dataFlow.entity;

import ru.sstu.vak.gridComputing.dataFlow.exception.TaskBuilderException;
import java.math.BigInteger;

public class RouteTask {

    private BigInteger taskIndex;

    private BigInteger taskSize;

    private BigInteger iterationCount;


    public static class Builder {

        private BigInteger taskIndex;
        private BigInteger taskSize;

        private BigInteger iterationCount;


        public Builder taskIndex(BigInteger taskIndex) {
            this.taskIndex = taskIndex;
            return this;
        }

        public Builder taskSize(BigInteger taskSize) {
            this.taskSize = taskSize;
            return this;
        }

        public Builder iterationCount(BigInteger iterationCount) {
            this.iterationCount = iterationCount;
            return this;
        }

        public RouteTask build() {

            if (taskIndex == null || taskSize == null) {
                throw new TaskBuilderException("The object is not ready! " +
                        "The following parameters must be defined: taskIndex, taskSize.");
            }

            return new RouteTask(this);
        }
    }

    public RouteTask(Builder builder) {
        this.taskIndex = builder.taskIndex;
        this.taskSize = builder.taskSize;
        this.iterationCount = builder.iterationCount;
    }

    public BigInteger getTaskIndex() {
        return taskIndex;
    }

    public BigInteger getRouteIndex() {
        return taskIndex.multiply(taskSize);
    }

    public BigInteger getTaskSize() {
        return taskSize;
    }

    public BigInteger getIterationCount() {
        if (iterationCount == null) {
            return taskSize;
        } else {
            return iterationCount;
        }
    }

    public String toFileFormat() {
        return "taskIndex=" + getTaskIndex() +
                "\r\ntaskSize=" + getTaskSize() +
                "\r\niterationCount=" + getIterationCount();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RouteTask)) return false;

        RouteTask routeTask = (RouteTask) o;

        if (!taskIndex.equals(routeTask.taskIndex)) return false;
        if (!taskSize.equals(routeTask.taskSize)) return false;
        return getIterationCount().equals(routeTask.getIterationCount());
    }

    @Override
    public int hashCode() {
        int result = taskIndex.hashCode();
        result = 31 * result + taskSize.hashCode();
        result = 31 * result + getIterationCount().hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "RouteTask{" +
                "taskIndex=" + taskIndex +
                ", taskSize=" + taskSize +
                ", routeIndex=" + getRouteIndex() +
                ", iterationCount=" + getIterationCount() +
                '}';
    }
}
