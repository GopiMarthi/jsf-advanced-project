package com.example.beans;

import com.example.exceptions.BusinessException;

public class PasswordValidator {

	public boolean isValid(String password, String confirmPassword) throws BusinessException {
		if (password == null || password.trim().isEmpty()) {
			throw new BusinessException("Password is required");
		}
		if (password.length() < 8) {
			throw new BusinessException("Password must be at least 8 characters");
		}
		if (!password.equals(confirmPassword)) {
			throw new BusinessException("Passwords do not match");
		}
		// Add more validation rules as needed
		return true;
	}
}
