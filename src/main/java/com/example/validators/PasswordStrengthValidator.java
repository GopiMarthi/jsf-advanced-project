package com.example.validators;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.validator.FacesValidator;
import jakarta.faces.validator.Validator;
import jakarta.faces.validator.ValidatorException;

@FacesValidator("passwordStrengthValidator")
public class PasswordStrengthValidator implements Validator<Object> {

    @Override
    public void validate(FacesContext context, UIComponent component, Object value)
            throws ValidatorException {

        if (value == null) {
            return; // Allow @NotNull or required attribute to handle null values
        }

        String password = value.toString().trim(); // Trim whitespace for better validation

        // Check for minimum length, uppercase, lowercase, and digit
        if (password.length() < 8 ||
                !password.matches(".*[A-Z].*") ||
                !password.matches(".*[a-z].*") ||
                !password.matches(".*\\d.*")) {

            FacesMessage msg = new FacesMessage(
                    FacesMessage.SEVERITY_ERROR,
                    "Weak password",
                    "Password must be at least 8 characters and contain at least one uppercase letter, one lowercase letter, and one digit");
            throw new ValidatorException(msg);
        }
    }
}