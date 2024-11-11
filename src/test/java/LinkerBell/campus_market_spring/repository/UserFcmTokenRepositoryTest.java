package LinkerBell.campus_market_spring.repository;

import static org.assertj.core.api.Assertions.assertThat;

import LinkerBell.campus_market_spring.domain.User;
import LinkerBell.campus_market_spring.domain.UserFcmToken;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class UserFcmTokenRepositoryTest {

    @Autowired
    UserFcmTokenRepository userFcmTokenRepository;

    @Autowired
    UserRepository userRepository;

    @Test
    @DisplayName("userFcmToken 정보 가져오기 테스트")
    public void getUserFcmTokenTest() {
        // given
        User user = User.builder().userId(1L).loginEmail("test@example.com").build();
        user = userRepository.save(user);
        UserFcmToken userFcmToken = UserFcmToken.builder().user(user).fcmToken("fcmToken").build();
        userFcmToken = userFcmTokenRepository.save(userFcmToken);
        // when
        Optional<UserFcmToken> fcmTokenOptional = userFcmTokenRepository.findByFcmToken("fcmToken");

        // then
        assertThat(fcmTokenOptional.isPresent()).isTrue();
        UserFcmToken fcmToken = fcmTokenOptional.get();
        assertThat(fcmToken.getFcmToken()).isEqualTo("fcmToken");
        assertThat(fcmToken.getUser().getUserId()).isEqualTo(user.getUserId());
    }

    @Test
    @DisplayName("userId로 fcmTokens 가져오는 테스트")
    public void findFcmTokenByUser_UserIdTest() throws Exception {
        //given
        User user = User.builder().userId(1L).loginEmail("test@example.com").build();
        user = userRepository.save(user);
        UserFcmToken userFcmToken1 = UserFcmToken.builder().user(user).fcmToken("fcmToken1")
            .build();
        UserFcmToken userFcmToken2 = UserFcmToken.builder().user(user).fcmToken("fcmToken2")
            .build();
        userFcmTokenRepository.save(userFcmToken1);
        userFcmTokenRepository.save(userFcmToken2);

        //when
        List<String> fcmTokens = userFcmTokenRepository.findFcmTokenByUser_UserId(
            user.getUserId());

        //then
        assertThat(fcmTokens.size()).isEqualTo(2);
        assertThat(fcmTokens).contains(userFcmToken1.getFcmToken(), userFcmToken2.getFcmToken());
    }
}
