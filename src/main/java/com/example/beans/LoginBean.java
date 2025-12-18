package com.example.beans;

import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.logging.Logger;

import com.example.model.User;
import com.example.utils.AuthenticationUtils;
import com.example.utils.FacesUtils;

@Named
@SessionScoped
public class LoginBean implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(LoginBean.class.getName());

    private String username;
    private String password;
    private boolean rememberMe = false;
    private String redirectUrl;
    private String environment = "Development";
    private boolean showEnv = true;

    public void init() {
        logger.info("LoginBean initialized");

        // Check for remember me cookie
        String rememberedUsername = AuthenticationUtils.getRememberMeCookie();
        if (rememberedUsername != null) {
            this.username = rememberedUsername;
            this.rememberMe = true;
        }
    }

    public String login() {
        logger.info("Login attempt for user: " + username);

        try {
            // Authenticate user
            User authenticatedUser = AuthenticationUtils.authenticate(username, password);

            if (authenticatedUser != null) {
                // Login successful
                AuthenticationUtils.loginUser(authenticatedUser);

                // Handle remember me
                if (rememberMe) {
                    AuthenticationUtils.setRememberMeCookie(username);
                }

                FacesUtils
                        .addSuccessMessage("Login successful! Welcome back, " + authenticatedUser.getFirstName() + "!");

                // Simulate login process delay
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }

                return "dashboard?faces-redirect=true";
            } else {
                // Login failed
                FacesUtils.addErrorMessage("Invalid username or password");
                return null;
            }
        } catch (Exception e) {
            logger.severe("Error during login: " + e.getMessage());
            FacesUtils.addErrorMessage("Login failed due to system error");
            return null;
        }
    }

    public String logout() {
        AuthenticationUtils.logoutUser();
        FacesUtils.addInfoMessage("You have been logged out successfully");
        return "/login.xhtml?faces-redirect=true";
    }

    // Getters and Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isRememberMe() {
        return rememberMe;
    }

    public void setRememberMe(boolean rememberMe) {
        this.rememberMe = rememberMe;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public boolean isShowEnv() {
        return showEnv;
    }

    public void setShowEnv(boolean showEnv) {
        this.showEnv = showEnv;
    }
}