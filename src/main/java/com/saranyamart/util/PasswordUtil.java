package com.saranyamart.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;

/**
 * Security utility class for password hashing and validation.
 */
public class PasswordUtil {

    private static final String SALT = "SaranyaMartSalt2026!";

    /**
     * Hashes plain text password using SHA-256 with a salt.
     */
    public static String hashPassword(String password) {
        if (password == null || password.isEmpty()) {
            return "";
        }
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            String saltedPassword = password + SALT;
            byte[] hashBytes = digest.digest(saltedPassword.getBytes(StandardCharsets.UTF_8));
            
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not found", e);
        }
    }

    /**
     * Verifies if raw password matches stored hash.
     */
    public static boolean verifyPassword(String rawPassword, String storedHash) {
        if (rawPassword == null || storedHash == null) {
            return false;
        }
        return hashPassword(rawPassword).equalsIgnoreCase(storedHash);
    }
}
