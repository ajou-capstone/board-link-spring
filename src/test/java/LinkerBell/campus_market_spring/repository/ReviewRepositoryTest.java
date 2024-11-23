package LinkerBell.campus_market_spring.repository;

import LinkerBell.campus_market_spring.domain.Item;
import LinkerBell.campus_market_spring.domain.Review;
import LinkerBell.campus_market_spring.domain.User;
import LinkerBell.campus_market_spring.dto.ReviewResponseDto;
import LinkerBell.campus_market_spring.dto.SliceResponse;
import jakarta.persistence.EntityManager;
import java.util.stream.IntStream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
class ReviewRepositoryTest {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    EntityManager em;

    @Test
    @DisplayName("countReview 테스트 - 특정 사용자에게 남겨진 리뷰 수를 반환")
    void countReview_ShouldReturnCorrectCount() {
        // given
        // 1. User 생성 및 저장
        User user = User.builder()
            .nickname("testUser")
            .loginEmail("test@example.com")
            .rating(4.5)
            .build();
        userRepository.save(user);

        User user2 = User.builder()
            .nickname("testUser2")
            .loginEmail("test2@example.com")
            .rating(4.5)
            .build();
        userRepository.save(user2);

        // 2. Item 생성 및 저장
        Item item = Item.builder()
            .user(user) // 아이템의 소유자
            .title("Test Item")
            .description("This is a test item")
            .price(1000)
            .build();
        itemRepository.save(item);

        Item item2 = Item.builder()
            .user(user) // 아이템의 소유자
            .title("Test Item2")
            .description("This is a test item")
            .price(1000)
            .build();
        itemRepository.save(item2);

        // 3. Review 생성 및 저장
        Review review1 = Review.builder()
            .item(item) // 연관된 아이템
            .user(user2) // 리뷰 작성자
            .description("Great item!")
            .rating(5)
            .build();
        reviewRepository.save(review1);

        Review review2 = Review.builder()
            .item(item)
            .user(user2)
            .description("Amazing quality!")
            .rating(4)
            .build();
        reviewRepository.save(review2);

        Review review3 = Review.builder()
            .item(item2)
            .user(user2)
            .description("Amazing quality!")
            .rating(4)
            .build();
        reviewRepository.save(review3);

        // when
        int reviewCount = reviewRepository.countReview(user);

        // then
        assertThat(reviewCount).isEqualTo(3);
    }

    @Test
    @DisplayName("findAllByUserId 쿼리가 올바르게 작동하는지 테스트")
    void testFindAllByUserId() {
        // Given: 테스트 데이터 생성
        User user = User.builder()
            .nickname("testUser")
            .profileImage("profile.jpg")
            .build();
        userRepository.save(user);

        Item item = Item.builder()
            .title("testItem")
            .price(10000)
            .user(user)
            .thumbnail("thumbnail.jpg")
            .build();
        itemRepository.save(item);

        IntStream.range(1, 15).forEach(i -> {
            Review review = Review.builder()
                .user(user)
                .item(item)
                .description("Review " + i)
                .rating(4)
                .build();
            reviewRepository.save(review);
        });

        em.flush();
        em.clear();

        // When: findAllByUserId 호출
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdDate"));
        SliceResponse<ReviewResponseDto> result = reviewRepository.findAllByUserId(user.getUserId(),
            pageable);

        // Then: 검증
        assertThat(result).isNotNull();
        assertThat(result.getContent().size()).isEqualTo(10); // 페이지 크기만큼 데이터 반환
        assertThat(result.isHasNext()).isTrue(); // 다음 페이지 존재 여부 확인

        // 데이터 내용 검증
        ReviewResponseDto firstReview = result.getContent().get(0);
        assertThat(firstReview.getNickname()).isEqualTo(user.getNickname());
        assertThat(firstReview.getDescription()).contains("Review");
        assertThat(firstReview.getRating()).isEqualTo(4);
    }

}
