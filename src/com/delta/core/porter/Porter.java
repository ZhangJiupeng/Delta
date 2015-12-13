package com.delta.core.porter;

import com.delta.core.porter.annotation.Entity;
import com.delta.core.porter.annotation.Ignore;
import com.delta.core.porter.except.IllegalBeanEntityException;
import com.delta.core.util.JDBCUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * I am responsible for carrying data from your database<br/>
 * Some annotations need to be declared in your javaBean.
 *
 * @author Jim Zhang
 * @version 1.2
 * @see com.delta.core.porter.annotation.Entity
 * @see com.delta.core.porter.annotation.Ignore
 * @see com.delta.core.porter.annotation.TreatAs
 * @since 1.8
 */
public class Porter {

    private Porter(){}

    /**
     * Mapping to table and load beans for you.
     *
     * @param clazz YourEntityJavaBean.class.
     * @return a bean list.
     * @throws IllegalBeanEntityException
     */
    public static <T> List<T> loadBeans(Class<T> clazz) throws IllegalBeanEntityException {
        return loadBeans(clazz, null);
    }

    /**
     * Mapping to table and load beans for you.
     *
     * @param clazz      YourEntityJavaBean.class.
     * @param conditions statement after call where.
     * @param obj        the parameters corresponding to ?.
     * @return a bean list.
     * @throws IllegalBeanEntityException
     */
    public static <T> List<T> loadBeans(Class<T> clazz, String conditions, Object... obj)
            throws IllegalBeanEntityException {
        if (clazz.getAnnotation(Entity.class) == null) {
            throw new IllegalBeanEntityException();
        }
        List<T> resultList = new ArrayList<>();
        try {
            ResultSet rs = JDBCUtil.executeQuery(
                    buildQuery(clazz.getAnnotation(Entity.class).value(), conditions), obj);
            while (rs.next()) {
                T target = clazz.newInstance();
                for (Method method : clazz.getDeclaredMethods()) {
                    if (method.getAnnotation(Ignore.class) != null) {
                        continue;
                    }
                    if (method.getName().startsWith("set") && method.getReturnType() == void.class) {
                        String propName = method.getName().substring(3, 4).toLowerCase()
                                + method.getName().substring(4);
                        Class paramType = method.getParameterTypes()[0];
                        if (paramType == byte.class)
                            method.invoke(target, rs.getByte(propName));
                        else if (paramType == short.class)
                            method.invoke(target, rs.getShort(propName));
                        else if (paramType == int.class)
                            method.invoke(target, rs.getInt(propName));
                        else if (paramType == long.class)
                            method.invoke(target, rs.getLong(propName));
                        else if (paramType == float.class)
                            method.invoke(target, rs.getFloat(propName));
                        else if (paramType == double.class)
                            method.invoke(target, rs.getDouble(propName));
                        else if (paramType == char.class)
                            method.invoke(target, rs.getString(propName).charAt(0));
                        else if (paramType == boolean.class)
                            method.invoke(target, rs.getBoolean(propName));
                        else
                            method.invoke(target, paramType.cast(rs.getObject(propName)));
                    }
                }
                resultList.add(target);
            }
            rs.close();
        } catch (SQLException | IllegalAccessException e) {
            throw new IllegalBeanEntityException(e.getMessage());
        } catch (InvocationTargetException | InstantiationException e) {
            e.printStackTrace();
        }
        return resultList;
    }

    /**
     * Save your bean into database.
     *
     * @param t your bean needs to be saved.
     * @return true if the database changed.
     * @throws IllegalBeanEntityException
     */
    public static <T> boolean saveBean(T t) throws IllegalBeanEntityException {
        if (t.getClass().getAnnotation(Entity.class) == null) {
            throw new IllegalBeanEntityException();
        }
        Map<String, Object> attrMap = getParametersPair(t);
        Set<String> paraNames = attrMap.keySet();
        return paraNames.size() != 0 && JDBCUtil.executeUpdate(buildInsert(t.getClass().getAnnotation(Entity.class).value(),
                paraNames.toArray(new String[paraNames.size()])), attrMap.values().toArray()) > 0;
    }

    public static <T> int updateBeans(T t, String conditions) throws IllegalBeanEntityException {
        if (t.getClass().getAnnotation(Entity.class) == null) {
            throw new IllegalBeanEntityException();
        }
        Map<String, Object> attrMap = getParametersPair(t);
        Set<String> paraNames = attrMap.keySet();
        return JDBCUtil.executeUpdate(buildUpdate(t.getClass().getAnnotation(Entity.class).value(),
                conditions, paraNames.toArray(new String[paraNames.size()])), attrMap.values().toArray());
    }

    /**
     * Remove beans from your db_table.
     *
     * @param clazz      YourEntityJavaBean.class.
     * @param conditions statement after call where.
     * @return count for how many rows updated.
     * @throws IllegalBeanEntityException
     */
    public static <T> int removeBeans(Class<T> clazz, String conditions, Object... obj) throws IllegalBeanEntityException {
        if (clazz.getAnnotation(Entity.class) == null) {
            throw new IllegalBeanEntityException();
        }
        return JDBCUtil.executeUpdate("delete from " + clazz.getAnnotation(Entity.class).value()
                + " where " + conditions, obj);
    }

    private static <T> Map<String, Object> getParametersPair(T t) throws IllegalBeanEntityException {
        Map<String, Object> attrMap = new HashMap<>();
        for (Method method : t.getClass().getDeclaredMethods()) {
            if (method.getAnnotation(Ignore.class) != null) {
                continue;
            }
            if (method.getName().startsWith("get") && method.getReturnType() != void.class) {
                try {
                    String key = method.getName().substring(3, 4).toLowerCase()
                            + method.getName().substring(4);
                    Object value = method.invoke(t);
                    attrMap.put(key, value);
                } catch (IllegalAccessException e) {
                    throw new IllegalBeanEntityException(e.getMessage());
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
        return attrMap;
    }

    private static String buildQuery(String tableName, String conditions) {
        if (conditions != null) {
            return "select * from " + tableName + " where " + conditions;
        } else {
            return "select * from " + tableName;
        }
    }

    private static String buildInsert(String tableName, String... paraNames) {
        if (paraNames.length == 0) {
            return "";
        }
        StringBuilder sbd = new StringBuilder();
        sbd.append("insert into ").append(tableName).append(" (");
        int i = 0;
        for (; i < paraNames.length - 1; i++) sbd.append(paraNames[i]).append(", ");
        sbd.append(paraNames[i]).append(") values (");
        for (; i > 0; i--) sbd.append("?, ");
        return sbd.append("?)").toString();
    }

    private static String buildUpdate(String tableName, String conditions, String... paraNames) {
        if (paraNames.length == 0) {
            return "";
        }
        StringBuilder sbd = new StringBuilder();
        sbd.append("update ").append(tableName).append(" set ");
        int i = 0;
        for (; i < paraNames.length - 1; i++) sbd.append(paraNames[i]).append(" = ?, ");
        sbd.append(paraNames[i]).append(" = ? ");
        if (conditions != null && !conditions.trim().equals("")) {
            sbd.append("where ").append(conditions);
        }
        return sbd.toString();
    }
}
