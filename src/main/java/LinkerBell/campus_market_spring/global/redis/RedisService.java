package LinkerBell.campus_market_spring.global.redis;

import LinkerBell.campus_market_spring.global.jwt.JwtUtils;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final JwtUtils jwtUtils;

    public void setLogout(String token) {
        Long expiration = jwtUtils.getExpirationTime(token);
        redisTemplate.opsForValue().set(token, "logout", Duration.ofMillis(expiration));
    }

    public String getLogout(String token) {
        return (String) redisTemplate.opsForValue().get(token);
    }
}
