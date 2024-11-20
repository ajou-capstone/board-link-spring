package LinkerBell.campus_market_spring.repository;

import LinkerBell.campus_market_spring.domain.Item;
import LinkerBell.campus_market_spring.domain.Review;
import LinkerBell.campus_market_spring.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
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
}
