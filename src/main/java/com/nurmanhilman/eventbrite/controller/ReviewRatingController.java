package com.nurmanhilman.eventbrite.controller;

import com.nurmanhilman.eventbrite.entities.ReviewRatingEntity;
import com.nurmanhilman.eventbrite.service.ReviewRatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews_ratings")
public class ReviewRatingController {

    private final ReviewRatingService reviewRatingService;

    @Autowired
    public ReviewRatingController(ReviewRatingService reviewRatingService) {
        this.reviewRatingService = reviewRatingService;
    }

    @GetMapping
    public List<ReviewRatingEntity> getAllReviewRatings() {
        return reviewRatingService.getAllReviewRatings();
    }

    @GetMapping("/{reviewRatingId}")
    public ResponseEntity<ReviewRatingEntity> getReviewRatingById(@PathVariable Long reviewRatingId) {
        return reviewRatingService.getReviewRatingById(reviewRatingId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ReviewRatingEntity createReviewRating(@RequestBody ReviewRatingEntity reviewRating) {
        return reviewRatingService.createReviewRating(reviewRating);
    }

    @PutMapping("/{reviewRatingId}")
    public ResponseEntity<ReviewRatingEntity> updateReviewRating(@PathVariable Long reviewRatingId, @RequestBody ReviewRatingEntity reviewRatingDetails) {
        try {
            ReviewRatingEntity updatedReviewRating = reviewRatingService.updateReviewRating(reviewRatingId, reviewRatingDetails);
            return ResponseEntity.ok(updatedReviewRating);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{reviewRatingId}")
    public ResponseEntity<Void> deleteReviewRating(@PathVariable Long reviewRatingId) {
        reviewRatingService.deleteReviewRating(reviewRatingId);
        return ResponseEntity.noContent().build();
    }
}

