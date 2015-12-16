package com.delta.core.assembler.except;

public class DetachException extends Exception {
    public DetachException() {

    }

    public DetachException(String reason) {
        super(reason);
    }
}
