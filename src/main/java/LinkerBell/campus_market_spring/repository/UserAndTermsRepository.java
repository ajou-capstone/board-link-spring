package LinkerBell.campus_market_spring.repository;

import LinkerBell.campus_market_spring.domain.User;
import LinkerBell.campus_market_spring.domain.UserAndTerms;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserAndTermsRepository extends JpaRepository<UserAndTerms, Long> {

    @Query(value = "select ut from UserAndTerms ut " +
        "join fetch ut.user as u " +
        "where u.userId = :userId")
    List<UserAndTerms> findAllByUserId(@Param("userId") Long userId);

    List<UserAndTerms> findAllByUser(User user);
}
