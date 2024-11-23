package LinkerBell.campus_market_spring.repository;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import LinkerBell.campus_market_spring.domain.Campus;
import LinkerBell.campus_market_spring.domain.Role;
import LinkerBell.campus_market_spring.domain.User;
import LinkerBell.campus_market_spring.domain.UserReport;
import LinkerBell.campus_market_spring.domain.UserReportCategory;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.jdbc.core.JdbcTemplate;

@DataJpaTest
class UserReportRepositoryTest {
    @Autowired
    private UserReportRepository userReportRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CampusRepository campusRepository;
    @Autowired
    private JdbcTemplate jdbcTemplate;

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

        List<UserReport> userReports = new ArrayList<>();
        for (int i = 1; i <= 20; i++) {
            UserReport userReport = UserReport.builder()
                .isCompleted(i % 2 == 0).user(user).target(other)
                .description("test reason" + i)
                .category(UserReportCategory.values()[(i - 1) % UserReportCategory.values().length])
                .build();

            userReports.add(userReport);
        }
        userReports = userReportRepository.saveAll(userReports);
        int i = 0;
        for(UserReport userReport : userReports) {
            LocalDateTime date = LocalDateTime.of(2023, LocalDate.now().getMonth(), 25 - i, 0 ,0);
            Timestamp timestamp = Timestamp.valueOf(date);
            String sql = "UPDATE user_report SET created_date=? WHERE user_report_id= ?;";
            jdbcTemplate.update(sql, timestamp, userReport.getUserReportId());
            i += 1;
            userReport.setCreatedDate(date);
            userReportRepository.save(userReport);
        }
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

    @Test
    @DisplayName("사용자 신고 리스트 테스트")
    public void getUserReportListTest() {
        // given
        Sort sort = Sort.by(Direction.DESC, "createdDate");
        Pageable pageable = PageRequest.of(0, 20, sort);
        // when
        Slice<UserReport> slice = userReportRepository.findUserReports(pageable);

        // then
        assertThat(slice).isNotNull();
        assertThat(slice.getContent().size()).isEqualTo(10);
        assertThat(slice.getContent().get(0).getCategory())
            .isEqualTo(UserReportCategory.values()[0]);
        assertThat(slice.getContent().get(0).getCreatedDate())
            .isAfter(slice.getContent().get(1).getCreatedDate());
    }

    @Test
    @DisplayName("사용자 신고 찾기 테스트")
    public void findUserReportTest() {
        // given
        String description =  UserReportCategory.FRAUD.getDescription();
        UserReportCategory category = UserReportCategory.FRAUD;
        UserReport userReport = UserReport.builder()
            .user(user).target(other).description(description).category(category).build();
        userReport = userReportRepository.save(userReport);
        // when
        Optional<UserReport> findUserReportOpt = userReportRepository.findById(userReport.getUserReportId());
        // then
        assertThat(findUserReportOpt).isPresent();
        UserReport findUserReport = findUserReportOpt.get();
        assertThat(findUserReport.getDescription()).isEqualTo(description);
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
