package com.saranyamart.dao;

import com.saranyamart.db.DatabaseManager;
import com.saranyamart.model.RegisterRequest;
import com.saranyamart.model.Role;
import com.saranyamart.model.User;
import com.saranyamart.util.PasswordUtil;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Data Access Object (DAO) for User operations in SaranyaMart using Pure Java storage engine.
 */
public class UserDao {

    public boolean existsByEmail(String email) {
        if (email == null) return false;
        return DatabaseManager.getEmailIndex().containsKey(email.trim().toLowerCase());
    }

    public User findByEmail(String email) {
        if (email == null) return null;
        Integer id = DatabaseManager.getEmailIndex().get(email.trim().toLowerCase());
        if (id != null) {
            return DatabaseManager.getUserMap().get(id);
        }
        return null;
    }

    public User findById(int id) {
        return DatabaseManager.getUserMap().get(id);
    }

    public synchronized User createUser(RegisterRequest request) {
        String cleanEmail = request.getEmail().trim().toLowerCase();
        if (existsByEmail(cleanEmail)) {
            throw new IllegalArgumentException("User with email '" + request.getEmail() + "' already exists.");
        }

        int newId = DatabaseManager.generateNextId();
        String hashedPassword = PasswordUtil.hashPassword(request.getPassword());
        String timestamp = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        User newUser = new User(newId, request.getFullName().trim(), cleanEmail, hashedPassword, request.getRole(), timestamp);
        
        DatabaseManager.getUserMap().put(newId, newUser);
        DatabaseManager.getEmailIndex().put(cleanEmail, newId);
        DatabaseManager.saveToDisk();

        return newUser;
    }

    public User authenticate(String email, String rawPassword) {
        User user = findByEmail(email);
        if (user == null) {
            return null;
        }
        if (PasswordUtil.verifyPassword(rawPassword, user.getPasswordHash())) {
            return user;
        }
        return null;
    }

    public List<User> getAllUsers() {
        List<User> list = new ArrayList<>(DatabaseManager.getUserMap().values());
        list.sort(Comparator.comparingInt(User::getId).reversed());
        return list;
    }
}
