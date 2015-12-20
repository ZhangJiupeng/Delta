package com.delta.core.rover;

import com.delta.core.rover.except.XFormCastException;

import java.lang.reflect.Field;

public class XFormConverter {
    public static <T> T cast(XForm xForm, Class<T> entityClazz) throws XFormCastException {
        T entity;
        Class xFormClazz = xForm.getClass();
        try {
            entity = entityClazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new XFormCastException(e.getMessage() + " check the constructor.");
        }
        for (Field field : entityClazz.getDeclaredFields()) {
            try {
                if (xFormClazz.getDeclaredField(field.getName()) != null) {
                    try {
                        Field formField = xFormClazz.getDeclaredField(field.getName());
                        field.setAccessible(true);
                        formField.setAccessible(true);
                        Object value = formField.get(xForm);
                        if (value == null)
                            continue;
                        field.set(entity, value);
                    } catch (IllegalAccessException e) {
                        throw new XFormCastException(e.getMessage());
                    }
                }
            } catch (NoSuchFieldException ignored) {
            }
        }
        return entity;
    }
}
