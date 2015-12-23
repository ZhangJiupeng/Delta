package com.delta.core.rover;

import com.delta.core.rover.except.XFormCastException;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

public class XFormLoader {

    public static <T> T newInstance(HttpServletRequest request, Class<T> clazz) throws XFormCastException {
        T target;
        boolean isXForm = false;
        for (Type type : clazz.getInterfaces())
            if (type == XForm.class)
                isXForm = true;
        if (!isXForm) {
            throw new XFormCastException(clazz + " must implements " + XForm.class);
        }
        try {
            target = clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new XFormCastException(e.getMessage() + " check the constructor.");
        }
        for (String key : request.getParameterMap().keySet()) {
            try {
                Field field = clazz.getDeclaredField(key);
                Method method;
                if (field != null) {
                    method = clazz.getMethod("set" + key.substring(0, 1).toUpperCase() + key.substring(1), field.getType());
                    if (method != null) {
                        try {
                            Object[] values = request.getParameterMap().get(key);
                            if (values.length > 1) {
                                method.invoke(target, field.getType().cast(values));
                            } else {
                                if (field.getType() == int.class) {
                                    method.invoke(target, Integer.parseInt((String) values[0]));
                                } else if (field.getType() == double.class) {
                                    method.invoke(target, Double.parseDouble((String) values[0]));
                                } else if (field.getType() == long.class) {
                                    method.invoke(target, Long.parseLong((String) values[0]));
                                } else if (field.getType() == float.class) {
                                    method.invoke(target, Float.parseFloat((String) values[0]));
                                } else if (field.getType() == short.class) {
                                    method.invoke(target, Short.parseShort((String) values[0]));
                                } else if (field.getType() == byte.class) {
                                    method.invoke(target, Byte.parseByte((String) values[0]));
                                } else if (field.getType() == char.class) {
                                    method.invoke(target, ((String) values[0]).charAt(0));
                                } else if (field.getType() == boolean.class) {
                                    method.invoke(target, values[0].equals("true"));
                                } else {
                                    method.invoke(target, field.getType().cast(values[0]));
                                }
                            }
                        } catch (Exception e) {
                            throw new XFormCastException(e.getMessage());
                        }
                    }
                }
            } catch (NoSuchMethodException | NoSuchFieldException e) {
                throw new XFormCastException(e.getMessage() + " not found.");
            }
        }
        if (!((XForm) target).validate()) {
            throw new XFormCastException("validator not passed.");
        }
        return target;
    }
}
