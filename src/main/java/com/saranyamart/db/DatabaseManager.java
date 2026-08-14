package com.saranyamart.db;

import com.saranyamart.model.Role;
import com.saranyamart.model.User;
import com.saranyamart.util.PasswordUtil;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Pure Java Database Manager for SaranyaMart.
 * Uses thread-safe memory storage with file-backed JSON persistence.
 * Requires 0 external JAR dependencies!
 */
public class DatabaseManager {

    private static final String DATA_FILE = "saranyamart_users.json";
    private static final Map<Integer, User> userMap = new ConcurrentHashMap<>();
    private static final Map<String, Integer> emailIndex = new ConcurrentHashMap<>();
    private static final AtomicInteger idCounter = new AtomicInteger(100);

    /**
     * Initializes database tables & seeds default accounts.
     */
    public static synchronized void initializeDatabase() {
        System.out.println("[DatabaseManager] Initializing Pure Java Storage Engine...");
        
        loadFromDisk();

        // Seed default accounts if missing
        seedUserIfNotExists("Admin User", "admin@saranyamart.com", "Admin@123", Role.ADMIN);
        seedUserIfNotExists("Priya Electronics", "seller@saranyamart.com", "Seller@123", Role.SELLER);
        seedUserIfNotExists("Arun Kumar", "buyer@saranyamart.com", "Buyer@123", Role.BUYER);

        saveToDisk();

        System.out.println("[DatabaseManager] Database initialized successfully! Total users: " + userMap.size());
    }

    public static Map<Integer, User> getUserMap() {
        return userMap;
    }

    public static Map<String, Integer> getEmailIndex() {
        return emailIndex;
    }

    public static int generateNextId() {
        return idCounter.incrementAndGet();
    }

    public static synchronized void saveToDisk() {
        File file = new File(DATA_FILE);
        StringBuilder json = new StringBuilder("[\n");
        List<User> list = new ArrayList<>(userMap.values());
        for (int i = 0; i < list.size(); i++) {
            User u = list.get(i);
            json.append("  {")
                .append("\"id\":").append(u.getId()).append(",")
                .append("\"fullName\":\"").append(escape(u.getFullName())).append("\",")
                .append("\"email\":\"").append(escape(u.getEmail())).append("\",")
                .append("\"passwordHash\":\"").append(escape(u.getPasswordHash())).append("\",")
                .append("\"role\":\"").append(u.getRole().getValue()).append("\",")
                .append("\"createdAt\":\"").append(escape(u.getCreatedAt() != null ? u.getCreatedAt() : "")).append("\"")
                .append("}");
            if (i < list.size() - 1) json.append(",");
            json.append("\n");
        }
        json.append("]");

        try (FileWriter writer = new FileWriter(file, StandardCharsets.UTF_8)) {
            writer.write(json.toString());
        } catch (IOException e) {
            System.err.println("[DatabaseManager] Error saving to disk: " + e.getMessage());
        }
    }

    private static void loadFromDisk() {
        File file = new File(DATA_FILE);
        if (!file.exists()) return;

        try {
            String content = Files.readString(file.toPath(), StandardCharsets.UTF_8);
            Pattern userPattern = Pattern.compile("\\{[^}]*\\}");
            Matcher matcher = userPattern.matcher(content);

            int maxId = 100;
            while (matcher.find()) {
                String block = matcher.group();
                int id = Integer.parseInt(extractField(block, "id", "100"));
                String fullName = extractField(block, "fullName", "");
                String email = extractField(block, "email", "");
                String hash = extractField(block, "passwordHash", "");
                String roleStr = extractField(block, "role", "buyer");
                String createdAt = extractField(block, "createdAt", "2026-08-12");

                User user = new User(id, fullName, email, hash, Role.fromString(roleStr), createdAt);
                userMap.put(id, user);
                emailIndex.put(email.toLowerCase(), id);

                if (id > maxId) maxId = id;
            }
            idCounter.set(maxId);
        } catch (Exception e) {
            System.err.println("[DatabaseManager] Warning: Could not parse " + DATA_FILE + ": " + e.getMessage());
        }
    }

    private static void seedUserIfNotExists(String name, String email, String rawPassword, Role role) {
        String cleanEmail = email.trim().toLowerCase();
        if (!emailIndex.containsKey(cleanEmail)) {
            int newId = generateNextId();
            String hash = PasswordUtil.hashPassword(rawPassword);
            User user = new User(newId, name, cleanEmail, hash, role, "2026-08-12 10:00:00");
            userMap.put(newId, user);
            emailIndex.put(cleanEmail, newId);
            System.out.println("[DatabaseManager] Seeded default account: " + cleanEmail + " (" + role + ")");
        }
    }

    private static String extractField(String block, String field, String defaultVal) {
        Pattern p = Pattern.compile("\"" + field + "\"\\s*:\\s*\"?([^\",}]*)\"?");
        Matcher m = p.matcher(block);
        if (m.find()) return m.group(1).trim();
        return defaultVal;
    }

    private static String escape(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
