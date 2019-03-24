package ru.sstu.vak.gridComputing.dataFlow.exception;

public class NoResultFileFoundException extends RuntimeException {
    public NoResultFileFoundException() {
        super();
    }

    public NoResultFileFoundException(String message) {
        super(message);
    }
}
