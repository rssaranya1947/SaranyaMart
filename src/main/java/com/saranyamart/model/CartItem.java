package com.saranyamart.model;

/**
 * Data Transfer Object representing an item in the Buyer's Shopping Cart.
 */
public class CartItem {
    private int productId;
    private String title;
    private double price;
    private String imageUrl;
    private int quantity;
    private int sellerId;
    private String sellerName;

    public CartItem() {}

    public CartItem(int productId, String title, double price, String imageUrl, int quantity, int sellerId, String sellerName) {
        this.productId = productId;
        this.title = title;
        this.price = price;
        this.imageUrl = imageUrl;
        this.quantity = quantity;
        this.sellerId = sellerId;
        this.sellerName = sellerName;
    }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public int getSellerId() { return sellerId; }
    public void setSellerId(int sellerId) { this.sellerId = sellerId; }

    public String getSellerName() { return sellerName; }
    public void setSellerName(String sellerName) { this.sellerName = sellerName; }
}
