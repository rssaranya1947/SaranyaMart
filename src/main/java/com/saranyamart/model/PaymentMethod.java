package com.saranyamart.model;

/**
 * Enumeration of supported payment methods across SaranyaMart checkout.
 */
public enum PaymentMethod {
    UPI("UPI"),
    CREDIT_DEBIT_CARD("Credit/Debit Card"),
    NET_BANKING("Net Banking"),
    WALLET("Wallet"),
    CASH_ON_DELIVERY("Cash on Delivery");

    private final String displayName;

    PaymentMethod(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static boolean isValid(String method) {
        if (method == null || method.trim().isEmpty()) {
            return false;
        }
        for (PaymentMethod pm : values()) {
            if (pm.displayName.equalsIgnoreCase(method.trim()) || pm.name().equalsIgnoreCase(method.trim())) {
                return true;
            }
        }
        return false;
    }
}
