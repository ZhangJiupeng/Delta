package com.delta.core.rover;

import com.delta.core.rover.annotation.Controller;
import com.delta.core.rover.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller(namespace = "/my")
public class MyAction {
    @RequestMapping(pattern = "/login", method = RequestMethod.GET)
    public String doLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setAttribute("a", 456);
        System.out.println("HAHAHa");
//        response.getWriter().print("asd");
        return "chain:/hehe";
    }
    @RequestMapping(pattern = "/hehe")
    public String play(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("Hello Delta!");
        return "/index.jsp";
    }
}
