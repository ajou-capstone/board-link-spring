package LinkerBell.campus_market_spring.repository;

import LinkerBell.campus_market_spring.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByLoginEmail(String email);

    @Query("select u from User u " +
            "join fetch u.campus " +
            "where u.userId = :userId")
    Optional<User> findUserWithCampus(@Param("userId") Long userId);

    Optional<User> findBySchoolEmail(String schoolEmail);
}
