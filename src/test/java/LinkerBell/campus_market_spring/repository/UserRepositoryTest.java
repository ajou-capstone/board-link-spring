package LinkerBell.campus_market_spring.repository;

import LinkerBell.campus_market_spring.domain.Role;
import LinkerBell.campus_market_spring.domain.User;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.util.MockUtil;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.*;

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
