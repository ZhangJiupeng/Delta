package com.test.action;

import com.delta.core.assembler.annotation.Detachable;
import com.delta.core.rover.annotation.Controller;
import com.delta.core.rover.annotation.RequestMapping;
import com.test.service.TestService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
public class TestAction {
    private TestService testService;

    @Detachable
    public void setTestService(TestService testService) {
        this.testService = testService;
    }

    public TestService getTestService() {
        return testService;
    }

    @RequestMapping(patterns = "/test")
    public String testService(HttpServletRequest request, HttpServletResponse response) {
        testService.testService();
        try {
            response.getWriter().write("Hello, this is testService");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
