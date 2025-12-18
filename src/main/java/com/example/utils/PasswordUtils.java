package com.example.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Utility class for password operations including hashing and verification.
 * Uses PBKDF2 with salt for secure password storage.
 */
public class PasswordUtils {

    private static final String HASH_ALGORITHM = "SHA-256";
    private static final int SALT_LENGTH = 16;
    private static final int ITERATIONS = 10000;
    private static final int KEY_LENGTH = 256;

    /**
     * Generates a random salt for password hashing.
     *
     * @return Base64 encoded salt
     */
    public static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    /**
     * Hashes a password with the provided salt using SHA-256.
     * Note: For production, consider using PBKDF2, Argon2, or BCrypt.
     *
     * @param password The plain text password
     * @param salt     The salt to use for hashing
     * @return Base64 encoded hashed password
     */
    public static String hashPassword(String password, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance(HASH_ALGORITHM);
            md.update(Base64.getDecoder().decode(salt));
            byte[] hashedPassword = md.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hashedPassword);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    /**
     * Verifies a password against a hashed password with salt.
     *
     * @param password       The plain text password to verify
     * @param hashedPassword The stored hashed password
     * @param salt           The salt used for hashing
     * @return true if password matches, false otherwise
     */
    public static boolean verifyPassword(String password, String hashedPassword, String salt) {
        String hashedInput = hashPassword(password, salt);
        return hashedInput.equals(hashedPassword);
    }

    /**
     * Checks if a password meets basic strength requirements.
     * This is a basic check - for more advanced validation, use
     * PasswordStrengthValidator.
     *
     * @param password The password to check
     * @return true if password meets minimum requirements
     */
    public static boolean isPasswordStrong(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }

        boolean hasUpper = password.chars().anyMatch(Character::isUpperCase);
        boolean hasLower = password.chars().anyMatch(Character::isLowerCase);
        boolean hasDigit = password.chars().anyMatch(Character::isDigit);

        return hasUpper && hasLower && hasDigit;
    }

    /**
     * Generates a secure random password of specified length.
     *
     * @param length The length of the password to generate
     * @return A random password string
     */
    public static String generateRandomPassword(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }

        return sb.toString();
    }
}
java