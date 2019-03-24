package ru.sstu.vak.gridComputing.computeNode.exception;

public class NegativeEdgeWeightException extends RuntimeException {
    public NegativeEdgeWeightException() {
        super("Edge weight can not be negative!");
    }

    public NegativeEdgeWeightException(String message) {
        super(message);
    }
}
