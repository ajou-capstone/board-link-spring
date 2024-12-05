package LinkerBell.campus_market_spring.controller;

import LinkerBell.campus_market_spring.dto.AuthUserDto;
import LinkerBell.campus_market_spring.dto.ReviewRequestDto;
import LinkerBell.campus_market_spring.dto.ReviewResponseDto;
import LinkerBell.campus_market_spring.dto.SliceResponse;
import LinkerBell.campus_market_spring.global.auth.Login;
import LinkerBell.campus_market_spring.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    // 리뷰 작성하기
    @PostMapping("/api/v1/users/{userId}/reviews")
    public ResponseEntity<Void> postReview(
        @Login AuthUserDto authUserDto, @PathVariable("userId") Long userId,
        @Valid @RequestBody ReviewRequestDto reviewRequestDto) {

        reviewService.postReview(authUserDto.getUserId(), userId, reviewRequestDto);

        return ResponseEntity.noContent().build();
    }

    // 내가 쓴 리뷰 가져오기
    @GetMapping("/api/v1/users/reviews")
    public ResponseEntity<SliceResponse<ReviewResponseDto>> getReviews(
        @Login AuthUserDto authUserDto,
        @PageableDefault(page = 0, size = 10, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable) {
        SliceResponse<ReviewResponseDto> reviewResponse = reviewService.getReviews(
            authUserDto.getUserId(), pageable);

        return ResponseEntity.ok(reviewResponse);
    }

    // 나에게 작성된 리뷰 가져오기
    @GetMapping("/api/v1/users/{userId}/reviews-to-me")
    public ResponseEntity<SliceResponse<ReviewResponseDto>> getReviewsToMe(
        @PathVariable(name = "userId") Long userId,
        @PageableDefault(page = 0, size = 10, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable) {
        SliceResponse<ReviewResponseDto> reviewsToMeResponse = reviewService.getReviewsToMe(userId,
            pageable);

        return ResponseEntity.ok(reviewsToMeResponse);
    }
}
