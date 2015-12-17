package com.delta.core.rover;

import com.delta.core.rover.except.AccessDenyException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

public interface ActionInterceptor {
    String intercept(Method method, HttpServletRequest request, HttpServletResponse response) throws AccessDenyException;
}
