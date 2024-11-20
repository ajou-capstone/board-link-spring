package LinkerBell.campus_market_spring.controller;

import LinkerBell.campus_market_spring.dto.AuthUserDto;
import LinkerBell.campus_market_spring.dto.ReviewRequestDto;
import LinkerBell.campus_market_spring.global.auth.Login;
import LinkerBell.campus_market_spring.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
        @Login AuthUserDto authUserDto, @PathVariable Long userId,
        @RequestBody ReviewRequestDto reviewRequestDto) {

        reviewService.postReview(authUserDto.getUserId(), userId, reviewRequestDto);

        return ResponseEntity.noContent().build();
    }

}
