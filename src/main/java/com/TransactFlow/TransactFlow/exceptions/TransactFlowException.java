package com.TransactFlow.TransactFlow.exceptions;

public class TransactFlowException extends RuntimeException {
    public TransactFlowException(String message) {
        super(message);
    }

    public TransactFlowException(String message, Throwable cause) {
        super(message, cause);
    }
}
