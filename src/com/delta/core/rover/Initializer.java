package com.delta.core.rover;

import com.delta.core.assembler.Assembler;
import com.delta.core.rover.except.IllegalControllerException;
import com.test.action.PlayAction;
import com.test.action.TestAction;
import com.test.dao.impl.TestDaoImpl;
import com.test.interceptor.TestInterceptor;
import com.test.service.impl.TestServiceImpl;
import org.junit.Test;

public class Initializer {

    // TODO 这里的Controller（Action）类地址、Action-Service映射、Service-Dao映射均从配置文件中读取

    private static void initActionAndService() throws Exception {

        try {
            ActionMapping.load(PlayAction.class);
            ActionMapping.load(TestAction.class);
        } catch (IllegalControllerException e) {
            e.printStackTrace();
            System.exit(-1);
        }

        Object action;
        Object actionInterceptor;
        Object serviceImpl;
        Object daoImpl;

        // load PlayAction
        action = PlayAction.class.newInstance();
        ActionMapping.controllers.put(action.getClass().getName(), action);

        // load TestAction
        Class<?> actionClass = TestAction.class;
        Class<?> actionInterceptorClass = TestInterceptor.class;
        Class<?> serviceImplClass = TestServiceImpl.class;
        Class<?> daoImplClass = TestDaoImpl.class;

        daoImpl = daoImplClass.newInstance();
        serviceImpl = serviceImplClass.newInstance();
        action = actionClass.newInstance();
        actionInterceptor = actionInterceptorClass.newInstance();

        // 注入实现
        serviceImpl = Assembler.detach(serviceImpl, daoImpl);   // 在ServiceImpl里注入Dao的实现
        action = Assembler.detach(action, serviceImpl);         // 在Action里注入ServiceImpl
        ActionMapping.controllers.put(action.getClass().getName(), action);

        // 注册Action拦截器
        ActionMapping.interceptors.put(actionClass, (ActionInterceptor) actionInterceptor);
    }

    @Test
    public void test() throws Exception {
        Initializer.doInit();
    }

    public static void doInit() throws Exception {
        initActionAndService();
    }
}
