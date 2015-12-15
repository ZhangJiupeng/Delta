package com.delta.core.rover;

import com.delta.core.rover.annotation.Controller;
import com.delta.core.rover.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class ActionMapping {
    public static Map<String, Object> controllers;
    public static Map<String, Method> getMap;
    public static Map<String, Method> postMap;

    static {
        controllers = new HashMap<>();
        getMap = new HashMap<>();
        postMap = new HashMap<>();
    }

    public static void load(Class clazz) throws Exception {
        if (clazz.getAnnotation(Controller.class) == null) {
            throw new Exception("");
        }
        controllers.put(clazz.getName(), clazz.newInstance());
        Controller controller = (Controller) clazz.getAnnotation(Controller.class);
        String namespace = controller.namespace();
        for (Method method : clazz.getMethods()) {
            RequestMapping[] requestMapping = method.getAnnotationsByType(RequestMapping.class);
            if (requestMapping.length != 0) {
                for (RequestMapping reqMapping : requestMapping) {
                    switch (reqMapping.method()) {
                        case RequestMethod.GET:
                            getMap.put(namespace + reqMapping.pattern(), method);
                            break;
                        case RequestMethod.POST:
                            postMap.put(namespace + reqMapping.pattern(), method);
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
                for (String key : getMap.keySet()) {
                    if (key.equals(target)) {
                        return getMap.get(target);
                    }
                }
                break;
            case "POST":
                for (String key : postMap.keySet()) {
                    if (key.equals(target)) {
                        return postMap.get(target);
                    }
                }
                break;
        }
        return null;
    }

    public static String doAction(Method method, HttpServletRequest request, HttpServletResponse response) {
        for (String key : controllers.keySet()) {
            if (key.equals(method.getDeclaringClass().getName())) {
                try {
                    return (String) method.invoke(controllers.get(key), request, response);
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
