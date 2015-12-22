package com.delta.core.rover;

import com.delta.core.rover.annotation.Controller;
import com.delta.core.rover.annotation.RequestMapping;
import com.delta.core.rover.except.AccessDenyException;
import com.delta.core.rover.except.IllegalControllerException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class ActionMapping {
    public static Map<String, Object> controllers;
    public static Map<Class<?>, ActionInterceptor> interceptors;
    public static Map<String, Method> getMap;
    public static Map<String, Method> postMap;

    static {
        controllers = new HashMap<>();
        interceptors = new HashMap<>();
        getMap = new HashMap<>();
        postMap = new HashMap<>();
    }

    public static void load(Class clazz) throws IllegalControllerException {
        if (clazz.getAnnotation(Controller.class) == null) {
            throw new RuntimeException("loading failed " + clazz);
        }
        Controller controller = (Controller) clazz.getAnnotation(Controller.class);
        String namespace = controller.namespace();
        for (Method method : clazz.getMethods()) {
            RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
            if (requestMapping != null) {
                Type[] paramTypes = method.getGenericParameterTypes();
                if (paramTypes.length != 2
                        || paramTypes[0] != HttpServletRequest.class
                        || paramTypes[1] != HttpServletResponse.class
                        || method.getReturnType() != String.class) {
                    throw new IllegalControllerException("check your method declare at " + clazz + " if String "
                            + method.getName() + "(HttpServletRequest request, HttpServletResponse response).");
                }
                if (method.getExceptionTypes().length != 0) {
                    throw new IllegalControllerException("You must deal with exceptions yourself in controller " + clazz + ".");
                }
                for (String pattern : requestMapping.patterns()) {
                    switch (requestMapping.method()) {
                        case RequestMethod.GET:
                            getMap.put(namespace + pattern, method);
                            break;
                        case RequestMethod.POST:
                            postMap.put(namespace + pattern, method);
                            break;
                    }
                }
            }
        }
    }

    public static Method match(HttpServletRequest request) {
        String target = request.getRequestURI();
        target = target.substring(0, target.lastIndexOf('.'));
        switch (request.getMethod()) {
            case "GET":
                for (String key : getMap.keySet())
                    if (key.equals(target))
                        return getMap.get(target);
                break;
            case "POST":
                for (String key : postMap.keySet())
                    if (key.equals(target))
                        return postMap.get(target);
                break;
        }
        return null;
    }

    public static String doAction(Method method, HttpServletRequest request, HttpServletResponse response) throws IOException {
        for (String key : controllers.keySet()) {
            if (key.equals(method.getDeclaringClass().getName())) {
                ActionInterceptor interceptor = interceptors.get(method.getDeclaringClass());
                try {
                    String resp = null;
                    if (interceptor == null) {
                        resp = (String) method.invoke(controllers.get(key), request, response);
                    } else {
                        try {
                            if ((resp = interceptor.intercept(method, request, response)) == null) {
                                resp = (String) method.invoke(controllers.get(key), request, response);
                            }
                        } catch (AccessDenyException e) {
                            response.sendError(403);
                        }
                    }
                    return resp;
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public static void clear() {
        getMap.clear();
        postMap.clear();
        controllers.clear();
    }
}
