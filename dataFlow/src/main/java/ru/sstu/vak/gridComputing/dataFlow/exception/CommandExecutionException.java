package ru.sstu.vak.gridComputing.dataFlow.exception;

public class CommandExecutionException extends RuntimeException {
    public CommandExecutionException() {
        super();
    }

    public CommandExecutionException(String message) {
        super(message);
    }
}
