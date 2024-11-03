package LinkerBell.campus_market_spring.repository;

import LinkerBell.campus_market_spring.domain.Role;
import LinkerBell.campus_market_spring.domain.User;
import LinkerBell.campus_market_spring.global.error.ErrorCode;
import LinkerBell.campus_market_spring.global.error.exception.CustomException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Test
    public void saveTest() {
        // given
        User user = createUser();
        userRepository.save(user);
        // when
        Optional<User> userOpt = userRepository.findByLoginEmail("tbc@gmail.com");
        userOpt.ifPresentOrElse(u -> {
        }, () -> {
            userRepository.save(createUser2());
        });

        User user2 = userRepository.findByLoginEmail("tbc@gmail.com")
                .orElseThrow(() -> new RuntimeException("User Not Found"));

        user2.setRefreshToken("test124");

        // then
        assertThat(user2.getRole()).isEqualTo(Role.USER);
        assertThat(user2.getRefreshToken()).isEqualTo("test124");
    }

    @Test
    public void saveTestWithAnotherMethod() {
        // given
        User user = createUser();
        userRepository.save(user);
        // when
        User user1 = userRepository.findByLoginEmail("tbc@gmail.com")
                .orElseGet(() -> createUser2());

        user1.setRefreshToken("test124");

        userRepository.save(user1);

        User user2 = userRepository.findByLoginEmail("tbc@gmail.com")
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
                .hasMessageContaining("사용자가 존재하지 않습니다.");
    }

    @Test
    @DisplayName("캠퍼스가 없는 유저를 찾았을 때 에러 테스트")
    public void UserHasNotCampusTest() throws Exception {
        // given
        User savedUser = userRepository.save(createUser());

        User findUser = userRepository.findById(savedUser.getUserId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        assertThatThrownBy(() -> {
            if (findUser.getCampus() == null) {
                throw new CustomException(ErrorCode.CAMPUS_NOT_FOUND);
            }
        }).isInstanceOf(CustomException.class)
                .hasMessageContaining("캠퍼스가 존재하지 않습니다.");
    }

    private User createUser() {
        return User.builder()
                .loginEmail("abc@gmail.com")
                .role(Role.GUEST)
                .build();
    }

    private User createUser2() {
        return User.builder()
                .loginEmail("tbc@gmail.com")
                .role(Role.USER)
                .build();
    }
}
