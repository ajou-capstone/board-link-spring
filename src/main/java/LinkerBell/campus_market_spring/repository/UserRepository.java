package LinkerBell.campus_market_spring.repository;

import LinkerBell.campus_market_spring.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByLoginEmail(String email);

}
