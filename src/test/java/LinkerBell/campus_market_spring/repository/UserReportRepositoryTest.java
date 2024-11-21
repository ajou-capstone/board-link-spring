package LinkerBell.campus_market_spring.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.*;

import LinkerBell.campus_market_spring.domain.Campus;
import LinkerBell.campus_market_spring.domain.Item;
import LinkerBell.campus_market_spring.domain.ItemReport;
import LinkerBell.campus_market_spring.domain.Role;
import LinkerBell.campus_market_spring.domain.User;
import LinkerBell.campus_market_spring.domain.UserReport;
import LinkerBell.campus_market_spring.domain.UserReportCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class UserReportRepositoryTest {
    @Autowired
    private UserReportRepository userReportRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CampusRepository campusRepository;

    private User user;
    private User other;
    private Campus campus;

    @BeforeEach
    public void setUp() {
        campus = createCampus();
        campus = campusRepository.save(campus);
        user = createUser(1L, campus);
        other = createUser(2L, campus);
        user = userRepository.save(user);
        other = userRepository.save(other);
    }

    @Test
    @DisplayName("사용자 신고 테스트")
    public void userReportTest() {
        // given
        String description =  UserReportCategory.FRAUD.getDescription();
        UserReportCategory category = UserReportCategory.FRAUD;
        UserReport userReport = UserReport.builder()
            .user(user).target(other).description(description).category(category).build();
        // when
        UserReport findUserReportDto = userReportRepository.save(userReport);
        // then
        assertThat(findUserReportDto).isNotNull();
        assertThat(findUserReportDto.getDescription()).isEqualTo(description);
    }

    private Campus createCampus() {
        return Campus.builder()
            .campusId(1L)
            .email("testUniv@example.com")
            .region("korea")
            .universityName("testUniv")
            .build();
    }

    private User createUser(Long userId, Campus campus) {
        return User.builder()
            .userId(userId)
            .role(Role.USER)
            .loginEmail("testEmail")
            .campus(campus)
            .schoolEmail("testSchool")
            .nickname("user" + userId).build();
    }


}