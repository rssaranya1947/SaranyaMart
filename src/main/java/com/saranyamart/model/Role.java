package com.saranyamart.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Enumeration representing the user roles in SaranyaMart.
 * Uses Jackson annotations for seamless JSON serialization & deserialization.
 */
public enum Role {
    BUYER("buyer"),
    SELLER("seller"),
    ADMIN("admin");

    private final String value;

    Role(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    /**
     * Parse string into Role enum value for Jackson JSON deserialization.
     */
    @JsonCreator
    public static Role fromString(String roleStr) {
        if (roleStr == null || roleStr.trim().isEmpty()) {
            return BUYER;
        }
        String cleanRole = roleStr.trim();
        for (Role r : Role.values()) {
            if (r.value.equalsIgnoreCase(cleanRole) || r.name().equalsIgnoreCase(cleanRole)) {
                return r;
            }
        }
        return BUYER; // Default fallback role
    }

    @Override
    public String toString() {
        return value;
    }
}
