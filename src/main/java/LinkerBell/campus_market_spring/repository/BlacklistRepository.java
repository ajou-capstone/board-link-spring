package LinkerBell.campus_market_spring.repository;

import LinkerBell.campus_market_spring.domain.Blacklist;
import LinkerBell.campus_market_spring.domain.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlacklistRepository extends JpaRepository<Blacklist, Long> {

    Optional<Blacklist> findByUser(User user);
}
