package vn.ClothingStore.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import vn.ClothingStore.domain.Review;
import vn.ClothingStore.domain.request.review.ReqReviewDTO;
import vn.ClothingStore.domain.request.review.ReqUpdateReviewDTO;
import vn.ClothingStore.domain.response.review.ResReviewDTO;
import vn.ClothingStore.service.ReviewService;
import vn.ClothingStore.util.annotation.ApiMessage;
import vn.ClothingStore.util.error.IdInvalidException;

@RestController
@RequestMapping("/api/v1")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    // Lấy review theo product
    @GetMapping("/review/product/{productId}")
    @ApiMessage("get reviews by product success")
    public ResponseEntity<List<ResReviewDTO>> getReviewsByProduct(@PathVariable int productId)
            throws IdInvalidException {
        List<Review> reviews = this.reviewService.getReviewsByProductId(productId);
        List<ResReviewDTO> res = reviews.stream()
                .map(this.reviewService::convertToResReviewDTO)
                .toList();
        return ResponseEntity.ok(res);
    }

    @PostMapping("/review")
    @ApiMessage("create review success")
    public ResponseEntity<ResReviewDTO> createReview(@Valid @RequestBody ReqReviewDTO req) throws IdInvalidException {
        Review review = this.reviewService.createReview(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(this.reviewService.convertToResReviewDTO(review));
    }

    @PutMapping("/review/{reviewId}")
    @ApiMessage("update review success")
    public ResponseEntity<ResReviewDTO> updateReview(@Valid @PathVariable int reviewId,
            @RequestBody ReqUpdateReviewDTO req) throws IdInvalidException {
        Review review = this.reviewService.updatReview(reviewId, req);
        return ResponseEntity.status(HttpStatus.OK).body(this.reviewService.convertToResReviewDTO(review));

    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/review/{reviewId}")
    @ApiMessage("delete review success")
    public ResponseEntity<Void> deleteReview(@PathVariable int reviewId) throws IdInvalidException {
        this.reviewService.deleteReview(reviewId);
        return ResponseEntity.ok().body(null);
    }

}
