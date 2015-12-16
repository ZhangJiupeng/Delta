package com.delta.core.rover.annotation;

import com.delta.core.rover.RequestMethod;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RequestMapping {
    String[] patterns();

    int method() default RequestMethod.GET;
}
