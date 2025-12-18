package com.example.beans;

import java.io.Serializable;

import jakarta.inject.Named;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.annotation.PostConstruct;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.utils.FacesUtils;
import com.example.utils.RegistrationUtils;

@Named
@ViewScoped
public class RegistrationBean implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = LoggerFactory.getLogger(RegistrationBean.class);

    @Inject
    private UserService userService;

    // Registration fields
    @NotEmpty(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;

    @NotEmpty(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotEmpty(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    @NotEmpty(message = "Confirm password is required")
    private String confirmPassword;

    @NotEmpty(message = "First name is required")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    private String firstName;

    @NotEmpty(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    private String lastName;

    // Terms and conditions
    private boolean acceptTerms;

    @PostConstruct
    public void init() {
        logger.info("Initializing RegistrationBean");
    }

    public String register() {
        try {
            // Validate registration data using utility
            RegistrationUtils.validateRegistrationData(username, email, password,
                    confirmPassword, firstName, lastName, acceptTerms, userService);

            // Create user using utility
            User newUser = RegistrationUtils.createUser(username, email, password,
                    firstName, lastName, userService);

            // Send welcome email (placeholder)
            RegistrationUtils.sendWelcomeEmail(newUser);

            FacesUtils.addSuccessMessage("Registration successful! Welcome " + newUser.getFirstName() +
                    "! Please check your email for verification instructions.");

            // Reset form
            reset();

            // Redirect to login page
            return FacesUtils.redirectTo("/login");

        } catch (BusinessException e) {
            FacesUtils.addErrorMessage("Registration failed: " + e.getMessage());
            logger.error("Error during registration", e);
            return null;
        } catch (Exception e) {
            FacesUtils.addErrorMessage("Unexpected error occurred during registration");
            logger.error("Unexpected error during registration", e);
            return null;
        }
    }

    public void reset() {
        username = null;
        email = null;
        password = null;
        confirmPassword = null;
        firstName = null;
        lastName = null;
        acceptTerms = false;
    }

    // Getters and Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public boolean isAcceptTerms() {
        return acceptTerms;
    }

    public void setAcceptTerms(boolean acceptTerms) {
        this.acceptTerms = acceptTerms;
    }
}