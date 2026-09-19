package com.saranyamart.model;

/**
 * Domain model representing a Product Review in SaranyaMart.
 */
public class Review {
    private int id;
    private int productId;
    private int buyerId;
    private String buyerName;
    private int rating; // 1 to 5
    private String comment;
    private String createdAt;

    public Review() {}

    public Review(int id, int productId, int buyerId, String buyerName, int rating, String comment, String createdAt) {
        this.id = id;
        this.productId = productId;
        this.buyerId = buyerId;
        this.buyerName = buyerName;
        this.rating = rating;
        this.comment = comment;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public int getBuyerId() { return buyerId; }
    public void setBuyerId(int buyerId) { this.buyerId = buyerId; }

    public String getBuyerName() { return buyerName; }
    public void setBuyerName(String buyerName) { this.buyerName = buyerName; }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
