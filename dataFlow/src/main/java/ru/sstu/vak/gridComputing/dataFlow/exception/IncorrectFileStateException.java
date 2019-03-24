package ru.sstu.vak.gridComputing.dataFlow.exception;

public class IncorrectFileStateException extends RuntimeException {
    public IncorrectFileStateException() {
        super("Unable to read file!");
    }

    public IncorrectFileStateException(String message) {
        super(message);
    }
}
