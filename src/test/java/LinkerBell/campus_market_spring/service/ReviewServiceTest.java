package LinkerBell.campus_market_spring.service;

import LinkerBell.campus_market_spring.domain.Item;
import LinkerBell.campus_market_spring.domain.Review;
import LinkerBell.campus_market_spring.domain.User;
import LinkerBell.campus_market_spring.dto.ReviewRequestDto;
import LinkerBell.campus_market_spring.global.error.ErrorCode;
import LinkerBell.campus_market_spring.global.error.exception.CustomException;
import LinkerBell.campus_market_spring.repository.ItemRepository;
import LinkerBell.campus_market_spring.repository.ReviewRepository;
import LinkerBell.campus_market_spring.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.within;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ReviewServiceTest {

    @InjectMocks
    private ReviewService reviewService;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("postReview - 정상적으로 리뷰를 저장하고 유저의 평점을 업데이트")
    void postReview_ShouldSaveReviewAndUpdateUserRating() {
        // given
        Long userId = 1L;
        Long writerId = 2L;
        Long itemId = 3L;

        User user = User.builder()
            .userId(userId)
            .rating(4.5)
            .build();
        User writer = User.builder()
            .userId(writerId)
            .build();
        Item item = Item.builder()
            .itemId(itemId)
            .user(user) // 아이템의 소유자
            .build();

        ReviewRequestDto reviewRequestDto = ReviewRequestDto.builder()
            .itemId(itemId)
            .description("Great item!")
            .rating(5)
            .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.findById(writerId)).thenReturn(Optional.of(writer));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(reviewRepository.countReview(user)).thenReturn(2); // 기존 리뷰 개수

        // when
        reviewService.postReview(userId, writerId, reviewRequestDto);

        // then
        verify(reviewRepository, times(1)).save(any(Review.class));
        assertThat(user.getRating()).isCloseTo((4.5 * 2 + 5) / 3, within(0.0001));
    }

    @Test
    @DisplayName("postReview - 존재하지 않는 사용자에 대해 예외 발생")
    void postReview_ShouldThrowExceptionWhenUserNotFound() {
        // given
        Long userId = 1L;
        Long writerId = 2L;
        ReviewRequestDto reviewRequestDto = ReviewRequestDto.builder()
            .itemId(3L)
            .description("Great item!")
            .rating(5)
            .build();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> reviewService.postReview(userId, writerId, reviewRequestDto))
            .isInstanceOf(CustomException.class)
            .hasMessage(ErrorCode.USER_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("postReview - 존재하지 않는 아이템에 대해 예외 발생")
    void postReview_ShouldThrowExceptionWhenItemNotFound() {
        // given
        Long userId = 1L;
        Long writerId = 2L;
        Long itemId = 3L;

        User user = User.builder().userId(userId).build();
        User writer = User.builder().userId(writerId).build();

        ReviewRequestDto reviewRequestDto = ReviewRequestDto.builder()
            .itemId(itemId)
            .description("Great item!")
            .rating(5)
            .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.findById(writerId)).thenReturn(Optional.of(writer));
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> reviewService.postReview(userId, writerId, reviewRequestDto))
            .isInstanceOf(CustomException.class)
            .hasMessage(ErrorCode.ITEM_NOT_FOUND.getMessage());
    }
}
