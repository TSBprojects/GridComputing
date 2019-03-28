package ru.sstu.vak.gridComputing.dataFlow.utils.console;

import ru.sstu.vak.gridComputing.dataFlow.exception.CommandExecutionException;
import ru.sstu.vak.gridComputing.dataFlow.utils.threading.RunnableTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class ConsoleExecutor {

    private ConsoleExecutor() {
    }

    public interface Callback {
        void onOutputLineRead(String outputLine);

        void onCommandComplete();

        void onException(Exception e);
    }

    public static void execute(String command, Callback callback) throws IOException {
        if (System.getProperty("os.name").contains("Windows")) {
            core(Runtime.getRuntime().exec("cmd /c " + command), callback);
        } else {
            core(Runtime.getRuntime().exec(command), callback);
        }
    }


    private static void core(Process p, Callback callback) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(new RunnableTask(() -> {

            StringBuilder errors = new StringBuilder("");

            BufferedReader stdInput = new BufferedReader(
                    new InputStreamReader((p.getInputStream())));

            BufferedReader stdError = new BufferedReader(
                    new InputStreamReader((p.getErrorStream())));

            String outputTmp;
            String errorsTmp;
            StringBuilder result = new StringBuilder("- COMMAND OUTPUT -\n");
            try {
                while ((outputTmp = stdInput.readLine()) != null) {
                    result.append(outputTmp).append("\nCommand executed....");
                    callback.onOutputLineRead(result.toString());
                    result = new StringBuilder("");
                }
                while ((errorsTmp = stdError.readLine()) != null) {
                    errors.append(errorsTmp).append("\n");
                }
                if (!errors.toString().equals("")) {
                    errors = errors.replace(errors.length() - 1, errors.length(), "");
                    callback.onException(new CommandExecutionException(errors.toString()));
                }
            } catch (IOException e) {
                callback.onException(e);
            }

        }, callback::onCommandComplete));

        executorService.shutdown();
    }


    @Deprecated
    public static void sudoExecute(String command, String password, Callback callback) throws IOException {
        String[] sudoCommand = new String[]{"bash", "-c", "echo " + password + "| sudo -S " + command};

        core(Runtime.getRuntime().exec(sudoCommand), callback);
    }

    @Deprecated
    public static String[] parseCommand(String command) {
        String[] commands = new String[]{command.trim()};
        if (command.contains(";")) {

            List<String> commandList = Arrays.stream(command.trim().split(";"))
                    .map(String::trim)
                    .collect(Collectors.toList());

            return commandList.toArray(new String[commandList.size()]);
        }
        return commands;
    }

}
