package LinkerBell.campus_market_spring.repository;

import LinkerBell.campus_market_spring.domain.Role;
import LinkerBell.campus_market_spring.domain.User;
import LinkerBell.campus_market_spring.dto.UserInfoDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{
    Optional<User> findByLoginEmail(String email);

    Optional<User> findBySchoolEmail(String schoolEmail);

    @Query(value = """
    SELECT new LinkerBell.campus_market_spring.dto.UserInfoDto(
        u.userId, u.nickname, u.profileImage, u.rating, u.isDeleted,
        b.reason, c.universityName, c.region, b.endDate
    )
    FROM User u
    LEFT JOIN Blacklist b ON u = b.user
    LEFT JOIN Campus c ON c = u.campus
    WHERE u.role = :role
    """)
    Slice<UserInfoDto> findUserInfoAll(@Param("role") Role role, Pageable pageable);

    @Query(value = """
    SELECT new LinkerBell.campus_market_spring.dto.UserInfoDto(
        u.userId, u.nickname, u.profileImage, u.rating, u.isDeleted,
        b.reason, c.universityName, c.region, b.endDate
    )
    FROM User u
    LEFT JOIN Blacklist b ON u = b.user
    LEFT JOIN Campus c ON c = u.campus
    WHERE u.userId=:userId
    """)
    Optional<UserInfoDto> findUserInfoByUserId(@Param("userId") Long userId);
}
