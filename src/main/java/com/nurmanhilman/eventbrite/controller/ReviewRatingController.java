package com.nurmanhilman.eventbrite.controller;

import com.nurmanhilman.eventbrite.entities.ReviewRatingEntity;
import com.nurmanhilman.eventbrite.entities.TrxEntity;
import com.nurmanhilman.eventbrite.entities.UserEntity;
import com.nurmanhilman.eventbrite.exception.CustomResponseStatusException;
import com.nurmanhilman.eventbrite.repositories.EventRepository;
import com.nurmanhilman.eventbrite.repositories.ReviewRatingRepository;
import com.nurmanhilman.eventbrite.repositories.TrxRepository;
import com.nurmanhilman.eventbrite.service.ReviewRatingService;
import com.nurmanhilman.eventbrite.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.*;

@RestController
@RequestMapping("/api/v1/reviews-ratings")
public class ReviewRatingController {

    @Autowired
    private ReviewRatingService reviewRatingService;

    @Autowired
    private ReviewRatingRepository reviewRatingRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private TrxRepository trxRepository;

    @Autowired
    private EventRepository eventRepository;

    @GetMapping("/id/{reviewRatingId}")
    public ResponseEntity<ReviewRatingEntity> getReviewRatingById(@PathVariable Long reviewRatingId) {
        return reviewRatingService.getReviewRatingById(reviewRatingId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/create/{eventId}")
    public ResponseEntity<?> createReviewRating(@RequestHeader("Authorization") String authorizationHeader,
                                                @RequestBody ReviewRatingEntity reviewRating,
                                                @PathVariable Long eventId) {
        UserEntity userEntity = userService.getUserFromJwt(authorizationHeader);
        Optional<Long> trxId = trxRepository.findTrxIdByUserIdAndEventId(userEntity.getUserId(), eventId);

        // tolak jika user belum transaksi untuk event ini
        if (trxId.isEmpty()) {
            return new CustomResponseStatusException(HttpStatus.FORBIDDEN, "Access denied. Only user who purchase tickets can review.").generateResponse();
        }

        // tolak jika event belum selesai
        if (!eventRepository.isEventExpired(eventId)) {
            return new CustomResponseStatusException(HttpStatus.FORBIDDEN, "Access denied. Event is not finished yet.").generateResponse();
        }

        if (reviewRatingRepository.existsByUserIdAndEventId(userEntity.getUserId(), eventId)) {
            return new CustomResponseStatusException(HttpStatus.FORBIDDEN, "Access denied. User have reviewed.").generateResponse();
        }

        reviewRating.setTransactionId(trxId.get());
        reviewRating.setCreatedAt(Instant.now());
        reviewRating.setUpdatedAt(Instant.now());

        return ResponseEntity.ok(reviewRatingService.createReviewRating(reviewRating));
    }

    @GetMapping("/list-by-user-id")
    public ResponseEntity<?> getListByUserId(@RequestHeader("Authorization") String authorizationHeader) {
        UserEntity userEntity = userService.getUserFromJwt(authorizationHeader);
        List<Map<String, Object>> result = new ArrayList<>();
        List<ReviewRatingEntity> reviews = reviewRatingRepository.findByUserId(userEntity.getUserId());
        for (int loop = 0; loop < reviews.size(); loop++) {
            Map<String, Object> data = new HashMap<>();
            data.put("transaction", trxRepository.findById(reviews.get(loop).getTransactionId()).get());
            data.put("review", reviews.get(loop));
            result.add(data);
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/list-by-event-id/{eventId}")
    public ResponseEntity<?> getListByEventId(@PathVariable Long eventId) {
        List<ReviewRatingEntity> reviews = reviewRatingRepository.findByEventId(eventId);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/is-eligible/{eventId}")
    public ResponseEntity<?> isEligibleForReview(@RequestHeader("Authorization") String authorizationHeader,
                                                 @PathVariable Long eventId) {
        UserEntity userEntity = userService.getUserFromJwt(authorizationHeader);
        Map<String, Object> result = new HashMap<>();
        // tolak jika user belum transaksi untuk event ini
       if (!trxRepository.existsByUserIdAndEventId(userEntity.getUserId(), eventId)) {
           throw new CustomResponseStatusException(HttpStatus.FORBIDDEN, "Access denied. Only user who purchase tickets can review.");
       }
       // tolak jika user sudah transaksi tapi event belum mulai
       else if (trxRepository.existsByUserIdAndEventId(userEntity.getUserId(), eventId)
       && !eventRepository.isEventExpired(eventId)) {
           result.put("isEligible", false);
           result.put("isReviewGiven", false);
           result.put("reason", "Event not finished");
       }
       // tolak jika user sudah membuat review
       else if (reviewRatingRepository.existsByUserIdAndEventId(userEntity.getUserId(), eventId)) {
           result.put("isEligible", false);
           result.put("isReviewGiven", true);
           result.put("reason", "Review Given");
        }else {
           result.put("isReviewGiven", false);
           result.put("isEligible", true);
        }
       return ResponseEntity.ok(result);
    }

    @PutMapping("/id/{reviewRatingId}")
    public ResponseEntity<ReviewRatingEntity> updateReviewRating(@PathVariable Long reviewRatingId, @RequestBody ReviewRatingEntity reviewRatingDetails) {
        try {
            ReviewRatingEntity updatedReviewRating = reviewRatingService.updateReviewRating(reviewRatingId, reviewRatingDetails);
            return ResponseEntity.ok(updatedReviewRating);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/id/{reviewRatingId}")
    public ResponseEntity<Void> deleteReviewRating(@PathVariable Long reviewRatingId) {
        reviewRatingService.deleteReviewRating(reviewRatingId);
        return ResponseEntity.noContent().build();
    }
}

