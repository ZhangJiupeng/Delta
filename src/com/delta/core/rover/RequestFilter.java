package com.delta.core.rover;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(urlPatterns = "/*", dispatcherTypes = DispatcherType.REQUEST)
public class RequestFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // TODO
        try {
            ActionMapping.load(MyAction.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse resp = (HttpServletResponse) servletResponse;
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
