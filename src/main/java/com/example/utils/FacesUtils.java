package com.example.utils;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.context.ExternalContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Utility class for JSF Faces operations.
 */
public class FacesUtils {

    private static final Logger logger = Logger.getLogger(FacesUtils.class.getName());

    // Message methods
    public static void addErrorMessage(String message) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", message));
    }

    public static void addSuccessMessage(String message) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", message));
    }

    public static void addWarningMessage(String message) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_WARN, "Warning", message));
    }

    public static void addInfoMessage(String message) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", message));
    }

    // Request/Response utilities
    public static HttpServletRequest getRequest() {
        return (HttpServletRequest) FacesContext.getCurrentInstance()
                .getExternalContext().getRequest();
    }

    public static HttpServletResponse getResponse() {
        return (HttpServletResponse) FacesContext.getCurrentInstance()
                .getExternalContext().getResponse();
    }

    public static String getRequestParameter(String name) {
        Map<String, String> params = FacesContext.getCurrentInstance()
                .getExternalContext().getRequestParameterMap();
        return params.get(name);
    }

    public static void redirect(String url) throws IOException {
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        externalContext.redirect(externalContext.getRequestContextPath() + url);
    }

    // Navigation utilities
    public static String redirectTo(String outcome) {
        return outcome + "?faces-redirect=true";
    }

    // Validation utilities
    public static boolean isPostback() {
        return FacesContext.getCurrentInstance().isPostback();
    }

    public static boolean isValidationFailed() {
        return FacesContext.getCurrentInstance().isValidationFailed();
    }

    // Session utilities
    public static Object getSessionAttribute(String key) {
        return FacesContext.getCurrentInstance().getExternalContext()
                .getSessionMap().get(key);
    }

    public static void setSessionAttribute(String key, Object value) {
        FacesContext.getCurrentInstance().getExternalContext()
                .getSessionMap().put(key, value);
    }

    public static void removeSessionAttribute(String key) {
        FacesContext.getCurrentInstance().getExternalContext()
                .getSessionMap().remove(key);
    }

    // Application utilities
    public static Object getApplicationAttribute(String key) {
        return FacesContext.getCurrentInstance().getExternalContext()
                .getApplicationMap().get(key);
    }

    public static void setApplicationAttribute(String key, Object value) {
        FacesContext.getCurrentInstance().getExternalContext()
                .getApplicationMap().put(key, value);
    }

    // Flash scope utilities
    public static Object getFlashAttribute(String key) {
        return FacesContext.getCurrentInstance().getExternalContext()
                .getFlash().get(key);
    }

    public static void setFlashAttribute(String key, Object value) {
        FacesContext.getCurrentInstance().getExternalContext()
                .getFlash().put(key, value);
    }

    // Context utilities
    public static String getContextPath() {
        return FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath();
    }

    public static String getRemoteUser() {
        return FacesContext.getCurrentInstance().getExternalContext().getRemoteUser();
    }

    public static boolean isUserInRole(String role) {
        return FacesContext.getCurrentInstance().getExternalContext().isUserInRole(role);
    }
}