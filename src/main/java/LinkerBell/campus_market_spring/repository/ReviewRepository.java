package LinkerBell.campus_market_spring.repository;

import LinkerBell.campus_market_spring.domain.Review;
import LinkerBell.campus_market_spring.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    @Query("SELECT COUNT(r) FROM Review r WHERE r.item.user = :user")
    int countReview(@Param("user") User user);
}
