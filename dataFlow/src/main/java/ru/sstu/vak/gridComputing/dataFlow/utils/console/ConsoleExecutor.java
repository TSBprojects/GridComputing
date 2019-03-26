package ru.sstu.vak.gridComputing.dataFlow.utils.console;

import ru.sstu.vak.gridComputing.dataFlow.utils.threading.RunnableTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import static ru.sstu.vak.gridComputing.dataFlow.utils.console.ExecutorCore.parseCommand;

public class ConsoleExecutor {

    private Callback callback;
    private String output;
    private String[] $commands;
    private List<Future<?>> tasks;
    private AtomicInteger completeCommands;

    public ConsoleExecutor() {
        this.output = "";
        this.tasks = new ArrayList<>();
        this.completeCommands = new AtomicInteger(0);
    }

    public void execute(String commands, Callback callback) throws IOException {
        this.callback = callback;

        $commands = parseCommand(commands);
        ExecutorService exec = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        for (String command : parseCommand(commands)) {
            tasks.add(exec.submit(new RunnableTask(() -> {
                try {
                    output += ExecutorCore.execute(command) + "\n";
                } catch (Exception e) {
                    callback.onException(e);
                }
            }, () -> {
                if ($commands.length == completeCommands.addAndGet(1)) {
                    callback.onComplete(output);
                    exec.shutdown();
                }
            })));
        }

    }

    public void stop() {

        for (Future<?> runnableTask : tasks) {
            if (!runnableTask.isDone()) {
                runnableTask.cancel(true);
                callback.onComplete("Command was interrupted!");
                break;
            }
        }

        tasks.forEach(runnableTask -> {
            if (!runnableTask.isDone()) {
                runnableTask.cancel(true);
            }
        });

    }

    public interface Callback {
        void onComplete(String output);

        void onException(Exception e);
    }

}
