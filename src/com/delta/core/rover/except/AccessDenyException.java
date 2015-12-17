package com.delta.core.rover.except;

public class AccessDenyException extends Exception {
    public AccessDenyException() {

    }

    public AccessDenyException(String reason) {
        super(reason);
    }
}
