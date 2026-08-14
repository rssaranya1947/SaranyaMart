package com.saranyamart.model;

/**
 * Java model representing a User in SaranyaMart.
 */
public class User {
    private int id;
    private String fullName;
    private String email;
    private String passwordHash;
    private Role role;
    private String createdAt;

    // Default Constructor
    public User() {}

    // Full Parameterized Constructor
    public User(int id, String fullName, String email, String passwordHash, Role role, String createdAt) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
        this.createdAt = createdAt;
    }

    // Constructor without ID (for creation)
    public User(String fullName, String email, String passwordHash, Role role) {
        this.fullName = fullName;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Helper to escape JSON string values.
     */
    private String escapeJson(String input) {
        if (input == null) return "";
        return input.replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\b", "\\b")
                    .replace("\f", "\\f")
                    .replace("\n", "\\n")
                    .replace("\r", "\\r")
                    .replace("\t", "\\t");
    }

    /**
     * Serializes User object to JSON string (excluding sensitive password hash).
     */
    public String toJson() {
        return String.format(
            "{\"id\":%d,\"fullName\":\"%s\",\"email\":\"%s\",\"role\":\"%s\",\"createdAt\":\"%s\"}",
            id,
            escapeJson(fullName),
            escapeJson(email),
            role != null ? role.getValue() : "buyer",
            escapeJson(createdAt != null ? createdAt : "")
        );
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", role=" + role +
                ", createdAt='" + createdAt + '\'' +
                '}';
    }
}
