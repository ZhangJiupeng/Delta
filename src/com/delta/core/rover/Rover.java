package com.delta.core.rover;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.RequestWrapper;
import java.io.IOException;
import java.lang.reflect.Method;

@WebServlet(urlPatterns = "*.rover")
public class Rover extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("protected void service()");
        String method = req.getMethod();
        if (method.equals("POST")) {
            System.out.println("Detect POST");
        } else if (method.equals("GET")) {
            System.out.println("Detect GET");
        }
        String direct = doAction(req, resp);
        if (direct != null) {
            if (direct.startsWith("redirect:")) {
                resp.sendRedirect(direct.substring(9));
            } else if (direct.startsWith("chain:")) {
                String path = req.getRequestURI();
                resp.sendRedirect(path.substring(0, path.lastIndexOf('/'))
                        + direct.substring(6));
            } else {
                req.getRequestDispatcher(direct).forward(req, resp);
            }
        }
        super.service(req, resp);
    }

    protected String doAction(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        System.out.println("Do Action method");
        Method method = ActionMapping.match(req);
        if (method == null) {
            resp.sendError(404);
            return null;
        } else {
            return ActionMapping.doAction(method, req, resp);
        }
    }
}
