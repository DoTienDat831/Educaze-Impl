package com.example.student_management.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "app_users")
public class AppUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String username;

    @Column(name = "password_hash", nullable = false, length = 100)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AppRole role;

    @Column(nullable = false)
    private boolean enabled = true;

    protected AppUser() {}

    public AppUser(String username, String passwordHash, AppRole role) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
        this.enabled = true;
    }

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getPasswordHash() { return passwordHash; }
    public AppRole getRole() { return role; }
    public boolean isEnabled() { return enabled; }
    public void setRole(AppRole role) { this.role = role; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
}
