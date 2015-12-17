package com.delta.core.rover.except;

public class IllegalInterceptStateException extends RuntimeException {
    public IllegalInterceptStateException() {

    }

    public IllegalInterceptStateException(String reason) {
        super(reason);
    }
}
