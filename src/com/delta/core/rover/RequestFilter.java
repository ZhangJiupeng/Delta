package com.delta.core.rover;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@WebFilter(urlPatterns = "/*", dispatcherTypes = DispatcherType.REQUEST)
public class RequestFilter implements Filter {
    public static String characterSet = "UTF-8";
    public static String welcomePage = "";
    public static Set<String> blackList = new HashSet<>();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        try {
            Initializer.doInit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        // Set Character Encoding
        servletRequest.setCharacterEncoding(characterSet);
        servletResponse.setCharacterEncoding(characterSet);
        servletResponse.setContentType("text/html;charset=" + characterSet);

        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse resp = (HttpServletResponse) servletResponse;

        if (req.getRequestURI().equals("/") && !welcomePage.equals("/") && !welcomePage.equals("")) {
            resp.sendRedirect(welcomePage);
            return;
        }

        for (String forbidPath : blackList) {
            if (req.getRequestURI().contains(forbidPath)) {
                resp.sendError(404);
                return;
            }
        }

        if (!req.getRequestURI().contains(".")) {
            req.getRequestDispatcher(req.getRequestURI() + ".rover").forward(req, resp);
        } else {
            filterChain.doFilter(req, resp);
        }
    }

    @Override
    public void destroy() {
        ActionMapping.clear();
    }
}
