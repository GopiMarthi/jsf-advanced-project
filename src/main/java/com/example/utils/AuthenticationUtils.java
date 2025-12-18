package com.example.utils;

import java.util.logging.Logger;

import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.example.model.User;

/**
 * Utility class for authentication-related operations.
 */
public class AuthenticationUtils {

    private static final Logger logger = Logger.getLogger(AuthenticationUtils.class.getName());
    private static final String USER_SESSION_KEY = "currentUser";
    private static final String REMEMBER_ME_COOKIE = "rememberMe";
    private static final int REMEMBER_ME_MAX_AGE = 30 * 24 * 60 * 60; // 30 days

    /**
     * Authenticates a user with username and password.
     *
     * @param username The username
     * @param password The password
     * @return User object if authentication successful, null otherwise
     */
    public static User authenticate(String username, String password) {
        // This is a placeholder - in real implementation, this would check against
        // database
        // For demo purposes, using hardcoded credentials
        if ("admin".equals(username) && "admin123".equals(password)) {
            User user = new User();
            user.setUsername(username);
            user.setEmail("admin@example.com");
            user.setFirstName("Admin");
            user.setLastName("User");
            user.setActive(true);
            return user;
        }
        return null;
    }

    /**
     * Logs in a user by storing user information in session.
     *
     * @param user The authenticated user
     */
    public static void loginUser(User user) {
        HttpSession session = getSession();
        session.setAttribute(USER_SESSION_KEY, user);
        logger.info("User logged in: " + user.getUsername());
    }

    /**
     * Logs out the current user by invalidating the session.
     */
    public static void logoutUser() {
        HttpSession session = getSession();
        if (session != null) {
            User currentUser = getCurrentUser();
            if (currentUser != null) {
                logger.info("User logged out: " + currentUser.getUsername());
            }
            session.invalidate();
        }

        // Clear remember me cookie
        clearRememberMeCookie();
    }

    /**
     * Gets the current logged-in user from session.
     *
     * @return Current user or null if not logged in
     */
    public static User getCurrentUser() {
        HttpSession session = getSession();
        if (session != null) {
            return (User) session.getAttribute(USER_SESSION_KEY);
        }
        return null;
    }

    /**
     * Checks if a user is currently logged in.
     *
     * @return true if user is logged in, false otherwise
     */
    public static boolean isUserLoggedIn() {
        return getCurrentUser() != null;
    }

    /**
     * Sets a remember me cookie for the user.
     *
     * @param username The username to remember
     */
    public static void setRememberMeCookie(String username) {
        HttpServletResponse response = getResponse();
        if (response != null) {
            Cookie cookie = new Cookie(REMEMBER_ME_COOKIE, username);
            cookie.setMaxAge(REMEMBER_ME_MAX_AGE);
            cookie.setPath("/");
            cookie.setHttpOnly(true); // Security: prevent JavaScript access
            cookie.setSecure(isSecureConnection()); // Use HTTPS in production
            response.addCookie(cookie);
        }
    }

    /**
     * Gets the remember me cookie value.
     *
     * @return Username from cookie or null if not present
     */
    public static String getRememberMeCookie() {
        HttpServletRequest request = getRequest();
        if (request != null) {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (REMEMBER_ME_COOKIE.equals(cookie.getName())) {
                        return cookie.getValue();
                    }
                }
            }
        }
        return null;
    }

    /**
     * Clears the remember me cookie.
     */
    public static void clearRememberMeCookie() {
        HttpServletResponse response = getResponse();
        if (response != null) {
            Cookie cookie = new Cookie(REMEMBER_ME_COOKIE, "");
            cookie.setMaxAge(0);
            cookie.setPath("/");
            response.addCookie(cookie);
        }
    }

    /**
     * Redirects to login page if user is not authenticated.
     *
     * @return Navigation outcome for login page
     */
    public static String redirectToLoginIfNotAuthenticated() {
        if (!isUserLoggedIn()) {
            return "/login?faces-redirect=true";
        }
        return null;
    }

    /**
     * Gets the HttpServletRequest from FacesContext.
     *
     * @return HttpServletRequest or null if not available
     */
    private static HttpServletRequest getRequest() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        if (facesContext != null) {
            ExternalContext externalContext = facesContext.getExternalContext();
            if (externalContext != null) {
                return (HttpServletRequest) externalContext.getRequest();
            }
        }
        return null;
    }

    /**
     * Gets the HttpServletResponse from FacesContext.
     *
     * @return HttpServletResponse or null if not available
     */
    private static HttpServletResponse getResponse() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        if (facesContext != null) {
            ExternalContext externalContext = facesContext.getExternalContext();
            if (externalContext != null) {
                return (HttpServletResponse) externalContext.getResponse();
            }
        }
        return null;
    }

    /**
     * Gets the HttpSession from FacesContext.
     *
     * @return HttpSession or null if not available
     */
    private static HttpSession getSession() {
        HttpServletRequest request = getRequest();
        if (request != null) {
            return request.getSession(false);
        }
        return null;
    }

    /**
     * Checks if the current connection is secure (HTTPS).
     *
     * @return true if HTTPS, false otherwise
     */
    private static boolean isSecureConnection() {
        HttpServletRequest request = getRequest();
        return request != null && request.isSecure();
    }
}
java