package LinkerBell.campus_market_spring.repository;

import LinkerBell.campus_market_spring.domain.Blacklist;
import LinkerBell.campus_market_spring.domain.Campus;
import LinkerBell.campus_market_spring.domain.Role;
import LinkerBell.campus_market_spring.domain.User;
import LinkerBell.campus_market_spring.dto.UserInfoDto;
import LinkerBell.campus_market_spring.global.error.ErrorCode;
import LinkerBell.campus_market_spring.global.error.exception.CustomException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    CampusRepository campusRepository;

    @Autowired
    BlacklistRepository blacklistRepository;

    @Nested
    @DisplayName("사용자용 테스트")
    class UserRepositoryTestForUser {

        private Campus campus;

        @BeforeEach
        public void setUp() {
            campus = createCampus(99L);
            campus = campusRepository.save(campus);
        }

        @Test
        public void saveTest() {
            // given
            User user = createUser(99L, campus, false);
            userRepository.save(user);
            // when
            Optional<User> userOpt = userRepository.findByLoginEmail(user.getLoginEmail());
            userOpt.ifPresentOrElse(u -> {
            }, () -> {
                userRepository.save(createUser(99L, campus, false));
            });

            User user2 = userRepository.findByLoginEmail(user.getLoginEmail())
                .orElseThrow(() -> new RuntimeException("User Not Found"));

            user2.setRefreshToken("test124");

            // then
            assertThat(user2.getRole()).isEqualTo(Role.USER);
            assertThat(user2.getRefreshToken()).isEqualTo("test124");
        }

        @Test
        public void saveTestWithAnotherMethod() {
            // given
            User user = createUser(99L, campus, false);
            userRepository.save(user);
            // when
            User user1 = userRepository.findByLoginEmail(user.getLoginEmail())
                .orElseGet(() -> createUser(99L, campus, false));

            user1.setRefreshToken("test124");

            userRepository.save(user1);

            User user2 = userRepository.findByLoginEmail(user.getLoginEmail())
                .orElseThrow(() -> new RuntimeException("User Not Found"));
            // then
            assertThat(user2.getRole()).isEqualTo(Role.USER);
            assertThat(user2.getRefreshToken()).isEqualTo("test124");
        }

        @Test
        @DisplayName("없는 유저 아이디 값으로 유저 찾을 때 에러나는 test 진행")
        public void findByIdTest() throws Exception {
            assertThatThrownBy(() -> {
                userRepository.findById(999999L)
                    .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
            }).isInstanceOf(CustomException.class)
                .hasMessageContaining(ErrorCode.USER_NOT_FOUND.getMessage());
        }

        @Test
        @DisplayName("캠퍼스가 없는 유저를 찾았을 때 에러 테스트")
        public void UserHasNotCampusTest() throws Exception {
            // given
            User user = User.builder().userId(99L).role(Role.USER).loginEmail("test@example.com")
                .build();
            User savedUser = userRepository.save(user);

            User findUser = userRepository.findById(savedUser.getUserId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

            assertThatThrownBy(() -> {
                if (findUser.getCampus() == null) {
                    throw new CustomException(ErrorCode.CAMPUS_NOT_FOUND);
                }
            }).isInstanceOf(CustomException.class)
                .hasMessageContaining("캠퍼스가 존재하지 않습니다.");
        }

        @Test
        @DisplayName("학교 이메일 중복 테스트")
        public void duplicateSchoolEmailTest() {
            // given
            User user = createUser(99L, campus, false);
            userRepository.save(user);

            // when
            boolean isPresent = userRepository.findBySchoolEmail(user.getSchoolEmail()).isPresent();
            // then
            assertThat(isPresent).isTrue();
        }
    }

    @Nested
    @DisplayName("관리자용 테스트")
    class UserRepositoryTestForAdmin {

        private Campus campus1;
        private Campus campus2;
        private List<User> users = new ArrayList<>();
        private List<Blacklist> blacklists = new ArrayList<>();

        @BeforeEach
        public void setUpNested() {
            campus1 = createCampus(1L);
            campus2 = createCampus(2L);

            List<Campus> campuses = List.of(campus1, campus2);

            campuses = campusRepository.saveAll(campuses);

            for (int i = 1; i <= 8; i++) {
                User user = createUser((long) i, campuses.get(i % campuses.size()), false);
                users.add(user);
            }
            users = userRepository.saveAll(users);

            for (int i = 0; i < 4; i++) {
                Blacklist blacklist = createBlacklist(users.get(i));
                blacklist = blacklistRepository.save(blacklist);
                blacklists.add(blacklist);
            }


        }

        @Test
        @DisplayName("사용자 정보 가져오기")
        public void getUsersWithBlackList() {
            // given
            Sort sort = Sort.by(Direction.ASC, "createdDate")
                .and(Sort.by(Direction.ASC, "userId"));

            Pageable pageable = PageRequest.of(0, 20, sort);
            // when
            Slice<UserInfoDto> slice = userRepository.findUserInfoAll(pageable);

            // then
            assertThat(slice).isNotNull();
            assertThat(slice.getContent()).isNotEmpty();

            List<UserInfoDto> userInfoDtoList = slice.getContent();
            assertThat(userInfoDtoList.size()).isEqualTo(users.size());
            assertThat(userInfoDtoList.get(0).getSuspendedReason()).isNotNull();
            assertThat(userInfoDtoList.get(0).getSuspendedReason())
                .isEqualTo(blacklists.get(0).getReason());
            assertThat(userInfoDtoList.get(0).getCampusName())
                .isEqualTo(users.get(0).getCampus().getUniversityName() + " "
                    + users.get(0).getCampus().getRegion());
            assertThat(userInfoDtoList.get(1).getCampusName())
                .isEqualTo(users.get(1).getCampus().getUniversityName() + " "
                    + users.get(1).getCampus().getRegion());
            assertThat(userInfoDtoList.get(4).getSuspendedReason()).isNull();
            assertThat(userInfoDtoList.get(5).getSuspendedDate()).isNull();
        }
    }


    private User createUser(Long id, Campus campus, Boolean isDeleted) {
        return User.builder()
            .userId(id)
            .loginEmail(String.format("test%d@example.com", id))
            .nickname(String.format("nickname%d", id))
            .isDeleted(isDeleted)
            .rating(5.5)
            .schoolEmail(String.format("test%d@%s", id, campus.getEmail()))
            .campus(campus)
            .role(Role.USER)
            .profileImage(String.format("testImage%d", id))
            .build();
    }

    private Campus createCampus(Long id) {
        return Campus.builder()
            .campusId(id)
            .universityName(String.format("test%dUniv", id))
            .email(String.format("school%d.ac.kr", id))
            .region(String.format("region%d", id))
            .build();
    }

    private Blacklist createBlacklist(User user) {
        return Blacklist.builder()
            .blacklistId(user.getUserId())
            .reason(String.format("reason%d", user.getUserId()))
            .user(user)
            .endDate(LocalDateTime.now().plusHours(user.getUserId())).build();
    }
}
