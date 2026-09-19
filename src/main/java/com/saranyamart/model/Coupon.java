package com.saranyamart.model;

/**
 * Domain model representing a Discount Coupon / Promo Code in SaranyaMart.
 */
public class Coupon {
    private String code;
    private double discountPercent;
    private double maxDiscountAmount;
    private double minOrderAmount;
    private boolean active;
    private String description;

    public Coupon() {}

    public Coupon(String code, double discountPercent, double maxDiscountAmount, double minOrderAmount, boolean active, String description) {
        this.code = code;
        this.discountPercent = discountPercent;
        this.maxDiscountAmount = maxDiscountAmount;
        this.minOrderAmount = minOrderAmount;
        this.active = active;
        this.description = description;
    }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public double getDiscountPercent() { return discountPercent; }
    public void setDiscountPercent(double discountPercent) { this.discountPercent = discountPercent; }

    public double getMaxDiscountAmount() { return maxDiscountAmount; }
    public void setMaxDiscountAmount(double maxDiscountAmount) { this.maxDiscountAmount = maxDiscountAmount; }

    public double getMinOrderAmount() { return minOrderAmount; }
    public void setMinOrderAmount(double minOrderAmount) { this.minOrderAmount = minOrderAmount; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
