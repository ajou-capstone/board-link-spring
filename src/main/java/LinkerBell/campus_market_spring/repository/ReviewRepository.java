package LinkerBell.campus_market_spring.repository;

import LinkerBell.campus_market_spring.domain.Item;
import LinkerBell.campus_market_spring.domain.Review;
import LinkerBell.campus_market_spring.domain.User;
import LinkerBell.campus_market_spring.dto.ReviewResponseDto;
import LinkerBell.campus_market_spring.dto.SliceResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long>, ReviewRepositoryCustom {

    @Query("SELECT COUNT(r) FROM Review r WHERE r.item.user = :user")
    int countReview(@Param("user") User user);

    boolean existsByUserAndItem(User user, Item item);

    @Query("SELECT r FROM Review r WHERE (r.user != :user) AND (r.item.user = :user OR r.item.userBuyer = :user)")
    Slice<Review> findReviewsToMe(@Param("user") User user, Pageable pageable);
}
