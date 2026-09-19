package com.saranyamart.service;

import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Component;

/**
 * Mock implementation of ChatProvider per Section 17 of Project Specification.
 * Provides canned, zero-latency answers for e-commerce domain questions without network calls.
 * Refined for Week 10 with enhanced domain knowledge and category assist.
 */
@Component("mockChatProvider")
public class MockChatProvider implements ChatProvider {

    private final Map<String, String> faqResponses = new HashMap<>();

    public MockChatProvider() {
        faqResponses.put("shipping", "🚚 **Shipping & Delivery Info**:\nSaranyaMart offers standard delivery (3-5 business days) across India. Free shipping is automatically applied on orders over ₹1,000!");
        faqResponses.put("delivery", "🚚 **Shipping & Delivery Info**:\nStandard shipping takes 3-5 business days. You can track live delivery status under 'My Order History'.");
        faqResponses.put("return", "🔄 **Return & Refund Policy**:\nYou can request a return within 7 days of receiving your item. Contact the seller directly or file a request via your Order History tab.");
        faqResponses.put("refund", "💰 **Refund Process**:\nRefunds are processed within 2-4 business days back to your original payment method after seller inspection.");
        faqResponses.put("payment", "💳 **Payment Methods**:\nSaranyaMart supports Credit/Debit cards, Net Banking, UPI, and Cash on Delivery (COD) via our secure mock checkout step.");
        faqResponses.put("coupon", "🎟️ **Promo Codes & Coupons**:\nApply promo code `SARANYA10` for 10% off (up to ₹5,000) or `WELCOME20` for 20% off directly in your Shopping Cart!");
        faqResponses.put("discount", "🏷️ **Discounts & Offers**:\nCheck our home page for featured products on discount, or enter `SARANYA10` at cart checkout for an instant 10% discount!");
        faqResponses.put("track", "📦 **Order Tracking**:\nLog in to your account and open the 'My Order History' tab to view real-time status and download GST invoices.");
        faqResponses.put("seller", "🏪 **Become a Seller**:\nTo start selling on SaranyaMart, register an account as a 'Seller'. You can list products, set stock, set prices, and fulfill customer orders!");
        faqResponses.put("warranty", "🛡️ **Product Warranty**:\nMost electronic items and laptops listed by verified sellers come with a 1-year manufacturer warranty detailed on each product card.");
        faqResponses.put("admin", "🛡️ **Admin Supervision**:\nPlatform administrators oversee user accounts, moderate listings, and export detailed CSV reports under the Admin Control Panel.");
        faqResponses.put("invoice", "🧾 **GST Invoices**:\nYou can view and print GST-compliant invoices for any past purchase directly from your 'My Order History' section!");
        faqResponses.put("contact", "✉️ **Customer Support**:\nYou can send direct messages to sellers on product pages or email our administrative team at `admin@saranyamart.com`.");
    }

    @Override
    public String getReply(String userMessage, String context) {
        if (userMessage == null || userMessage.trim().isEmpty()) {
            return "Hello! I am your SaranyaMart AI Shopping Assistant. How can I help you today with products, orders, shipping, or payments?";
        }

        String lower = userMessage.toLowerCase().trim();

        for (Map.Entry<String, String> entry : faqResponses.entrySet()) {
            if (lower.contains(entry.getKey())) {
                return entry.getValue();
            }
        }

        if (lower.contains("hello") || lower.contains("hi") || lower.contains("hey")) {
            return "Hello there! Welcome to **SaranyaMart**. How can I assist you with your shopping or selling experience today?";
        }

        if (lower.contains("laptop")) {
            return "💻 **Laptops on SaranyaMart**:\nWe feature high-performance Intel i7 laptops, ultrabooks, and gaming laptops with 1-year brand warranty. Click 'Laptops' in the category bar to filter catalog!";
        }

        if (lower.contains("mobile") || lower.contains("phone")) {
            return "📱 **Smartphones & Mobiles**:\nExplore 5G smartphones with AMOLED displays, 50MP camera setups, and fast charging. Use the category bar to browse top mobile listings!";
        }

        if (lower.contains("electronics") || lower.contains("headphone") || lower.contains("watch")) {
            return "🎧 **Electronics & Accessories**:\nBrowse active noise-cancelling headphones, wireless Bluetooth headsets, and smart fitness watches listed by verified sellers!";
        }

        if (lower.contains("fashion")) {
            return "👕 **Fashion & Lifestyle**:\nDiscover trendsetting apparel and accessories at competitive marketplace prices!";
        }

        return "🤖 **SaranyaMart Assistant**:\nThank you for asking! I am trained on SaranyaMart policies, products, orders, returns, and coupons. Try asking: *'What is your return policy?'*, *'How do I use a coupon?'*, or *'How long does shipping take?'*";
    }
}
