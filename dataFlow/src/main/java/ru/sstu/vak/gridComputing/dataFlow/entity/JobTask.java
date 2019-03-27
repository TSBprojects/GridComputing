package ru.sstu.vak.gridComputing.dataFlow.entity;

import java.nio.file.Path;

public class JobTask {

    private Path jarFilePath;
    private Path dataFilePath;
    private Path taskFilePath;
    private String taskResultFileName;
    private String remoteCommand;

    /**
     * @param remoteCommand Command example:
     * "java -jar $JAR $DATA $TASK > $RESULT".
     * $JAR - jar file,
     * $DATA - data file,
     * $TASK - task file,
     * $RESULT – result file;
     */
    public JobTask(
            Path jarFilePath, Path dataFilePath,
            Path taskFilePath, String taskResultFileName,
            String remoteCommand
    ) {
        this.jarFilePath = jarFilePath;
        this.dataFilePath = dataFilePath;
        this.taskFilePath = taskFilePath;
        this.taskResultFileName = taskResultFileName;
        this.remoteCommand = remoteCommand;
    }

    public Path getJarFilePath() {
        return jarFilePath;
    }

    public Path getDataFilePath() {
        return dataFilePath;
    }

    public Path getTaskFilePath() {
        return taskFilePath;
    }

    public String getTaskResultFileName() {
        return taskResultFileName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof JobTask)) return false;

        JobTask jobTask = (JobTask) o;

        if (!getJarFilePath().equals(jobTask.getJarFilePath())) return false;
        if (!getDataFilePath().equals(jobTask.getDataFilePath())) return false;
        if (!getTaskFilePath().equals(jobTask.getTaskFilePath())) return false;
        return getTaskResultFileName().equals(jobTask.getTaskResultFileName());
    }

    @Override
    public int hashCode() {
        int result = getJarFilePath().hashCode();
        result = 31 * result + getDataFilePath().hashCode();
        result = 31 * result + getTaskFilePath().hashCode();
        result = 31 * result + getTaskResultFileName().hashCode();
        return result;
    }

    @Override
    public String toString() {

        String remComm = "  remote : " + this.remoteCommand
                .replace("$JAR", jarFilePath.getFileName().toString())
                .replace("$DATA", dataFilePath.getFileName().toString())
                .replace("$TASK", taskFilePath.getFileName().toString())
                .replace("$RESULT", taskResultFileName) + "\n";

        return "task :\n" +
                "  init :\n" +
                String.format("    put %1$s %2$s\n", jarFilePath, jarFilePath.getFileName()) +
                String.format("    put %1$s %2$s\n", dataFilePath, dataFilePath.getFileName()) +
                String.format("    put %1$s %2$s\n", taskFilePath, taskFilePath.getFileName()) + remComm +
                String.format("  final : get %1$s %1$s\n", taskResultFileName);
    }
}