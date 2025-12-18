package com.example.beans;

import com.example.model.User;
import com.example.exception.BusinessException;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import org.omnifaces.util.Ajax;
import org.primefaces.PrimeFaces;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;
import org.primefaces.model.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.SessionScoped;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Named
@ViewScoped
public class UserBean implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = LoggerFactory.getLogger(UserBean.class);

    @Inject
    private UserService userService;

    @Inject
    private SessionBean sessionBean;

    @Inject
    private PasswordValidator passwordValidator;

    // User properties
    private Long userId;

    @NotEmpty(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;

    @NotEmpty(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotEmpty(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    private String confirmPassword;

    @NotEmpty(message = "First name is required")
    private String firstName;

    @NotEmpty(message = "Last name is required")
    private String lastName;

    private boolean active = true;
    private List<String> roles = new ArrayList<>();
    private LocalDateTime createdDate;

    // UI properties
    private List<User> users;
    private User selectedUser;
    private List<User> selectedUsers;
    private String searchTerm;
    private boolean editMode = false;
    private LazyUserDataModel lazyModel;

    @PostConstruct
    public void init() {
        logger.info("Initializing UserBean for user: {}",
                sessionBean.getCurrentUser() != null ? sessionBean.getCurrentUser().getUsername() : "anonymous");
        resetForm();
        loadUsers();
        initializeLazyModel();
    }

    public void loadUsers() {
        try {
            if (searchTerm != null && !searchTerm.trim().isEmpty()) {
                users = userService.searchUsers(searchTerm);
            } else {
                users = userService.getAllUsers();
            }
        } catch (BusinessException e) {
            FacesUtils.addErrorMessage("Error loading users: " + e.getMessage());
            logger.error("Error loading users", e);
        }
    }

    public void saveUser() {
        try {
            validatePassword();

            User user;
            if (editMode) {
                user = userService.getUserById(userId);
                user.setEmail(email);
                user.setFirstName(firstName);
                user.setLastName(lastName);
                user.setActive(active);
                user.setRoles(roles);
                userService.updateUser(user);
                FacesUtils.addSuccessMessage("User updated successfully");
            } else {
                user = new User();
                user.setUsername(username);
                user.setEmail(email);
                user.setPassword(password);
                user.setFirstName(firstName);
                user.setLastName(lastName);
                user.setActive(active);
                user.setRoles(roles);
                userService.createUser(user);
                FacesUtils.addSuccessMessage("User created successfully");
            }

            resetForm();
            loadUsers();
            PrimeFaces.current().executeScript("PF('userDialog').hide()");
            PrimeFaces.current().ajax().update("userForm:messages", "userForm:usersTable");

        } catch (BusinessException e) {
            FacesUtils.addErrorMessage("Error saving user: " + e.getMessage());
            logger.error("Error saving user", e);
        } catch (Exception e) {
            FacesUtils.addErrorMessage("Unexpected error occurred");
            logger.error("Unexpected error saving user", e);
        }
    }

    public void deleteUser() {
        if (selectedUser != null) {
            try {
                // Prevent self-deletion
                if (selectedUser.getId().equals(sessionBean.getCurrentUser().getId())) {
                    FacesUtils.addErrorMessage("Cannot delete your own account");
                    return;
                }

                userService.deleteUser(selectedUser.getId());
                users.remove(selectedUser);
                selectedUser = null;
                FacesUtils.addSuccessMessage("User deleted successfully");
                PrimeFaces.current().ajax().update("userForm:messages", "userForm:usersTable");
            } catch (BusinessException e) {
                FacesUtils.addErrorMessage("Error deleting user: " + e.getMessage());
                logger.error("Error deleting user", e);
            }
        }
    }

    public void deleteSelectedUsers() {
        if (selectedUsers != null && !selectedUsers.isEmpty()) {
            try {
                userService.deleteUsers(selectedUsers);
                users.removeAll(selectedUsers);
                int deletedCount = selectedUsers.size();
                selectedUsers = null;
                FacesUtils.addSuccessMessage(deletedCount + " users deleted successfully");
                PrimeFaces.current().ajax().update("userForm:messages", "userForm:usersTable");
            } catch (BusinessException e) {
                FacesUtils.addErrorMessage("Error deleting users: " + e.getMessage());
                logger.error("Error deleting users", e);
            }
        }
    }

    public void editUser(User user) {
        this.userId = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.active = user.isActive();
        this.roles = new ArrayList<>(user.getRoles());
        this.createdDate = user.getCreatedDate();
        this.editMode = true;
    }

    public void resetForm() {
        this.userId = null;
        this.username = "";
        this.email = "";
        this.password = "";
        this.confirmPassword = "";
        this.firstName = "";
        this.lastName = "";
        this.active = true;
        this.roles = new ArrayList<>();
        this.createdDate = null;
        this.editMode = false;
    }

    private void validatePassword() {
        if (!editMode && !password.equals(confirmPassword)) {
            throw new BusinessException("Passwords do not match");
        }

        if (!editMode && !passwordValidator.isValid(password, null)) {
            throw new BusinessException("Password does not meet security requirements");
        }
    }

    private void initializeLazyModel() {
        lazyModel = new LazyUserDataModel(userService);
    }

    // Action listeners
    public void onRowSelect() {
        logger.info("User selected: {}", selectedUser.getUsername());
    }

    public void onRowUnselect() {
        selectedUser = null;
    }

    public void handleSearch(ActionEvent event) {
        loadUsers();
        Ajax.update("userForm:usersTable");
    }

    // Getters and Setters
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

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

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public User getSelectedUser() {
        return selectedUser;
    }

    public void setSelectedUser(User selectedUser) {
        this.selectedUser = selectedUser;
    }

    public List<User> getSelectedUsers() {
        return selectedUsers;
    }

    public void setSelectedUsers(List<User> selectedUsers) {
        this.selectedUsers = selectedUsers;
    }

    public String getSearchTerm() {
        return searchTerm;
    }

    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }

    public boolean isEditMode() {
        return editMode;
    }

    public void setEditMode(boolean editMode) {
        this.editMode = editMode;
    }

    public LazyUserDataModel getLazyModel() {
        return lazyModel;
    }

    // Lazy Loading DataModel
    private class LazyUserDataModel extends LazyDataModel<User> {
        private final UserService userService;

        public LazyUserDataModel(UserService userService) {
            this.userService = userService;
        }

        @Override
        public List<User> load(int first, int pageSize,
                Map<String, SortMeta> sortBy, Map<String, FilterMeta> filters) {
            try {
                // Extract sort field and order
                String sortField = null;
                SortOrder sortOrder = SortOrder.UNSORTED;
                if (!sortBy.isEmpty()) {
                    SortMeta sortMeta = sortBy.values().iterator().next(); // Assuming single sort
                    sortField = sortMeta.getField();
                    sortOrder = sortMeta.getOrder();
                }

                // Convert PrimeFaces filters to service layer filters
                Map<String, String> serviceFilters = convertFilters(filters);

                // Get data from service
                List<User> data = userService.getUsersLazy(first, pageSize,
                        sortField, sortOrder,
                        serviceFilters);

                // Set row count
                int rowCount = userService.getUserCount(serviceFilters);
                this.setRowCount(rowCount);

                return data;
            } catch (BusinessException e) {
                logger.error("Error loading lazy data", e);
                return new ArrayList<>();
            }
        }

        @Override
        public User getRowData(String rowKey) {
            try {
                Long id = Long.valueOf(rowKey);
                return userService.getUserById(id);
            } catch (NumberFormatException | BusinessException e) {
                return null;
            }
        }

        @Override
        public String getRowKey(User user) {
            return user.getId().toString();
        }

        private Map<String, String> convertFilters(Map<String, FilterMeta> primeFilters) {
            Map<String, String> filters = new HashMap<>();
            for (Map.Entry<String, FilterMeta> entry : primeFilters.entrySet()) {
                FilterMeta meta = entry.getValue();
                if (meta.getFilterValue() != null) {
                    filters.put(entry.getKey(), meta.getFilterValue().toString());
                }
            }
            return filters;
        }
    }
}