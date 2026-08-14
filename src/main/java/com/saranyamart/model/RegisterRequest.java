package com.saranyamart.model;

/**
 * Data Transfer Object (DTO) for user registration requests.
 */
public class RegisterRequest {
    private String fullName;
    private String email;
    private String password;
    private Role role;

    public RegisterRequest() {}

    public RegisterRequest(String fullName, String email, String password, Role role) {
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.role = role;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    /**
     * Validates input fields for registration.
     */
    public boolean isValid() {
        return fullName != null && !fullName.trim().isEmpty() &&
               email != null && email.contains("@") &&
               password != null && password.length() >= 6 &&
               role != null && role != Role.ADMIN; // Admin cannot be registered publicly
    }
}
