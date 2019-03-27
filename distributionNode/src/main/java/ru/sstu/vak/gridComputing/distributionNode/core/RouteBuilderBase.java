package ru.sstu.vak.gridComputing.distributionNode.core;

import ru.sstu.vak.gridComputing.dataFlow.entity.*;
import ru.sstu.vak.gridComputing.dataFlow.exception.RouteLengthException;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.function.Consumer;

import static java.math.BigInteger.ONE;
import static java.math.BigInteger.ZERO;
import static ru.sstu.vak.gridComputing.dataFlow.core.DataManager.*;
import static ru.sstu.vak.gridComputing.dataFlow.utils.MathUtils.inverseFactorial;

public class RouteBuilderBase implements RouteBuilder {

    private Route minRoute;

    private int routeLength;
    private int[][] adjMatrix;
    private BigInteger taskSize;


    public RouteBuilderBase(Path dataFilePath) throws IOException {
        RouteData routeData = readDataFile(dataFilePath);
        initialize(routeData.getAdjMatrix(), routeData.getRouteLength());
    }

    public RouteBuilderBase(int[][] adjMatrix, int routeLength) {
        initialize(adjMatrix, routeLength);
    }

    public RouteBuilderBase(RouteData routeData) {
        this(routeData.getAdjMatrix(), routeData.getRouteLength());
    }


    @Override
    public RouteData getRouteData() {
        return new RouteData(adjMatrix, routeLength);
    }


    @Override
    public Path writeJobAndTaskFiles(
            BigInteger taskSize, String jobName, Path jarFilePath,
            Path dataFilePath, Path tasksFolderPath,
            Path jobFolderPath, String remoteCommand

    ) throws IOException {
        this.taskSize = taskSize;

        Job job = new Job(jobName, null);
        Path jobFilePath = Paths.get(String.format(JOB_NAME_PATTERN, jobFolderPath, jobName));
        writeFile(jobFilePath, job.toString(), "UTF-8");
        taskIterator(routeTask -> {
            try {
                Path taskPath = writeTaskFile(routeTask, tasksFolderPath);
                Files.write(
                        jobFilePath,
                        new JobTask(
                                jarFilePath,
                                dataFilePath,
                                taskPath,
                                TASK_RESULT_NAME + routeTask.getTaskIndex() + FILE_EXTENSION,
                                remoteCommand
                        ).toString().getBytes(),
                        StandardOpenOption.APPEND
                );
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        return jobFilePath;
    }

//    @Override
//    public void writeTaskFiles(BigInteger taskSize, Path folderPath) {
//        this.taskSize = taskSize;
//
//        taskIterator(routeTask -> {
//            try {
//                writeTaskFile(routeTask, folderPath);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        });
//    }
//
//    @Override
//    public Path writeJobFile(
//            BigInteger taskSize, String jobName,
//            Path jarFilePath, Path dataFolderPath, Path taskFilePath,
//            Path folderPath, String remoteCommand
//    ) throws IOException {
//        this.taskSize = taskSize;
//
//        Job job = new Job(jobName, null);
//        Path filePath = Paths.get(String.format(JOB_NAME_PATTERN, folderPath, jobName));
//        Path filePath = Paths.get(String.format(DATA_NAME_PATTERN, folderPath, jobName));
//
//        writeFile(filePath, job.toString(), "UTF-8");
//        taskIterator(routeTask -> {
//            try {
//                Files.write(
//                        filePath,
//                        new JobTask(
//                                jarFilePath,
//                                DATA_FILE_NAME + FILE_EXTENSION,
//                                TASK_FILE_NAME + routeTask.getTaskIndex() + FILE_EXTENSION,
//                                TASK_RESULT_NAME + routeTask.getTaskIndex() + FILE_EXTENSION,
//                                remoteCommand
//                        ).toString().getBytes(),
//                        StandardOpenOption.APPEND
//                );
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        });
//
//        return filePath;
//    }


    @Override
    public Route getFinalResult(List<TaskResult> taskResults) {
        this.minRoute = initMinRoute();
        for (TaskResult taskResult : taskResults) {
            minRouteFinder(taskResult.getMinRoute());
        }
        return this.minRoute;
    }

    @Override
    public BigInteger getTaskCount(BigInteger taskSize) {
        BigInteger routesCount = inverseFactorial(this.adjMatrix.length, this.routeLength);
        BigInteger taskCount = routesCount.divide(taskSize);

        // остаток при нечётном количестве подзадач
        BigInteger remainder = routesCount.subtract(taskCount.multiply(taskSize));
        if (remainder.compareTo(ZERO) > 0) {
            taskCount = taskCount.add(ONE);
        }

        return taskCount;
    }


    private void taskIterator(Consumer<RouteTask> task) {

        BigInteger routesCount = inverseFactorial(this.adjMatrix.length, this.routeLength);
        BigInteger taskCount = routesCount.divide(this.taskSize);

        for (BigInteger i = ZERO; i.compareTo(taskCount) < 0; i = i.add(ONE)) {

            RouteTask routeTask = new RouteTask.Builder()
                    .taskIndex(i)
                    .taskSize(taskSize)
                    .build();

            task.accept(routeTask);
        }

        // остаток при нечётном количестве подзадач
        BigInteger remainder = routesCount.subtract(taskCount.multiply(taskSize));
        if (remainder.compareTo(ZERO) > 0) {

            RouteTask routeTask = new RouteTask.Builder()
                    .taskIndex(taskCount)
                    .taskSize(taskSize)
                    .iterationCount(remainder)
                    .build();

            task.accept(routeTask);
        }
    }

    private Route initMinRoute() {
        return new Route(new int[]{}, Integer.MAX_VALUE);
    }

    private void minRouteFinder(Route route) {
        if (route.compareTo(minRoute) < 0) {
            minRoute = route;
        }
    }


    private void validateData() {
        if (this.routeLength > this.adjMatrix.length || this.routeLength <= 0) {
            throw new RouteLengthException();
        }
    }

    private void initialize(int[][] adjMatrix, int routeLength) {
        this.adjMatrix = adjMatrix;
        this.routeLength = routeLength;
        validateData();
    }

}

