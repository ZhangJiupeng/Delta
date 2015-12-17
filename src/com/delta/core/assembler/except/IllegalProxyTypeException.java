package com.delta.core.assembler.except;

public class IllegalProxyTypeException extends RuntimeException {
    public IllegalProxyTypeException() {
    }

    public IllegalProxyTypeException(String reason) {
        super(reason);
    }
}
