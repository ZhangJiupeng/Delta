package com.delta.garbage.logger;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class LogProxy<T> implements InvocationHandler {
    private T target;
    private String[] fields;

    public LogProxy(T target, String... fields) {
        this.target = target;
        this.fields = fields;
    }

    public void beforeExecute() {
        for (String s : fields) {
            try {
                Field field = target.getClass().getDeclaredField(s);
                System.err.println(field.getName() + ": " + field);
            } catch (NoSuchFieldException e) {
                continue;
            }
        }
    }

    public void afterExecute() {
        for (String s : fields) {
            try {
                Field field = target.getClass().getDeclaredField(s);
                System.err.println(field.getName() + ": " + field);
            } catch (NoSuchFieldException e) {
                continue;
            }
        }
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.err.println("before " + method);
        beforeExecute();
        Object returnValue = method.invoke(target, args);
        System.err.println("after " + method);
        afterExecute();
        return returnValue;
    }
}
