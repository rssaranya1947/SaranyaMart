package com.saranyamart.controller;

import com.saranyamart.dao.CouponDao;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Spring REST Controller for Coupon validation and promo discounts.
 */
@RestController
@RequestMapping("/api/coupons")
@CrossOrigin(origins = "*")
public class CouponController {

    private final CouponDao couponDao = new CouponDao();

    @PostMapping("/apply")
    public ResponseEntity<?> applyCoupon(@RequestBody Map<String, Object> payload) {
        String code = (String) payload.get("code");
        Number totalNum = (Number) payload.get("orderTotal");
        double orderTotal = totalNum != null ? totalNum.doubleValue() : 0.0;

        Map<String, Object> result = couponDao.validateAndApply(code, orderTotal);
        if ((Boolean) result.get("valid")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }
}
