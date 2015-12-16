package com.delta.core.rover;

import com.delta.core.assembler.Assembler;
import com.delta.core.rover.except.IllegalControllerException;
import com.test.action.PlayAction;
import com.test.action.TestAction;
import com.test.dao.impl.TestDaoImpl;
import com.test.service.impl.TestServiceImpl;

public class Initializer {

    // TODO 这里的Controller（Action）类地址、Action-Service映射、Service-Dao映射均从配置文件中读取

    // 在Action里注入Service的实现
    private static void initActions() throws Exception{

        try {
            ActionMapping.load(PlayAction.class);
            ActionMapping.load(TestAction.class);
        } catch (IllegalControllerException e) {
            e.printStackTrace();
            System.exit(-1);
        }

        ActionMapping.controllers.put(PlayAction.class.getName(), PlayAction.class.newInstance());

//        Object action = TestAction.class.newInstance();
//        action = Assembler.detach(action, TestServiceImpl.class);
//        ActionMapping.controllers.put(TestAction.class.getName(), action);

        Object service = TestServiceImpl.class.newInstance();
        service = Assembler.detach(service, TestDaoImpl.class);
        Object action = TestAction.class.newInstance();
        action = Assembler.detach(action, service);
        ActionMapping.controllers.put(action.getClass().getName(), action);

    }

    // 在ServiceImpl里注入Dao的实现
    private static void initServiceImpl() {

    }

    public static void doInit() throws Exception {
        initActions();
        initServiceImpl();
    }
}
