package com.delta.core.rover;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface ActionServlet {
    String doAction(HttpServletRequest req, HttpServletResponse resp) throws IOException;
}
