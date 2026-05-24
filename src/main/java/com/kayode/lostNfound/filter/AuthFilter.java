package com.kayode.lostNfound.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.kayode.lostNfound.model.User;
import com.kayode.lostNfound.model.UserRole;

public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest  request  = (HttpServletRequest)  req;
        HttpServletResponse response = (HttpServletResponse) res;
        HttpSession         session  = request.getSession(false);

        String uri = request.getRequestURI();
        String ctx = request.getContextPath();

        // Always allow public resources
        if (isPublic(uri)) {
            chain.doFilter(req, res);
            return;
        }

        boolean loggedIn = session != null && session.getAttribute("loggedInUser") != null;

        if (!loggedIn) {
            response.sendRedirect(ctx + "/login.xhtml");
            return;
        }

        // Admin-only pages
        if (uri.contains("/admin.xhtml") || uri.contains("/list.xhtml")) {
            User user = (User) session.getAttribute("loggedInUser");
            if (user.getRole() != UserRole.ADMIN) {
                response.sendRedirect(ctx + "/view2.xhtml");
                return;
            }
        }

        chain.doFilter(req, res);
    }

    private boolean isPublic(String uri) {
        return uri.contains("/login.xhtml")
            || uri.contains("/register.xhtml")
            || uri.contains("/javax.faces.resource/")
            || uri.contains("/resources/")
            || uri.endsWith(".css")
            || uri.endsWith(".js")
            || uri.endsWith(".png")
            || uri.endsWith(".jpg")
            || uri.endsWith(".ico");
    }

    @Override public void init(FilterConfig c) {}
    @Override public void destroy() {}
}
