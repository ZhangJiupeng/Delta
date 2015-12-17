package com.test.action;

import com.delta.core.assembler.annotation.Detachable;
import com.delta.core.rover.XForm;
import com.delta.core.rover.XFormLoader;
import com.delta.core.rover.annotation.Controller;
import com.delta.core.rover.annotation.RequestMapping;
import com.delta.core.rover.except.XFormCastException;
import com.test.form.UserForm;
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

    @RequestMapping(patterns = "/form")
    public String formTest(HttpServletRequest request, HttpServletResponse response) {
        try {
            XForm userForm = XFormLoader.newInstance(UserForm.class, request);
            System.out.println(userForm);
            System.out.println(userForm.validate());
        } catch (XFormCastException e) {
            e.printStackTrace();
        }
        return null;
    }
}
