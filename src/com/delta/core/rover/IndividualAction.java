package com.delta.core.rover;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@FunctionalInterface
public interface IndividualAction {
    String doAction(HttpServletRequest request, HttpSession session);
}
