package com.saranyamart.dao;

import com.saranyamart.db.DatabaseManager;
import com.saranyamart.model.Product;
import com.saranyamart.model.Review;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Data Access Object (DAO) for Product Reviews and Star Ratings in SaranyaMart.
 */
public class ReviewDao {

    public synchronized Review addReview(Review review) {
        if (review.getRating() < 1 || review.getRating() > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5 stars.");
        }

        int newId = DatabaseManager.generateNextReviewId();
        review.setId(newId);
        
        if (review.getCreatedAt() == null || review.getCreatedAt().isEmpty()) {
            review.setCreatedAt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }

        DatabaseManager.getReviewMap().put(newId, review);
        DatabaseManager.saveReviewsToDisk();

        // Recalculate average rating & review count for the product
        updateProductRatingStats(review.getProductId());

        return review;
    }

    public List<Review> getReviewsByProduct(int productId) {
        return DatabaseManager.getReviewMap().values().stream()
                .filter(r -> r.getProductId() == productId)
                .sorted(Comparator.comparingInt(Review::getId).reversed())
                .collect(Collectors.toList());
    }

    public void updateProductRatingStats(int productId) {
        List<Review> reviews = getReviewsByProduct(productId);
        Product product = DatabaseManager.getProductMap().get(productId);
        if (product != null) {
            if (reviews.isEmpty()) {
                product.setAverageRating(5.0);
                product.setReviewCount(0);
            } else {
                double avg = reviews.stream().mapToInt(Review::getRating).average().orElse(5.0);
                // Round to 1 decimal place
                double roundedAvg = Math.round(avg * 10.0) / 10.0;
                product.setAverageRating(roundedAvg);
                product.setReviewCount(reviews.size());
            }
            DatabaseManager.saveProductsToDisk();
        }
    }
}
