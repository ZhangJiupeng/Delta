package com.test.action;

import com.delta.core.rover.RequestMethod;
import com.delta.core.rover.annotation.Controller;
import com.delta.core.rover.annotation.RequestMapping;
import com.test.service.TestService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller(namespace = "/my")
public class PlayAction {

    @RequestMapping(patterns = "/login", method = RequestMethod.GET)
    public String doLogin(HttpServletRequest request, HttpServletResponse response) {
        request.setAttribute("a", 456);
        System.out.println("HAHAHa");
        return "/index.jsp";
    }

    @RequestMapping(patterns = {"/haha", "/hehe"})
    public String play(HttpServletRequest request, HttpServletResponse response) {
        try {
            response.getWriter().print("asd");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @RequestMapping(patterns = "/welcome")
    public String welcome(HttpServletRequest request, HttpServletResponse response) {
        try {
            response.getWriter().print("Good Morning Delta!");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
