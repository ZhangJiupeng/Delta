package com.delta.core.rover;

public interface XForm {
    default boolean validate() {
        return true;
    }

//    <T> T convert(Class<T> clazz);
}
