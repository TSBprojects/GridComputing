package ru.sstu.vak.gridComputing.dataFlow.exception;

public class AdjacencyMatrixSizeException extends RuntimeException {
    public AdjacencyMatrixSizeException() {
        super("The size of the adjacency matrix can not be less than 2!");
    }

    public AdjacencyMatrixSizeException(String message) {
        super(message);
    }
}
