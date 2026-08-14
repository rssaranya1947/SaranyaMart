package com.saranyamart.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.saranyamart.dao.UserDao;
import com.saranyamart.model.AuthResponse;
import com.saranyamart.model.LoginRequest;
import com.saranyamart.model.RegisterRequest;
import com.saranyamart.model.Role;
import com.saranyamart.model.User;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * High-performance Java HTTP REST API handler for SaranyaMart authentication & user endpoints.
 */
public class AuthHandler implements HttpHandler {

    private final UserDao userDao = new UserDao();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // Set CORS Headers for browser clients
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type, Authorization");

        String method = exchange.getRequestMethod();

        if ("OPTIONS".equalsIgnoreCase(method)) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        String path = exchange.getRequestURI().getPath();

        try {
            if ("POST".equalsIgnoreCase(method) && "/api/register".equals(path)) {
                handleRegister(exchange);
            } else if ("POST".equalsIgnoreCase(method) && "/api/login".equals(path)) {
                handleLogin(exchange);
            } else if ("GET".equalsIgnoreCase(method) && "/api/users".equals(path)) {
                handleGetUsers(exchange);
            } else {
                sendJsonResponse(exchange, 404, new AuthResponse(false, "Endpoint not found: " + path).toJson());
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendJsonResponse(exchange, 500, new AuthResponse(false, "Internal Server Error: " + e.getMessage()).toJson());
        }
    }

    /**
     * Handles POST /api/register
     */
    private void handleRegister(HttpExchange exchange) throws IOException {
        String body = readRequestBody(exchange);
        
        String fullName = extractJsonField(body, "fullName");
        String email = extractJsonField(body, "email");
        String password = extractJsonField(body, "password");
        String roleStr = extractJsonField(body, "role");

        Role role = Role.fromString(roleStr);

        if (fullName.isEmpty() || email.isEmpty() || password.isEmpty()) {
            sendJsonResponse(exchange, 400, new AuthResponse(false, "All fields (fullName, email, password) are required.").toJson());
            return;
        }

        if (password.length() < 6) {
            sendJsonResponse(exchange, 400, new AuthResponse(false, "Password must be at least 6 characters long.").toJson());
            return;
        }

        if (role == Role.ADMIN) {
            sendJsonResponse(exchange, 403, new AuthResponse(false, "Admin role cannot be registered publicly.").toJson());
            return;
        }

        RegisterRequest request = new RegisterRequest(fullName, email, password, role);

        try {
            User newUser = userDao.createUser(request);
            String token = "SM_TOKEN_" + UUID.randomUUID().toString().replace("-", "");
            AuthResponse response = new AuthResponse(true, "Registration successful! Welcome to SaranyaMart.", newUser, token);
            sendJsonResponse(exchange, 201, response.toJson());
        } catch (IllegalArgumentException e) {
            sendJsonResponse(exchange, 400, new AuthResponse(false, e.getMessage()).toJson());
        } catch (Exception e) {
            sendJsonResponse(exchange, 500, new AuthResponse(false, "Database error during registration.").toJson());
        }
    }

    /**
     * Handles POST /api/login
     */
    private void handleLogin(HttpExchange exchange) throws IOException {
        String body = readRequestBody(exchange);

        String email = extractJsonField(body, "email");
        String password = extractJsonField(body, "password");

        if (email.isEmpty() || password.isEmpty()) {
            sendJsonResponse(exchange, 400, new AuthResponse(false, "Email and password are required.").toJson());
            return;
        }

        User authenticatedUser = userDao.authenticate(email, password);

        if (authenticatedUser == null) {
            sendJsonResponse(exchange, 401, new AuthResponse(false, "Invalid email or password.").toJson());
            return;
        }

        String token = "SM_TOKEN_" + UUID.randomUUID().toString().replace("-", "");
        AuthResponse response = new AuthResponse(true, "Login successful! Welcome back, " + authenticatedUser.getFullName(), authenticatedUser, token);
        sendJsonResponse(exchange, 200, response.toJson());
    }

    /**
     * Handles GET /api/users (Admin user listing)
     */
    private void handleGetUsers(HttpExchange exchange) throws IOException {
        List<User> users = userDao.getAllUsers();
        
        StringBuilder json = new StringBuilder();
        json.append("{\"success\":true,\"total\":").append(users.size()).append(",\"users\":[");
        for (int i = 0; i < users.size(); i++) {
            json.append(users.get(i).toJson());
            if (i < users.size() - 1) {
                json.append(",");
            }
        }
        json.append("]}");

        sendJsonResponse(exchange, 200, json.toString());
    }

    private String readRequestBody(HttpExchange exchange) throws IOException {
        try (InputStream is = exchange.getRequestBody()) {
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private String extractJsonField(String json, String fieldName) {
        if (json == null || json.isEmpty()) return "";
        Pattern pattern = Pattern.compile("\"" + fieldName + "\"\\s*:\\s*\"([^\"]*)\"");
        Matcher matcher = pattern.matcher(json);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return "";
    }

    private void sendJsonResponse(HttpExchange exchange, int statusCode, String responseJson) throws IOException {
        byte[] bytes = responseJson.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }
}
