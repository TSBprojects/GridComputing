package ru.sstu.vak.gridComputing.dataFlow.core;

import ru.sstu.vak.gridComputing.dataFlow.entity.*;
import ru.sstu.vak.gridComputing.dataFlow.exception.IncorrectFileStateException;
import ru.sstu.vak.gridComputing.dataFlow.exception.NoResultFileFoundException;
import ru.sstu.vak.gridComputing.dataFlow.exception.RouteLengthException;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class DataManager {

    public static String DATA_FILE_NAME = "Data";
    public static String TASK_FILE_NAME = "Task_";
    public static final String FILE_EXTENSION = ".rb";
    public static final String JOB_FILE_EXTENSION = ".jdf";
    public static final String TASK_RESULT_NAME = "Task_Result_";
    public static String JOB_NAME_PATTERN;

    private static String SEPARATOR;
    private static String DATA_NAME_PATTERN;
    private static String TASK_NAME_PATTERN;
    private static String TASK_RESULT_NAME_PATTERN;

    static {
        if (System.getProperty("os.name").contains("Windows")) {
            SEPARATOR = "\\";
        } else {
            SEPARATOR = "/";
        }

        TASK_RESULT_NAME_PATTERN = "%1$s" + SEPARATOR + TASK_RESULT_NAME + "%2$s" + FILE_EXTENSION;
        JOB_NAME_PATTERN = "%1$s" + SEPARATOR + "%2$s" + JOB_FILE_EXTENSION;
        DATA_NAME_PATTERN = "%1$s" + SEPARATOR + DATA_FILE_NAME + FILE_EXTENSION;
        TASK_NAME_PATTERN = "%1$s" + SEPARATOR + TASK_FILE_NAME + "%2$s" + FILE_EXTENSION;
    }

    private DataManager() {
    }

    public static RouteTask readTaskFile(Path filePath) throws IOException {

        List<String> taskProps = Files.readAllLines(filePath);
        if (taskProps.size() < 3) {
            throw new IncorrectFileStateException();
        }

        Pattern p = Pattern.compile("(\\d+)");
        Matcher mTaskIndex = p.matcher(taskProps.get(0));
        Matcher mTaskSize = p.matcher(taskProps.get(1));
        Matcher mIterationCount = p.matcher(taskProps.get(2));

        if (!mTaskIndex.find() || !mTaskSize.find() || !mIterationCount.find()) {
            throw new IncorrectFileStateException();
        }

        BigInteger taskIndex = new BigInteger(mTaskIndex.group(1));
        BigInteger taskSize = new BigInteger(mTaskSize.group(1));
        BigInteger iterationCount = new BigInteger(mIterationCount.group(1));

        return new RouteTask.Builder()
                .taskIndex(taskIndex)
                .taskSize(taskSize)
                .iterationCount(iterationCount)
                .build();
    }

    public static RouteData readDataFile(Path filePath) throws IOException {

        List<String> data = Files.readAllLines(filePath);
        if (data.size() < 1) {
            throw new IncorrectFileStateException();
        }


        int routeLength;
        int adjMatrixSize = data.size() - 2;
        int[][] adjMatrix = new int[adjMatrixSize][adjMatrixSize];

        Pattern p = Pattern.compile("(\\d+)");
        Matcher m = p.matcher(data.get(0));
        if (m.find()) {
            throw new IncorrectFileStateException();
        }

        for (int i = 0; i < adjMatrixSize; i++) {

            m = p.matcher(data.get(i + 1));

            int j = 0;
            while (m.find()) {
                adjMatrix[i][j] = Integer.parseInt(m.group(1));
                j++;
            }

            if (j != adjMatrixSize) {
                throw new IncorrectFileStateException();
            }
        }

        m = p.matcher(data.get(data.size() - 1));
        if (m.find()) {
            routeLength = Integer.parseInt(m.group(1));
        } else {
            throw new IncorrectFileStateException();
        }

        if (routeLength > adjMatrix.length || routeLength <= 0) {
            throw new RouteLengthException();
        }

        return new RouteData(adjMatrix, routeLength);
    }

    public static Route readRouteFile(Path filePath) throws IOException {

        List<String> routeProps = Files.readAllLines(filePath);
        if (routeProps.size() < 2) {
            throw new IncorrectFileStateException();
        }

        Pattern p = Pattern.compile("(\\d+)");
        Matcher mWeight = p.matcher(routeProps.get(0));
        Matcher mNodes = p.matcher(routeProps.get(1));

        if (!mWeight.find() || !mNodes.find()) {
            throw new IncorrectFileStateException();
        }

        int weight = Integer.parseInt(mWeight.group(1));
        List<Integer> nodesList = new ArrayList<>();

        do {
            nodesList.add(Integer.parseInt(mNodes.group(1)));
        } while (mNodes.find());


        return new Route(nodesList, weight);
    }

    public static TaskResult readTaskResultFile(Path filePath) throws IOException {

        List<String> routeProps = Files.readAllLines(filePath);
        if (routeProps.size() < 3) {
            throw new IncorrectFileStateException();
        }

        Pattern p = Pattern.compile("(\\d+)");
        Matcher mTaskIndex = p.matcher(routeProps.get(0));
        Matcher mWeight = p.matcher(routeProps.get(1));
        Matcher mNodes = p.matcher(routeProps.get(2));

        if (!mWeight.find() || !mNodes.find() || !mTaskIndex.find()) {
            throw new IncorrectFileStateException();
        }

        BigInteger taskIndex = new BigInteger(mTaskIndex.group(1));
        int weight = Integer.parseInt(mWeight.group(1));
        List<Integer> nodesList = new ArrayList<>();

        do {
            nodesList.add(Integer.parseInt(mNodes.group(1)));
        } while (mNodes.find());

        return new TaskResult(new Route(nodesList, weight), taskIndex);
    }

    public static List<TaskResult> readTaskResultFiles(Path folderPath) throws IOException {

        List<TaskResult> taskResults = new ArrayList<>();

        List<Path> paths = Files.walk(folderPath)
                .filter(path ->
                        isRightFile(
                                path,
                                "(" + TASK_RESULT_NAME + "\\d+" + FILE_EXTENSION + ")"
                        )
                )
                .collect(Collectors.toList());

        if (paths.size() == 0) {
            throw new NoResultFileFoundException();
        }

        for (Path path : paths) {
            taskResults.add(readTaskResultFile(path));
            StringBuilder stringBuffer = new StringBuilder(path.getFileName().toString());
            stringBuffer.insert(stringBuffer.indexOf("."), "_Checked");
            Path newPath = path.resolveSibling(stringBuffer.toString());
            Files.deleteIfExists(newPath);
            Files.move(path, newPath);
        }

        return taskResults;
    }


    public static Path writeDataFile(RouteData routeData, Path folderPath) throws IOException {

        Path filePath = Paths.get(String.format(DATA_NAME_PATTERN, folderPath));

        writeFile(filePath, routeData.toFileFormat(), "UTF-8");

        return filePath;
    }

    public static Path writeTaskFile(RouteTask routeTask, Path folderPath) throws IOException {
        Path filePath = Paths.get(String.format(TASK_NAME_PATTERN, folderPath, routeTask.getTaskIndex()));

        writeFile(filePath, routeTask.toFileFormat(), "UTF-8");

        return filePath;
    }

    @Deprecated
    public static Path writeJobFile(Job job, Path folderPath) throws IOException {

        Path filePath = Paths.get(String.format(JOB_NAME_PATTERN, folderPath, job.getLabel()));

        writeFile(filePath, job.toString(), "UTF-8");

        return filePath;
    }

    public static Path writeTaskResultFile(TaskResult taskResult, Path folderPath) throws IOException {

        Path filePath = Paths.get(String.format(TASK_RESULT_NAME_PATTERN, folderPath, taskResult.getTaskIndex()));

        writeFile(filePath, taskResult.toFileFormat(), "UTF-8");

        return filePath;
    }


    public static void writeFile(Path path, String data, String charset) throws IOException {
        Files.deleteIfExists(path);
        Files.createFile(path);
        Files.write(
                path,
                Collections.singletonList(data),
                Charset.forName(charset)
        );
    }


    private static boolean isRightFile(Path path, String pattern) {
        String fileName = path.getFileName().toString().toLowerCase();
        Pattern p = Pattern.compile(pattern.toLowerCase());
        Matcher m = p.matcher(fileName);
        return m.find() && fileName.endsWith(FILE_EXTENSION);
    }

}
