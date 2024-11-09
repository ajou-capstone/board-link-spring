package LinkerBell.campus_market_spring.repository;

import static org.assertj.core.api.Assertions.*;

import LinkerBell.campus_market_spring.domain.User;
import LinkerBell.campus_market_spring.domain.UserFcmToken;
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
}
