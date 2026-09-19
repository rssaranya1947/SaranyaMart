package com.saranyamart.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.saranyamart.dao.UserDao;
import com.saranyamart.model.AuthResponse;
import com.saranyamart.model.LoginRequest;
import com.saranyamart.model.RegisterRequest;
import com.saranyamart.model.Role;
import com.saranyamart.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Spring Boot REST Controller handling user registration, login, and user management for SaranyaMart.
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class AuthController {

    private final UserDao userDao = new UserDao();

    /**
     * POST /api/register - User Registration
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        if (request == null || request.getFullName() == null || request.getEmail() == null || request.getPassword() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new AuthResponse(false, "All fields (fullName, email, password) are required."));
        }

        if (request.getPassword().length() < 6) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new AuthResponse(false, "Password must be at least 6 characters long."));
        }

        if (request.getRole() == Role.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new AuthResponse(false, "Admin role cannot be registered publicly."));
        }

        try {
            User newUser = userDao.createUser(request);
            String token = "SM_TOKEN_" + UUID.randomUUID().toString().replace("-", "");
            AuthResponse response = new AuthResponse(true, "Registration successful! Welcome to SaranyaMart.", newUser, token);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new AuthResponse(false, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new AuthResponse(false, "Database error during registration."));
        }
    }

    /**
     * POST /api/login - User Authentication
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        if (request == null || request.getEmail() == null || request.getPassword() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new AuthResponse(false, "Email and password are required."));
        }

        User authenticatedUser = userDao.authenticate(request.getEmail(), request.getPassword());

        if (authenticatedUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthResponse(false, "Invalid email or password."));
        }

        String token = "SM_TOKEN_" + UUID.randomUUID().toString().replace("-", "");
        AuthResponse response = new AuthResponse(true, "Login successful! Welcome back, " + authenticatedUser.getFullName(), authenticatedUser, token);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/users - Admin User Listing
     */
    @GetMapping("/users")
    public ResponseEntity<Map<String, Object>> getAllUsers() {
        List<User> users = userDao.getAllUsers();
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("total", users.size());
        response.put("users", users);
        return ResponseEntity.ok(response);
    }
}
