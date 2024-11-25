package LinkerBell.campus_market_spring.repository;

import static org.assertj.core.api.Assertions.*;

import LinkerBell.campus_market_spring.domain.Blacklist;
import LinkerBell.campus_market_spring.domain.Role;
import LinkerBell.campus_market_spring.domain.User;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class BlacklistRepositoryTest {

    @Autowired
    private BlacklistRepository blacklistRepository;

    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder().userId(1L).loginEmail("test@example.com")
            .role(Role.USER).build();
        user = userRepository.save(user);
    }

    @Test
    @DisplayName("블랙리스트 저장 테스트")
    public void saveBlacklistTest() {
        // given
        LocalDateTime period = LocalDateTime.now().plusDays(2);
        Blacklist blacklist = Blacklist.builder().user(user).reason("test reason")
            .endDate(period).build();
        // when
        Blacklist savedBlacklist = blacklistRepository.save(blacklist);
        // then
        assertThat(savedBlacklist).isNotNull();
        assertThat(savedBlacklist.getUser()).isEqualTo(user);
    }

    @Test
    @DisplayName("사용자로 블랙리스트 찾기 테스트")
    public void findBlacklistByUserTest() {
        // given
        LocalDateTime period = LocalDateTime.now().plusDays(2);
        Blacklist blacklist = Blacklist.builder().user(user).reason("test reason")
            .endDate(period).build();
        Blacklist savedBlacklist = blacklistRepository.save(blacklist);

        // when
        Blacklist findBlacklist = blacklistRepository.findByUser(user).orElse(null);

        // then
        assertThat(findBlacklist).isNotNull();
        assertThat(findBlacklist.getUser()).isEqualTo(user);
    }
}
