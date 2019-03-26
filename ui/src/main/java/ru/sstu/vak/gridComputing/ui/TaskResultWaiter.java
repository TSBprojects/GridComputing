package ru.sstu.vak.gridComputing.ui;

import ru.sstu.vak.gridComputing.dataFlow.entity.Route;
import ru.sstu.vak.gridComputing.dataFlow.entity.TaskResult;
import ru.sstu.vak.gridComputing.dataFlow.exception.NoResultFileFoundException;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static ru.sstu.vak.gridComputing.dataFlow.core.DataManager.readTaskResultFiles;

public class TaskResultWaiter {

    private Route minRoute = new Route(new int[]{}, Integer.MAX_VALUE);

    private Timer timer;
    private TimerTask timerTask;

    private BigInteger taskCount;
    private BigInteger currentTaskCount;
    private Path taskResFolderPath;

    public interface Callback {
        boolean onTaskReceive(TaskResult taskResult);

        void onTaskComplete(TaskResult taskResult, BigInteger index);

        void onWorkComplete(Route minRoute);

        void onException(Exception e);
    }

    public TaskResultWaiter(String taskResFolderPath, BigInteger taskCount) {
        this.taskResFolderPath = Paths.get(taskResFolderPath);
        this.taskCount = taskCount;
    }

    public void start(Callback callback, long delay) {
        this.currentTaskCount = BigInteger.ZERO;
        this.timer = new Timer();
        this.timerTask = new TimerTask() {
            @Override
            public void run() {
                try {
                    List<TaskResult> taskRes = readTaskResultFiles(taskResFolderPath);

                    for (TaskResult taskResult : taskRes) {
                        if (callback.onTaskReceive(taskResult)) {
                            Route route = taskResult.getMinRoute();
                            if (route.compareTo(minRoute) < 0) {
                                minRoute = route;
                            }
                            currentTaskCount = currentTaskCount.add(BigInteger.ONE);
                            callback.onTaskComplete(taskResult, currentTaskCount);
                        }
                    }

                    if (currentTaskCount.equals(taskCount)) {
                        callback.onWorkComplete(minRoute);
                        stop();
                    }

                } catch (IOException e) {
                    callback.onException(e);
                    stop();
                } catch (NoResultFileFoundException ignored) {

                }

            }
        };
        this.timer.schedule(this.timerTask, delay, delay);
    }

    public void stop() {
        timerTask.cancel();
        timer.cancel();
    }

}