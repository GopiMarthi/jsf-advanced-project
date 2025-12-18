package com.example.validators;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.validator.FacesValidator;
import jakarta.faces.validator.Validator;
import jakarta.faces.validator.ValidatorException;
import java.util.regex.Pattern;

@FacesValidator("emailValidator")
public class EmailValidator implements Validator<Object> {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,63}$");

    @Override
    public void validate(FacesContext context, UIComponent component, Object value)
            throws ValidatorException {

        if (value == null) {
            return; // let @NotNull / required handle this
        }

        String email = value.toString().trim();
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            FacesMessage msg = new FacesMessage(
                    FacesMessage.SEVERITY_ERROR,
                    "Invalid email address",
                    "Please enter a valid email address");
            throw new ValidatorException(msg);
        }
    }
}
