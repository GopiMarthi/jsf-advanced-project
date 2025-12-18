package com.example.model;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = "username"),
        @UniqueConstraint(columnNames = "email")
})
@NamedQueries({
        @NamedQuery(name = "User.findByUsername", query = "SELECT u FROM User u WHERE u.username = :username"),
        @NamedQuery(name = "User.findByEmail", query = "SELECT u FROM User u WHERE u.email = :email"),
        @NamedQuery(name = "User.findActiveUsers", query = "SELECT u FROM User u WHERE u.active = true"),
        @NamedQuery(name = "User.search", query = "SELECT u FROM User u WHERE " +
                "LOWER(u.username) LIKE LOWER(:term) OR " +
                "LOWER(u.email) LIKE LOWER(:term) OR " +
                "LOWER(u.firstName) LIKE LOWER(:term) OR " +
                "LOWER(u.lastName) LIKE LOWER(:term)")
})
@Cacheable
@org.hibernate.annotations.Cache(usage = org.hibernate.annotations.CacheConcurrencyStrategy.READ_WRITE)
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(min = 3, max = 50)
    @Column(nullable = false, unique = true)
    private String username;

    @NotNull
    @Email
    @Column(nullable = false, unique = true)
    private String email;

    @NotNull
    @Size(min = 8)
    @Column(nullable = false)
    private String password;

    @NotNull
    @Size(min = 2, max = 50)
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @NotNull
    @Size(min = 2, max = 50)
    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "is_active")
    private boolean active = true;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    private Set<String> roles = new HashSet<>();

    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @Column(name = "last_login_date")
    private LocalDateTime lastLoginDate;

    @Column(name = "failed_login_attempts")
    private int failedLoginAttempts = 0;

    @Column(name = "account_locked")
    private boolean accountLocked = false;

    @Version
    private Long version;

    @PrePersist
    protected void onCreate() {
        createdDate = LocalDateTime.now();
    }

    // Constructors, getters, setters, equals, hashCode
    // Builder pattern implementation
    // Validation methods
}