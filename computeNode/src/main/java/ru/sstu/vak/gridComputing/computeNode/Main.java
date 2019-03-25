package ru.sstu.vak.gridComputing.computeNode;

import ru.sstu.vak.gridComputing.computeNode.core.RouteTaskExecutor;
import ru.sstu.vak.gridComputing.computeNode.core.RouteTaskExecutorBase;
import ru.sstu.vak.gridComputing.dataFlow.entity.RouteTask;
import ru.sstu.vak.gridComputing.dataFlow.entity.TaskResult;

import java.io.IOException;
import java.nio.file.Paths;

import static ru.sstu.vak.gridComputing.dataFlow.core.DataManager.readTaskFile;
import static ru.sstu.vak.gridComputing.dataFlow.core.DataManager.writeTaskResultFile;

public class Main {

    private static final String help = "\nAvailable commands:\n\n" +
            "'cn.jar RouteData.rb T1.rb -exec'          Complete the task and print result.\n" +
            "'cn.jar RouteData.rb T1.rb C:\\res -exec'   Complete the task 'T1.rb' and generate\n" +
            "                                           the result file in folder 'C:\\res'.\n";


    public static void main(String[] args) throws Exception {

        if (args.length == 3) {
            switch (args[2]) {
                case "-exec":
                case "-e": {
                    executeTask(args[0], args[1]);
                    break;
                }
                default: {
                    System.out.println(help);
                }
            }
        } else if (args.length == 4) {
            switch (args[3]) {
                case "-exec":
                case "-e": {
                    executeTask(args[0], args[1], args[2]);
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

    private static void executeTask(String dataFilePath, String taskFilePath)
            throws IOException {

        RouteTaskExecutor taskExecutor = new RouteTaskExecutorBase(Paths.get(dataFilePath));
        RouteTask task = readTaskFile(Paths.get(taskFilePath));
        TaskResult taskResult = taskExecutor.executeTask(task);

        System.out.println(taskResult.toFileFormat());
    }

    private static void executeTask(String dataFilePath, String taskFilePath, String resultFolder)
            throws IOException {
        RouteTaskExecutor taskExecutor = new RouteTaskExecutorBase(Paths.get(dataFilePath));

        RouteTask task = readTaskFile(Paths.get(taskFilePath));
        TaskResult taskResult = taskExecutor.executeTask(task);
        writeTaskResultFile(taskResult, Paths.get(resultFolder));
    }
}
