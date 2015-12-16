package com.delta.core.rover.except;

public class IllegalControllerException extends Exception {
    public IllegalControllerException() {

    }

    public IllegalControllerException(String reason) {
        super(reason);
    }
}
