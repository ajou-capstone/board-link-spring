package LinkerBell.campus_market_spring.repository;

import LinkerBell.campus_market_spring.domain.UserFcmToken;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserFcmTokenRepository extends JpaRepository<UserFcmToken, Long> {

    Optional<UserFcmToken> findByFcmToken(String fcmToken);

    @Query("select uft.fcmToken from UserFcmToken uft " +
        "where uft.user.userId = :userId")
    List<String> findFcmTokenByUser_UserId(@Param("userId") Long userId);
}
