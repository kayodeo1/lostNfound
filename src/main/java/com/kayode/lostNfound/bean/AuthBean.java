package com.kayode.lostNfound.bean;

import java.io.IOException;
import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpSession;

import org.omnifaces.util.Messages;

import com.kayode.lostNfound.model.User;
import com.kayode.lostNfound.model.UserRole;
import com.kayode.lostNfound.service.UserService;

@Named("authBean")
@SessionScoped
public class AuthBean implements Serializable {

    private String loginEmail;
    private String loginPassword;
    private String regName;
    private String regEmail;
    private String regPassword;
    private String regConfirmPassword;
    private User loggedInUser;

    @Inject
    private UserService userService;

    public void login() {
        User user = userService.authenticate(loginEmail, loginPassword);
        if (user == null) {
            Messages.addGlobalError("Invalid email or password.");
            return;
        }
        loggedInUser = user;
        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        ((HttpSession) ec.getSession(true)).setAttribute("loggedInUser", user);
        try {
            String ctx = ec.getRequestContextPath();
            if (user.getRole() == UserRole.ADMIN) {
                ec.redirect(ctx + "/admin.xhtml");
            } else {
                ec.redirect(ctx + "/view2.xhtml");
            }
        } catch (IOException e) {
            Messages.addGlobalError("Redirect failed: " + e.getMessage());
        }
    }

    public void logout() {
        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        HttpSession session = (HttpSession) ec.getSession(false);
        if (session != null) session.invalidate();
        loggedInUser = null;
        try {
            ec.redirect(ec.getRequestContextPath() + "/login.xhtml");
        } catch (IOException e) {
            // ignore
        }
    }

    public void register() {
        if (!regPassword.equals(regConfirmPassword)) {
            Messages.addGlobalError("Passwords do not match.");
            return;
        }
        if (userService.emailExists(regEmail)) {
            Messages.addGlobalError("Email is already registered.");
            return;
        }
        User u = new User();
        u.setName(regName);
        u.setEmail(regEmail);
        u.setRole(UserRole.USER);
        userService.createUser(u, regPassword);
        Messages.addFlashGlobalInfo("Registration successful! Please log in.");
        try {
            FacesContext.getCurrentInstance().getExternalContext()
                .redirect(FacesContext.getCurrentInstance().getExternalContext()
                    .getRequestContextPath() + "/login.xhtml");
        } catch (IOException e) {
            Messages.addGlobalError("Redirect failed.");
        }
    }

    public void redirectIfLoggedIn() {
        if (loggedInUser == null) return;
        try {
            ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
            String ctx = ec.getRequestContextPath();
            if (loggedInUser.getRole() == UserRole.ADMIN) {
                ec.redirect(ctx + "/admin.xhtml");
            } else {
                ec.redirect(ctx + "/view2.xhtml");
            }
        } catch (IOException e) {
            // ignore
        }
    }

    public boolean isLoggedIn() { return loggedInUser != null; }
    public boolean isAdmin()    { return loggedInUser != null && loggedInUser.getRole() == UserRole.ADMIN; }
    public boolean isUser()     { return loggedInUser != null && loggedInUser.getRole() == UserRole.USER; }

    public User getLoggedInUser()           { return loggedInUser; }
    public void setLoggedInUser(User u)     { this.loggedInUser = u; }

    public String getLoginEmail()               { return loginEmail; }
    public void setLoginEmail(String v)         { this.loginEmail = v; }
    public String getLoginPassword()            { return loginPassword; }
    public void setLoginPassword(String v)      { this.loginPassword = v; }
    public String getRegName()                  { return regName; }
    public void setRegName(String v)            { this.regName = v; }
    public String getRegEmail()                 { return regEmail; }
    public void setRegEmail(String v)           { this.regEmail = v; }
    public String getRegPassword()              { return regPassword; }
    public void setRegPassword(String v)        { this.regPassword = v; }
    public String getRegConfirmPassword()       { return regConfirmPassword; }
    public void setRegConfirmPassword(String v) { this.regConfirmPassword = v; }
}
