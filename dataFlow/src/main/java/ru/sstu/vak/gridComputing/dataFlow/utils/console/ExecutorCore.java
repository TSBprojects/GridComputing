package ru.sstu.vak.gridComputing.dataFlow.utils.console;

import ru.sstu.vak.gridComputing.dataFlow.exception.CommandExecutionException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ExecutorCore {

    private ExecutorCore() {
    }

    static String execute(String command) throws IOException {
        if (System.getProperty("os.name").contains("Windows")) {
            return core(Runtime.getRuntime().exec("cmd /c " + command));
        } else {
            return core(Runtime.getRuntime().exec(command));
        }
    }

    static String sudoExecute(String command, String password) throws IOException {
        String[] sudoCommand = new String[]{"bash", "-c", "echo " + password + "| sudo -S " + command};

        return core(Runtime.getRuntime().exec(sudoCommand));
    }

    static String[] parseCommand(String command) {
        String[] commands = new String[]{command};
        if (command.contains(";")) {
            return command.trim().split(";");
        }
        return commands;
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
            output.append(outputTmp).append("\n");
        }
        if (!output.toString().equals("")) {
            output = output.replace(output.length() - 1, output.length(), "");
        }

        String errorsTmp;
        while ((errorsTmp = stdError.readLine()) != null) {
            errors.append(errorsTmp).append("\n");
        }
        if (!errors.toString().equals("")) {
            errors = errors.replace(errors.length() - 1, errors.length(), "");
        }

        if (!errors.toString().equals("")) {
            throw new CommandExecutionException(errors.toString());
        }

        return "\n- COMMAND OUTPUT -\n" + output.toString();
    }
}
