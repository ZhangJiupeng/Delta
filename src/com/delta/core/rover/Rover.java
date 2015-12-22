package com.delta.core.rover;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;

@WebServlet(urlPatterns = "*.rover")
public class Rover extends HttpServlet implements ActionServlet {
    public static boolean handleError = false;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    }

    @Override
    public String doAction(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Method method = ActionMapping.match(req);
        if (method == null) {
            resp.sendError(404);
            return null;
        } else {
            return ActionMapping.doAction(method, req, resp);
        }
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String direct = doAction(req, resp);
        if (direct != null) {
            if (direct.startsWith("redirect:")) {
                resp.sendRedirect(direct.substring("redirect:".length()));
            } else if (direct.startsWith("chain:")) {
                String path = req.getRequestURI();
                resp.sendRedirect(path.substring(0, path.lastIndexOf('/'))
                        + direct.substring("chain:".length()));
            } else {
                req.getRequestDispatcher(direct).forward(req, resp);
            }
        }
        super.service(req, resp);
    }
}
