package com.example.utils;

import java.util.logging.Logger;

import com.example.exceptions.BusinessException;
import com.example.model.User;
import com.example.services.UserService;

/**
 * Utility class for user registration operations.
 */
public class RegistrationUtils {

    private static final Logger logger = Logger.getLogger(RegistrationUtils.class.getName());

    /**
     * Validates registration data before creating a user.
     *
     * @param username        The username
     * @param email           The email address
     * @param password        The password
     * @param confirmPassword The password confirmation
     * @param firstName       The first name
     * @param lastName        The last name
     * @param acceptTerms     Whether terms are accepted
     * @param userService     The user service for database operations
     * @throws BusinessException if validation fails
     */
    public static void validateRegistrationData(String username, String email, String password,
            String confirmPassword, String firstName, String lastName, boolean acceptTerms,
            UserService userService) throws BusinessException {

        // Validate password confirmation
        if (!password.equals(confirmPassword)) {
            throw new BusinessException("Passwords do not match");
        }

        // Validate terms acceptance
        if (!acceptTerms) {
            throw new BusinessException("You must accept the terms and conditions");
        }

        // Check password strength
        if (!PasswordUtils.isPasswordStrong(password)) {
            throw new BusinessException("Password does not meet strength requirements");
        }

        // Check if username already exists
        if (userService.findByUsername(username) != null) {
            throw new BusinessException("Username already exists");
        }

        // Check if email already exists
        if (userService.findByEmail(email) != null) {
            throw new BusinessException("Email already exists");
        }

        logger.info("Registration data validation passed for user: " + username);
    }

    /**
     * Creates a new user with the provided registration data.
     *
     * @param username    The username
     * @param email       The email address
     * @param password    The password (will be hashed)
     * @param firstName   The first name
     * @param lastName    The last name
     * @param userService The user service for database operations
     * @return The created User object
     * @throws BusinessException if user creation fails
     */
    public static User createUser(String username, String email, String password,
            String firstName, String lastName, UserService userService) throws BusinessException {

        // Hash the password
        String salt = PasswordUtils.generateSalt();
        String hashedPassword = PasswordUtils.hashPassword(password, salt);

        // Create new user
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(hashedPassword); // Store hashed password
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setActive(true);

        // Note: In a real application, you might want to store the salt separately
        // For this demo, we'll store it as part of the password hash
        // In production, use proper password hashing libraries like BCrypt

        userService.createUser(user);

        logger.info("User created successfully: " + username);
        return user;
    }

    /**
     * Sends a welcome email to the newly registered user.
     * This is a placeholder - implement actual email sending logic.
     *
     * @param user The registered user
     */
    public static void sendWelcomeEmail(User user) {
        // Placeholder for email sending functionality
        // In a real application, you would integrate with an email service
        logger.info("Welcome email sent to: " + user.getEmail());

        // Example implementation:
        // EmailService.sendEmail(user.getEmail(), "Welcome!", "Welcome to our
        // application!");
    }

    /**
     * Generates a verification token for email verification.
     * This is a placeholder for email verification functionality.
     *
     * @return A random verification token
     */
    public static String generateVerificationToken() {
        return PasswordUtils.generateRandomPassword(32);
    }

    /**
     * Checks if a username is available for registration.
     *
     * @param username    The username to check
     * @param userService The user service
     * @return true if available, false if taken
     */
    public static boolean isUsernameAvailable(String username, UserService userService) {
        return userService.findByUsername(username) == null;
    }

    /**
     * Checks if an email is available for registration.
     *
     * @param email       The email to check
     * @param userService The user service
     * @return true if available, false if taken
     */
    public static boolean isEmailAvailable(String email, UserService userService) {
        return userService.findByEmail(email) == null;
    }

    /**
     * Sanitizes user input to prevent XSS attacks.
     * This is a basic implementation - consider using a proper HTML sanitizer.
     *
     * @param input The input to sanitize
     * @return Sanitized input
     */
    public static String sanitizeInput(String input) {
        if (input == null) {
            return null;
        }

        // Basic HTML escaping
        return input.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#x27;")
                .replace("/", "&#x2F;");
    }
}
java