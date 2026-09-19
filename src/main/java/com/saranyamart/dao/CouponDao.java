package com.saranyamart.dao;

import com.saranyamart.db.DatabaseManager;
import com.saranyamart.model.Coupon;

import java.util.Map;

/**
 * Data Access Object (DAO) for Coupon validation and discount calculations in SaranyaMart.
 */
public class CouponDao {

    public Coupon findByCode(String code) {
        if (code == null) return null;
        return DatabaseManager.getCouponMap().get(code.trim().toUpperCase());
    }

    public Map<String, Object> validateAndApply(String code, double orderTotal) {
        if (code == null || code.trim().isEmpty()) {
            return Map.of("valid", false, "message", "Coupon code is required.");
        }

        Coupon coupon = findByCode(code);
        if (coupon == null || !coupon.isActive()) {
            return Map.of("valid", false, "message", "Invalid or expired coupon code.");
        }

        if (orderTotal < coupon.getMinOrderAmount()) {
            return Map.of("valid", false, "message", "Minimum order amount of ₹" + coupon.getMinOrderAmount() + " required for this coupon.");
        }

        double calculatedDiscount = (orderTotal * coupon.getDiscountPercent()) / 100.0;
        if (coupon.getMaxDiscountAmount() > 0 && calculatedDiscount > coupon.getMaxDiscountAmount()) {
            calculatedDiscount = coupon.getMaxDiscountAmount();
        }

        // Round to 2 decimal places
        calculatedDiscount = Math.round(calculatedDiscount * 100.0) / 100.0;
        double finalTotal = Math.max(0.0, orderTotal - calculatedDiscount);

        return Map.of(
            "valid", true,
            "code", coupon.getCode(),
            "discountPercent", coupon.getDiscountPercent(),
            "discountAmount", calculatedDiscount,
            "finalTotal", finalTotal,
            "message", "Coupon '" + coupon.getCode() + "' applied! Discount: ₹" + calculatedDiscount
        );
    }
}
