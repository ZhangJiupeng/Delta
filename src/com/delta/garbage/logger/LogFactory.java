package com.delta.garbage.logger;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

@SuppressWarnings("ALL")
public final class LogFactory {
    public static <T> T toProxyInstance(T target, String... fields) {
        InvocationHandler handler = new LogProxy<T>(target, fields);
        return (T) Proxy.newProxyInstance(target.getClass().getClassLoader(), new Class[]{target.getClass()}, handler);
    }
}
