package com.saranyamart.model;

/**
 * Domain model representing an item within a placed Order.
 */
public class OrderItem {
    private int productId;
    private String title;
    private double price;
    private int quantity;
    private int sellerId;

    public OrderItem() {}

    public OrderItem(int productId, String title, double price, int quantity, int sellerId) {
        this.productId = productId;
        this.title = title;
        this.price = price;
        this.quantity = quantity;
        this.sellerId = sellerId;
    }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public int getSellerId() { return sellerId; }
    public void setSellerId(int sellerId) { this.sellerId = sellerId; }
}
