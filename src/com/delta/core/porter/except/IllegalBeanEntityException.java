package com.delta.core.porter.except;

public class IllegalBeanEntityException extends Exception {
    public IllegalBeanEntityException() {

    }

    public IllegalBeanEntityException(String reason) {
        super(reason);
    }
}
