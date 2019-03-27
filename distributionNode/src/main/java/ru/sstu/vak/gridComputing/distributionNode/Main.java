package ru.sstu.vak.gridComputing.distributionNode;

import ru.sstu.vak.gridComputing.dataFlow.entity.Route;
import ru.sstu.vak.gridComputing.dataFlow.entity.RouteData;
import ru.sstu.vak.gridComputing.dataFlow.entity.TaskResult;
import ru.sstu.vak.gridComputing.distributionNode.core.RouteBuilder;
import ru.sstu.vak.gridComputing.distributionNode.core.RouteBuilderBase;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Paths;
import java.util.List;

import static ru.sstu.vak.gridComputing.dataFlow.core.DataManager.readTaskResultFiles;
import static ru.sstu.vak.gridComputing.dataFlow.core.DataManager.writeDataFile;
import static ru.sstu.vak.gridComputing.dataFlow.utils.MathUtils.genAdjMatrix;

public class Main {

    private static final int N = 3;

    private static final int[][] M = new int[][]{
            {0, 3, 0, 1, 0, 0, 0, 0, 0},
            {3, 0, 5, 0, 3, 0, 0, 0, 0},
            {0, 5, 0, 0, 0, 1, 0, 0, 0},
            {1, 0, 0, 0, 4, 0, 1, 0, 0},
            {0, 3, 0, 4, 0, 1, 0, 1, 0},
            {0, 0, 1, 0, 1, 0, 0, 0, 2},
            {0, 0, 0, 1, 0, 0, 0, 1, 0},
            {0, 0, 0, 0, 1, 0, 1, 0, 3},
            {0, 0, 0, 0, 0, 2, 0, 3, 0},
    };

    private static final String help = "\nAvailable commands:\n\n" +
            "'rb.jar data.rb C:\\jobs job1 prog1 300 -j'  Create job file in folder 'C:\\jobs'\n" +
            "                                            with label 'job1', jar file name\n" +
            "                                            'prog1' and task size 300.\n" +
            "'rb.jar RouteData.rb C:\\tasks 300 -t'       Create tasks with size 300 in\n" +
            "                                            folder 'C:\\tasks'.\n" +
            "'rb.jar 11 5 C:\\data -d'                    Create an adjacency matrix in\n" +
            "                                            folder 'C:\\data', size 11 and the\n" +
            "                                            length of route 5.\n" +
            "'rb.jar -default -default C:\\data -d'       Create an adjacency matrix in\n" +
            "                                            folder 'C:\\data', default size 9\n" +
            "                                            and the default length of route 3.\n" +
            "'rb.jar RouteData.rb C:\\res -r'             Read the resulting files from the\n" +
            "                                            'C:\\res' folder and print\n" +
            "                                            the final result.\n";


    public static void main(String[] args) throws Exception {

        if (args.length == 3) {
            switch (args[2]) {
                case "-result":
                case "-r": {
                    System.out.println("Min route - " + getTasksResult(args[0], args[1]));
                    break;
                }
                default: {
                    System.out.println(help);
                }
            }
        } else if (args.length == 4) {
            switch (args[3]) {
                case "-data":
                case "-d": {
                    if (args[0].equals("-default") && args[1].equals("-default")) {
                        generateDataFile(M, N, args[2]);
                    } else {
                        generateDataFile(Integer.parseInt(args[0]), Integer.parseInt(args[1]), args[2]);
                    }
                    break;
                }
                case "-tasks":
                case "-t": {
                    generateTaskFiles(args[0], args[1], new BigInteger(args[2]));
                    break;
                }
                default: {
                    System.out.println(help);
                }
            }
        } else if (args.length == 6) {
            switch (args[5]) {
                case "-job":
                case "-j": {
                    generateJobFile(args[0], args[1], args[2], args[3], new BigInteger(args[4]));
                    break;
                }
                default: {
                    System.out.println(help);
                }
            }
        } else {
            System.out.println(help);
        }
    }


    private static Route getTasksResult(String dataFilePath, String resultsFolderPath)
            throws IOException {

        RouteBuilder routeBuilder = new RouteBuilderBase(Paths.get(dataFilePath));
        List<TaskResult> taskResults = readTaskResultFiles(Paths.get(resultsFolderPath));
        return routeBuilder.getFinalResult(taskResults);
    }

    private static void generateDataFile(int adjMatrixSize, int routeLength, String dataFolder)
            throws IOException {

        writeDataFile(
                new RouteData(genAdjMatrix(adjMatrixSize, 0, 10), routeLength),
                Paths.get(dataFolder)
        );
    }

    private static void generateDataFile(int[][] adjMatrix, int routeLength, String dataFolder)
            throws IOException {

        RouteBuilder routeBuilder = new RouteBuilderBase(
                adjMatrix,
                routeLength
        );
        writeDataFile(new RouteData(adjMatrix, routeLength), Paths.get(dataFolder));
    }

    private static void generateTaskFiles(String dataFilePath, String tasksFolder, BigInteger taskSize)
            throws IOException {
        throw new UnsupportedOperationException();
//        RouteBuilder routeBuilder = new RouteBuilderBase(Paths.get(dataFilePath));
//        routeBuilder.writeTaskFiles(taskSize, Paths.get(tasksFolder));
    }


    private static void generateJobFile(
            String dataFilePath, String jobsFolder,
            String jobName, String jarName, BigInteger taskSize
    ) throws IOException {
        throw new UnsupportedOperationException();
//        RouteBuilder routeBuilder = new RouteBuilderBase(Paths.get(dataFilePath));
//        routeBuilder.writeJobFile(
//                taskSize, jobName,
//                Paths.get(jarName), Paths.get(jobsFolder),
//                "java -jar $JAR $DATA $TASK > $RESULT"
//        );
    }

}
