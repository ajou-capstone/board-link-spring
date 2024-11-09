package LinkerBell.campus_market_spring.repository;

import LinkerBell.campus_market_spring.domain.User;
import LinkerBell.campus_market_spring.domain.UserFcmToken;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserFcmTokenRepository extends JpaRepository<UserFcmToken, Long> {

    Optional<UserFcmToken> findByFcmToken(String fcmToken);
}
