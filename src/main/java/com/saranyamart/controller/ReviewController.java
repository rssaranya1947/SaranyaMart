package com.saranyamart.controller;

import com.saranyamart.dao.ReviewDao;
import com.saranyamart.model.Review;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Spring REST Controller for Product Reviews and Ratings.
 */
@RestController
@RequestMapping("/api/reviews")
@CrossOrigin(origins = "*")
public class ReviewController {

    private final ReviewDao reviewDao = new ReviewDao();

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<Review>> getReviewsForProduct(@PathVariable int productId) {
        List<Review> reviews = reviewDao.getReviewsByProduct(productId);
        return ResponseEntity.ok(reviews);
    }

    @PostMapping
    public ResponseEntity<?> addReview(@RequestBody Review review) {
        try {
            if (review.getProductId() <= 0) {
                return ResponseEntity.badRequest().body(Map.of("message", "Product ID is required"));
            }
            if (review.getRating() < 1 || review.getRating() > 5) {
                return ResponseEntity.badRequest().body(Map.of("message", "Rating must be between 1 and 5 stars"));
            }
            Review saved = reviewDao.addReview(review);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", e.getMessage()));
        }
    }
}
