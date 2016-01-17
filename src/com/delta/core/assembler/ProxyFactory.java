package com.delta.core.assembler;

import com.delta.core.assembler.except.IllegalProxyTypeException;
import com.test.dao.TestDao;
import com.test.dao.impl.TestDaoImpl;
import org.junit.Test;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;

@SuppressWarnings("ALL")
public class ProxyFactory {

    public static <T> T getProxyInstance(Class<T> itf, Class<?> impl, Class<? extends AssemblerProxy> pxy) {
        boolean isImplements = itf == impl;
        for (Type type : impl.getGenericInterfaces())
            if (type == itf)
                isImplements = true;
        if (!isImplements) {
            throw new IllegalProxyTypeException("Class@param_1 must implements Interface@param_0");
        }
        try {
            return getProxyInstance(itf, impl, (AssemblerProxy) pxy.newInstance());
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> T getProxyInstance(Class<T> itf, Class<?> impl, AssemblerProxy assemblerProxy) {
        Object object = Proxy.newProxyInstance(assemblerProxy.getClass().getClassLoader(),
                new Class[]{itf}, (proxy, method, args) -> {
                    if (assemblerProxy == null) {
                        return method.invoke(impl.newInstance(), args);
                    }
                    assemblerProxy.before();
                    Object retVal = method.invoke(impl.newInstance(), args);
                    assemblerProxy.after();
                    return retVal;
                });
        return (T) object;
    }

    public static <T> T getProxyInstance(Class<T> itf, Class<?> impl, SingleProxy singleProxy) {
        Object object = Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(), new Class[]{itf}, (proxy, method, args) -> {
            if (singleProxy != null) {
                singleProxy.before();
            }
            return method.invoke(impl.newInstance(), args);
        });
        return (T) object;
    }

    @Test
    public void test() {
//        TestDao dao = ProxyFactory.getProxyInstance(TestDao.class, TestDaoImpl.class, () -> {
//            System.out.println("before dao.test()");
//        });
//        TestDao dao = new TestDaoImpl();
//        dao.test();
//        System.out.println(dao.getClass());
//        System.out.println((TestDao) dao);

        TestDao dao = (TestDao)Proxy.newProxyInstance(
                TestDao.class.getClassLoader()
                , new Class[]{TestDao.class}
                , new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        System.out.println(1);
                        Object returnValue = method.invoke(TestDaoImpl.class.newInstance(), args);
                        System.out.println(2);
                        return returnValue;
                    }
                });
        dao.test();
        System.out.println(dao.getClass());
    }

}
