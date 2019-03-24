package ru.sstu.vak.gridComputing.dataFlow.exception;

public class TaskBuilderException extends RuntimeException {
    public TaskBuilderException() {
        super("The object is not ready!");
    }

    public TaskBuilderException(String message) {
        super(message);
    }
}
