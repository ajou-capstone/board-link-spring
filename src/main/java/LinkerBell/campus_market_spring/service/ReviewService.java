package LinkerBell.campus_market_spring.service;

import LinkerBell.campus_market_spring.domain.Item;
import LinkerBell.campus_market_spring.domain.Review;
import LinkerBell.campus_market_spring.domain.User;
import LinkerBell.campus_market_spring.dto.ReviewRequestDto;
import LinkerBell.campus_market_spring.dto.ReviewResponseDto;
import LinkerBell.campus_market_spring.dto.SliceResponse;
import LinkerBell.campus_market_spring.global.error.ErrorCode;
import LinkerBell.campus_market_spring.global.error.exception.CustomException;
import LinkerBell.campus_market_spring.repository.ItemRepository;
import LinkerBell.campus_market_spring.repository.ReviewRepository;
import LinkerBell.campus_market_spring.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    // 리뷰 작성하기
    public void postReview(Long userId, Long writerId, ReviewRequestDto reviewRequestDto) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        User writer = userRepository.findById(writerId)
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Item item = itemRepository.findById(reviewRequestDto.getItemId())
            .orElseThrow(() -> new CustomException(ErrorCode.ITEM_NOT_FOUND));

        int reviewCount = reviewRepository.countReview(user);

        Review review = Review.builder()
            .user(writer)
            .item(item)
            .description(reviewRequestDto.getDescription())
            .rating(reviewRequestDto.getRating())
            .build();

        reviewRepository.save(review);

        // 리뷰 저장 이후 유저 평균 별점을 다시 계산
        double userRating = user.getRating();
        double newUserRating =
            ((userRating * reviewCount) + (reviewRequestDto.getRating())) / (reviewCount + 1);
        user.setRating(newUserRating);
    }

    // 리뷰 가져오기
    @Transactional(readOnly = true)
    public SliceResponse<ReviewResponseDto> getReviews(Long userId, Pageable pageable) {
        return reviewRepository.findAllByUserId(userId, pageable);
    }

    // 나에게 작성된 리뷰 가져오기
    @Transactional(readOnly = true)
    public SliceResponse<ReviewResponseDto> getReviewsToMe(Long userId, Pageable pageable) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Slice<ReviewResponseDto> reviewsToMe = reviewRepository.findReviewsToMe(user, pageable)
            .map(review ->
                ReviewResponseDto.builder()
                    .reviewId(review.getReviewId())
                    .nickname(review.getUser().getNickname())
                    .profileImage(review.getUser().getProfileImage())
                    .description(review.getDescription())
                    .rating(review.getRating())
                    .createdAt(review.getCreatedDate())
                    .build());

        return new SliceResponse<>(reviewsToMe);
    }
}
