package LinkerBell.campus_market_spring.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.mockito.BDDMockito.*;

import LinkerBell.campus_market_spring.domain.Role;
import LinkerBell.campus_market_spring.domain.User;
import LinkerBell.campus_market_spring.dto.AuthResponseDto;
import LinkerBell.campus_market_spring.dto.AuthUserDto;
import LinkerBell.campus_market_spring.global.jwt.JwtUtils;
import LinkerBell.campus_market_spring.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.util.ReflectionTestUtils;


@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    UserRepository userRepository;

    @InjectMocks
    JwtUtils jwtUtils;

    @InjectMocks
    AuthService authService;

    private User user;

    private String jwtToken;

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(jwtUtils, "secretKey", "TESTKEY1TESTKEY2TESTKEY3TESTKEY4TESTKEY5TESTKEY6");
        ReflectionTestUtils.setField(jwtUtils, "accessExpiredTime", Long.valueOf(360000));
        ReflectionTestUtils.setField(jwtUtils, "refreshExpiredTime", Long.valueOf(120960000));

        ReflectionTestUtils.setField(authService, "jwtUtils", jwtUtils);

        user = getUser();
        jwtToken = jwtUtils.generateRefreshToken(user.getUserId(), user.getLoginEmail(), user.getRole());
    }

    @Test
    @DisplayName("사용자 정보 찾기 테스트")
    public void getUserByLoginEmailTest() {
        AuthUserDto userDto = authService.getUserByJwt(jwtToken);

        assertThat(userDto.getUserId()).isEqualTo(1L);
        assertThat(userDto.getLoginEmail()).isEqualTo("abc@gmail.com");
    }

    @Test
    @DisplayName("jwt 재발급 테스트")
    public void reissueJwtTest() {
        // given
        String refreshToken = jwtUtils.generateRefreshToken(user.getUserId(), user.getLoginEmail(), user.getRole());
        user.setRefreshToken(refreshToken);
        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
        mockHttpServletRequest.addHeader("refresh", "Bearer " + refreshToken);

        given(userRepository.findByLoginEmail(Mockito.anyString())).willReturn(Optional.ofNullable(user));
        // when
        AuthResponseDto authResponseDto = authService.reissueJwt(mockHttpServletRequest);
        // then
        assertThat(authResponseDto.getAccessToken()).isNotNull();
        assertThat(authResponseDto.getRefreshToken()).isNotNull();

        assertThat(authResponseDto.getRefreshToken()).isEqualTo(user.getRefreshToken());
    }

    private  User getUser() {
        return User.builder()
            .userId(1L)
            .loginEmail("abc@gmail.com")
            .role(Role.GUEST)
            .build();
    }
}