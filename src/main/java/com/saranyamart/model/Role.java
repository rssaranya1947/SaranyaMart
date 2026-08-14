package com.saranyamart.model;

/**
 * Enumeration representing the user roles in SaranyaMart.
 */
public enum Role {
    BUYER("buyer"),
    SELLER("seller"),
    ADMIN("admin");

    private final String value;

    Role(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    /**
     * Parse string into Role enum value.
     */
    public static Role fromString(String roleStr) {
        if (roleStr == null) {
            return BUYER;
        }
        String cleanRole = roleStr.trim().toLowerCase();
        for (Role r : Role.values()) {
            if (r.value.equalsIgnoreCase(cleanRole)) {
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
