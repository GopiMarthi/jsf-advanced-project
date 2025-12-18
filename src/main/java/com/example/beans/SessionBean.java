package com.example.beans;

import com.example.model.User;
import jakarta.enterprise.context.SessionScoped;
import java.io.Serializable;

import javax.faces.event.NamedEvent;

@NamedEvent
@SessionScoped
public class SessionBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private User currentUser;

	public User getCurrentUser() {
		return currentUser;
	}

	public void setCurrentUser(User currentUser) {
		this.currentUser = currentUser;
	}

	public boolean isLoggedIn() {
		return currentUser != null;
	}

	public void logout() {
		currentUser = null;
	}
}
