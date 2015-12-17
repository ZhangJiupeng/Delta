package com.test.service.impl;

import com.delta.core.assembler.annotation.Detachable;
import com.test.dao.TestDao;
import com.test.service.TestService;

public class TestServiceImpl implements TestService {

    private TestDao testDao;

    @Detachable
    public void setTestDao(TestDao testDao) {
        this.testDao = testDao;
    }

    @Override
    public void testService() {
        System.out.println("this is my testService.");
        testDao.test();
    }
}
