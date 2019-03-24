package ru.sstu.vak.gridComputing.dataFlow.entity;

import java.nio.file.Path;

public class JobTask {

    private Path jarFileName;
    private String dataFileName;
    private String taskFileName;
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
            Path jarFilePath, String dataFileName,
            String taskFileName, String taskResultFileName,
            String remoteCommand
    ) {
        this.jarFileName = jarFilePath;
        this.dataFileName = dataFileName;
        this.taskFileName = taskFileName;
        this.taskResultFileName = taskResultFileName;
        this.remoteCommand = remoteCommand;
    }

    public Path getJarFileName() {
        return jarFileName;
    }

    public String getDataFileName() {
        return dataFileName;
    }

    public String getTaskFileName() {
        return taskFileName;
    }

    public String getTaskResultFileName() {
        return taskResultFileName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof JobTask)) return false;

        JobTask jobTask = (JobTask) o;

        if (!getJarFileName().equals(jobTask.getJarFileName())) return false;
        if (!getDataFileName().equals(jobTask.getDataFileName())) return false;
        if (!getTaskFileName().equals(jobTask.getTaskFileName())) return false;
        return getTaskResultFileName().equals(jobTask.getTaskResultFileName());
    }

    @Override
    public int hashCode() {
        int result = getJarFileName().hashCode();
        result = 31 * result + getDataFileName().hashCode();
        result = 31 * result + getTaskFileName().hashCode();
        result = 31 * result + getTaskResultFileName().hashCode();
        return result;
    }

    @Override
    public String toString() {

        String remComm = "  remote : " + this.remoteCommand
                .replace("$JAR", jarFileName.getFileName().toString())
                .replace("$DATA", dataFileName)
                .replace("$TASK", taskFileName)
                .replace("$RESULT", taskResultFileName) + "\n";

        return "task :\n" +
                "  init :\n" +
                String.format("    put %1$s %2$s\n", jarFileName, jarFileName.getFileName()) +
                String.format("    put %1$s %1$s\n", dataFileName) +
                String.format("    put %1$s %1$s\n", taskFileName) + remComm +
                String.format("  final : get %1$s %1$s\n", taskResultFileName);
    }
}