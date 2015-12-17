package com.test.interceptor;

import com.delta.core.rover.ActionInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

public class TestInterceptor implements ActionInterceptor {
    @Override
    public String intercept(Method method, HttpServletRequest request, HttpServletResponse response) {
        System.out.println("before action");
        return null;
    }
}
