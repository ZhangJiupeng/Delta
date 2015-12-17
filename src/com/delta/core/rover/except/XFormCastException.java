package com.delta.core.rover.except;

public class XFormCastException extends Exception {
    public XFormCastException() {

    }

    public XFormCastException(String reason) {
        super(reason);
    }
}
