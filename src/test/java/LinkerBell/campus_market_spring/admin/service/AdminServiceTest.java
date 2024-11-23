package LinkerBell.campus_market_spring.admin.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import LinkerBell.campus_market_spring.domain.Role;
import LinkerBell.campus_market_spring.domain.User;
import LinkerBell.campus_market_spring.domain.UserReport;
import LinkerBell.campus_market_spring.dto.AuthResponseDto;
import LinkerBell.campus_market_spring.global.error.ErrorCode;
import LinkerBell.campus_market_spring.global.error.exception.CustomException;
import LinkerBell.campus_market_spring.global.jwt.JwtUtils;
import LinkerBell.campus_market_spring.repository.BlacklistRepository;
import LinkerBell.campus_market_spring.repository.UserReportRepository;
import LinkerBell.campus_market_spring.repository.UserRepository;
import LinkerBell.campus_market_spring.service.GoogleAuthService;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @InjectMocks
    private AdminService adminService;

    @Mock
    private GoogleAuthService googleAuthService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserReportRepository userReportRepository;

    @Mock
    private BlacklistRepository blacklistRepository;

    @Mock
    private JwtUtils jwtUtils;

    @Test
    @DisplayName("관리자 로그인 테스트")
    public void adminLoginTest() {
        // given
        User admin = createAdmin();
        String accessToken = "testAccessToken";
        String refreshToken = "testRefreshToken";
        given(googleAuthService.getEmailWithVerifyIdToken(anyString()))
            .willReturn("test@example.com");
        given(userRepository.findByLoginEmail("test@example.com")).willReturn(Optional.ofNullable(admin));
        given(jwtUtils.generateAccessToken(anyLong(), anyString(), any(Role.class)))
            .willReturn(accessToken);
        given(jwtUtils.generateRefreshToken(anyLong(), anyString(), any(Role.class)))
            .willReturn(refreshToken);
        // when
        AuthResponseDto responseDto = adminService.adminLogin("testIdToken");
        // then
        assertThat(responseDto).isNotNull();
        assertThat(responseDto.getAccessToken()).isEqualTo(accessToken);
        assertThat(responseDto.getRefreshToken()).isEqualTo(refreshToken);
    }

    @Test
    @DisplayName("관리자가 아닌 사용자 로그인 테스트")
    public void adminLoginWithoutAdminRoleTest() {
        // given
        User user = createUser(100L);
        given(googleAuthService.getEmailWithVerifyIdToken(anyString()))
            .willReturn("test@example.com");
        given(userRepository.findByLoginEmail("test@example.com")).willReturn(Optional.ofNullable(user));
        // when & then
        assertThatThrownBy(() -> adminService.adminLogin("testIdToken"))
            .isInstanceOf(CustomException.class)
            .hasMessage(ErrorCode.NOT_ADMINISTRATOR.getMessage());
    }

    @Test
    @DisplayName("사용자 신고 처리 테스트")
    public void receiveUserReportTest() {
        // given
        User user = createUser(100L);
        User target = createUser(101L);
        UserReport userReport = createUserReport(user, target, 1L);

        given(userReportRepository.findById(anyLong())).willReturn(Optional.ofNullable(userReport));
        given(blacklistRepository.findByUser(any(User.class))).willReturn(Optional.empty());

        // when
        adminService.receiveUserReport(userReport.getUserReportId(), true, "test Reason", 5);

        // then
        then(blacklistRepository).should(times(1)).save(assertArg(b -> {
            assertThat(b).isNotNull();
            assertThat(b.getEndDate()).isAfter(LocalDateTime.now());
        }));
    }

    private User createAdmin() {
        return User.builder()
            .loginEmail("test@example.com")
            .role(Role.ADMIN)
            .userId(1L).build();
    }

    private User createUser(Long userId) {
        return User.builder()
            .loginEmail("test" + userId + "@example.com")
            .role(Role.USER)
            .userId(userId).build();
    }

    private UserReport createUserReport(User user, User target, Long id) {
        return UserReport.builder()
            .userReportId(id)
            .user(user)
            .target(target)
            .description("test description" + id)
            .isCompleted(false).build();
    }
}
