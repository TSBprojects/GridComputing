package ru.sstu.vak.gridComputing.dataFlow.entity;

import java.math.BigInteger;
import java.util.List;

public class Job {

    private String label;

    private BigInteger taskCount;


    public Job(String label, BigInteger taskCount) {
        this.label = label;
        this.taskCount = taskCount;
    }

    public String getLabel() {
        return label;
    }

    public BigInteger getTaskCount() {
        return taskCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Job)) return false;

        Job job = (Job) o;

        if (!getLabel().equals(job.getLabel())) return false;
        return getTaskCount().equals(job.getTaskCount());
    }

    @Override
    public int hashCode() {
        int result = getLabel().hashCode();
        result = 31 * result + getTaskCount().hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "job :\n" + String.format("  label : %1$s", label);
    }
}
