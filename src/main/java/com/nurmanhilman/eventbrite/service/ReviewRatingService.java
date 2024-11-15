package com.nurmanhilman.eventbrite.service;

import com.nurmanhilman.eventbrite.entities.ReviewRatingEntity;
import com.nurmanhilman.eventbrite.repositories.ReviewRatingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class ReviewRatingService {

    private final ReviewRatingRepository reviewRatingRepository;

    @Autowired
    public ReviewRatingService(ReviewRatingRepository reviewRatingRepository) {
        this.reviewRatingRepository = reviewRatingRepository;
    }

    public List<ReviewRatingEntity> getAllReviewRatings() {
        return reviewRatingRepository.findAll();
    }

    public Optional<ReviewRatingEntity> getReviewRatingById(Long reviewRatingId) {
        return reviewRatingRepository.findById(reviewRatingId);
    }

    public ReviewRatingEntity createReviewRating(ReviewRatingEntity reviewRating) {
        return reviewRatingRepository.save(reviewRating);
    }

    public ReviewRatingEntity updateReviewRating(Long reviewRatingId, ReviewRatingEntity reviewRatingDetails) {
        return reviewRatingRepository.findById(reviewRatingId).map(reviewRating -> {
            reviewRating.setReview(reviewRatingDetails.getReview());
            reviewRating.setSuggestion(reviewRatingDetails.getSuggestion());
            reviewRating.setRating(reviewRatingDetails.getRating());
            reviewRating.setUpdatedAt(Instant.now());
            return reviewRatingRepository.save(reviewRating);
        }).orElseThrow(() -> new RuntimeException("ReviewRating not found with id " + reviewRatingId));
    }

    public void deleteReviewRating(Long reviewRatingId) {
        reviewRatingRepository.deleteById(reviewRatingId);
    }
}

