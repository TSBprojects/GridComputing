package ru.sstu.vak.gridComputing.dataFlow.entity;

import ru.sstu.vak.gridComputing.dataFlow.exception.IncorrectFileStateException;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TaskResult {

    private BigInteger taskIndex;

    private Route minRoute;


    public TaskResult(Route minRoute, BigInteger taskIndex) {
        this.taskIndex = taskIndex;
        this.minRoute = minRoute;
    }

    public BigInteger getTaskIndex() {
        return taskIndex;
    }

    public Route getMinRoute() {
        return minRoute;
    }

    public String toFileFormat() {
        return "taskIndex=" + getTaskIndex() +
                "\r\n" + minRoute.toFileFormat();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TaskResult)) return false;

        TaskResult that = (TaskResult) o;

        if (!getTaskIndex().equals(that.getTaskIndex())) return false;
        return getMinRoute().equals(that.getMinRoute());
    }

    @Override
    public int hashCode() {
        int result = getTaskIndex().hashCode();
        result = 31 * result + getMinRoute().hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "TaskResult{" +
                "taskIndex=" + taskIndex +
                ", minRoute=" + minRoute +
                '}';
    }
}
