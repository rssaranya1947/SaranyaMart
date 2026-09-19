package com.saranyamart.service;

import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Component;

/**
 * Mock implementation of ChatProvider per Section 17 of Project Specification.
 * Provides canned, zero-latency answers for e-commerce domain questions without network calls.
 */
@Component("mockChatProvider")
public class MockChatProvider implements ChatProvider {

    private final Map<String, String> faqResponses = new HashMap<>();

    public MockChatProvider() {
        faqResponses.put("shipping", "🚚 **Shipping & Delivery Info**:\nSaranyaMart offers standard delivery (3-5 business days) across India. Express shipping options are available at checkout!");
        faqResponses.put("delivery", "🚚 **Shipping & Delivery Info**:\nStandard shipping takes 3-5 business days. Free shipping is automatically applied on eligible promotional items!");
        faqResponses.put("return", "🔄 **Return & Refund Policy**:\nYou can request a return within 7 days of receiving your item. Contact the seller directly or file a request via your Order History tab.");
        faqResponses.put("refund", "💰 **Refund Process**:\nRefunds are processed within 2-4 business days after the returned item passes quality check with the seller.");
        faqResponses.put("payment", "💳 **Payment Methods**:\nSaranyaMart supports Credit/Debit cards, Net Banking, UPI, and Cash on Delivery (COD) via our mock checkout step.");
        faqResponses.put("coupon", "🎟️ **Promo Codes & Coupons**:\nYou can apply promo codes like `SARANYA10` (10% off) or `WELCOME20` (20% off) directly in your Shopping Cart before checkout!");
        faqResponses.put("discount", "🏷️ **Discounts**:\nCheck our home page for featured products on discount, or use coupon `SARANYA10` during checkout for an extra 10% discount!");
        faqResponses.put("track", "📦 **Order Tracking**:\nLog in to your account and open the 'My Order History' tab to check the status of your purchases in real-time.");
        faqResponses.put("seller", "🏪 **Become a Seller**:\nTo start selling on SaranyaMart, register an account as a 'Seller'. You can list products, set stock, set prices, and fulfill customer orders!");
        faqResponses.put("warranty", "🛡️ **Product Warranty**:\nMost electronic items and laptops listed by verified sellers come with a 1-year brand warranty. Details are listed on each product card.");
        faqResponses.put("contact", "✉️ **Customer Support**:\nYou can send direct messages to sellers on product pages or email our administrative team at `admin@saranyamart.com`.");
    }

    @Override
    public String getReply(String userMessage, String context) {
        if (userMessage == null || userMessage.trim().isEmpty()) {
            return "Hello! I am your SaranyaMart Assistant. How can I help you today with products, orders, shipping, or payments?";
        }

        String lower = userMessage.toLowerCase().trim();

        for (Map.Entry<String, String> entry : faqResponses.entrySet()) {
            if (lower.contains(entry.getKey())) {
                return entry.getValue();
            }
        }

        if (lower.contains("hello") || lower.contains("hi") || lower.contains("hey")) {
            return "Hello there! Welcome to SaranyaMart. How can I assist you with your shopping or selling experience today?";
        }

        if (lower.contains("laptop") || lower.contains("mobile") || lower.contains("electronics") || lower.contains("fashion")) {
            return "🛍️ We have a wide range of Laptops, Mobiles, Electronics, and Fashion listed by top sellers! Use the top search bar or category filters to browse.";
        }

        return "🤖 **SaranyaMart Assistant**:\nThank you for reaching out! I can help you with order tracking, shipping rates, return policies, coupons, or seller inquiries. Try asking: *'What is your return policy?'* or *'How do I use a coupon?'*";
    }
}
