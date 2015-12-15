package com.delta.core.porter.except;

public class IllegalBeanEntityException extends RuntimeException {
    public IllegalBeanEntityException() {

    }

    public IllegalBeanEntityException(String reason) {
        super(reason);
    }
}
