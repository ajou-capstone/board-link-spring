package LinkerBell.campus_market_spring.global.redis;

import static org.assertj.core.api.Assertions.*;

import LinkerBell.campus_market_spring.domain.Role;
import LinkerBell.campus_market_spring.global.jwt.JwtUtils;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
@SpringBootTest
class RedisTest {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Container
    public static GenericContainer redis = new GenericContainer(DockerImageName.parse("redis:6-alpine"))
        .withExposedPorts(6379);

    @BeforeEach
    public void setUp() {
        redis.start();
    }

    @DynamicPropertySource
    private static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", () -> redis.getMappedPort(6379).toString());
    }

    @Test
    @DisplayName("로그아웃 토큰 저장 테스트")
    public void saveLogoutTokenInRedisTest() {
        // given
        String logoutToken = UUID.randomUUID().toString();
        String value = "logout";

        // when
        redisTemplate.opsForValue().set(logoutToken, value);
        String findValue = (String) redisTemplate.opsForValue().get(logoutToken);

        // then
        assertThat(findValue).isNotNull();
        assertThat(findValue).isEqualTo(value);
    }

    @Test
    @DisplayName("로그아웃하지 않았을 경우 테스트")
    public void notLogoutTokenInRedisTest() {
        // given
        String logoutToken = UUID.randomUUID().toString();
        String value = "logout";

        String loginToken = UUID.randomUUID().toString();
        // when
        redisTemplate.opsForValue().set(logoutToken, value);
        String findValue = (String) redisTemplate.opsForValue().get(loginToken);

        // then
        assertThat(findValue).isNull();
    }

    @Nested
    @DisplayName("RedisService 테스트")
    class RedisServiceTest {

        @Autowired
        private RedisService redisService;
        @Autowired
        private JwtUtils jwtUtils;

        @DynamicPropertySource
        private static void registerProperties(DynamicPropertyRegistry registry) {
            registry.add("jwt.secret.key", () -> String.format("testestestesttesttestestestesttest"));
            registry.add("jwt.access.expiration", () -> String.format("%d", 1000));
            registry.add("jwt.refresh.expiration", () -> String.format("%d", 120960000));
        }

        @Test
        @DisplayName("로그아웃 토큰 저장 테스트")
        public void saveLogoutTokenTest() {
            // given
            String logoutToken = jwtUtils.generateRefreshToken(1L, "test@example.com", Role.USER);
            String value = "logout";

            // when
            redisService.setLogout(logoutToken);
            String findValue = redisService.getLogout(logoutToken);

            // then
            assertThat(value).isNotNull();
            assertThat(value).isEqualTo(findValue);
        }

        @Test
        @DisplayName("로그아웃 토큰 만료 시 삭제 테스트")
        public void expireLogoutTokenTest() {
            // given
            String logoutToken = jwtUtils.generateAccessToken(1L, "test@example.com", Role.USER);
            String value = "logout";

            // when
            try {
                redisService.setLogout(logoutToken);
                Thread.sleep(500);
            } catch (InterruptedException e) {
                fail("{}", e.getMessage());
            }
            String findValue = redisService.getLogout(logoutToken);

            // then
            assertThat(findValue).isNull();
        }

        @Test
        @DisplayName("로그아웃 하지 않은 토큰 테스트")
        public void notLogoutTokenTest() {
            // given
            String logoutToken = jwtUtils.generateAccessToken(1L, "test@example.com", Role.USER);

            // when
            String findValue = redisService.getLogout(logoutToken);

            // then
            assertThat(findValue).isNull();
        }
    }
}
