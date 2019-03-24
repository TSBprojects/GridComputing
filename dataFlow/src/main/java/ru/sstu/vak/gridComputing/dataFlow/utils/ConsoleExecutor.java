package ru.sstu.vak.gridComputing.dataFlow.utils;

import ru.sstu.vak.gridComputing.dataFlow.exception.CommandExecutionException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ConsoleExecutor {

    private ConsoleExecutor() {
    }

    public static String execute(String command) throws IOException {
        if (command.equals("")) {
            throw new CommandExecutionException("Empty console command!");
        }

        if (System.getProperty("os.name").contains("Windows")) {
            return core(Runtime.getRuntime().exec("cmd /c " + command));
        } else {
            return core(Runtime.getRuntime().exec(command));
        }
    }

    public static String sudoExecute(String command, String password) throws IOException {
        if (command.equals("") || password.equals("")) {
            throw new CommandExecutionException("Empty console command or password!");
        }

        String[] sudoCommand = new String[]{"bash", "-c", "echo " + password + "| sudo -S " + command};

        return core(Runtime.getRuntime().exec(sudoCommand));
    }

    private static String core(Process p) throws IOException {
        StringBuilder output = new StringBuilder("");
        StringBuilder errors = new StringBuilder("");

        BufferedReader stdInput = new BufferedReader(
                new InputStreamReader((p.getInputStream())));

        BufferedReader stdError = new BufferedReader(
                new InputStreamReader((p.getErrorStream())));

        String outputTmp;
        while ((outputTmp = stdInput.readLine()) != null) {
            output.append(outputTmp);
        }

        String errorsTmp;
        while ((errorsTmp = stdError.readLine()) != null) {
            errors.append(errorsTmp);
        }

        if (!errors.toString().equals("")) {
            throw new CommandExecutionException(errors.toString());
        }

        return output.toString();
    }

}
