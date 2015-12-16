package com.delta.core.rover;

import com.delta.core.assembler.Assembler;
import com.delta.core.rover.except.IllegalControllerException;
import com.test.action.PlayAction;
import com.test.action.TestAction;
import com.test.dao.impl.TestDaoImpl;
import com.test.service.impl.TestServiceImpl;

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

        Object dao;
        Object service;
        Object action;

        // load PlayAction
        action = PlayAction.class.newInstance();
        ActionMapping.controllers.put(action.getClass().getName(), action);

        // load TestAction
        dao = TestDaoImpl.class.newInstance();
        service = TestServiceImpl.class.newInstance();
        action = TestAction.class.newInstance();
        service = Assembler.detach(service, dao);   // 在ServiceImpl里注入Dao的实现
        action = Assembler.detach(action, service); // 在Action里注入Service的实现
        ActionMapping.controllers.put(action.getClass().getName(), action);

    }

    public static void doInit() throws Exception {
        initActionAndService();
    }
}
