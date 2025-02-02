package LinkerBell.campus_market_spring.service;

import LinkerBell.campus_market_spring.domain.Role;
import LinkerBell.campus_market_spring.domain.User;
import LinkerBell.campus_market_spring.dto.AuthResponseDto;
import LinkerBell.campus_market_spring.dto.AuthUserDto;
import LinkerBell.campus_market_spring.global.error.ErrorCode;
import LinkerBell.campus_market_spring.global.error.exception.CustomException;
import LinkerBell.campus_market_spring.global.jwt.JwtUtils;
import LinkerBell.campus_market_spring.global.redis.RedisService;
import LinkerBell.campus_market_spring.repository.BlacklistRepository;
import LinkerBell.campus_market_spring.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;
    private final FcmService fcmService;
    private final GoogleAuthService googleAuthService;
    private final BlacklistRepository blacklistRepository;
    private final RedisService redisService;

    public AuthResponseDto userLogin(String googleToken, String firebaseToken) {
        String email = googleAuthService.getEmailWithVerifyIdToken(googleToken);

        User user = userRepository.findByLoginEmail(email)
            .orElseGet(() -> createNewUser(email));

        checkBlacklist(user.getUserId());
        checkWithdrawUser(user);

        String accessToken = jwtUtils.generateAccessToken(user.getUserId(), user.getLoginEmail(), user.getRole());
        String refreshToken = jwtUtils.generateRefreshToken(user.getUserId(), user.getLoginEmail(), user.getRole());

        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        fcmService.saveUserFcmToken(firebaseToken, user);

        return AuthResponseDto.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken).build();
    }

    private void checkWithdrawUser(User user) {
        if (user.isDeleted()) {
            throw new CustomException(ErrorCode.ALREADY_WITHDRAW_USER);
        }
    }

    private void checkBlacklist(Long userId) {
        blacklistRepository.findByUser_UserId(userId).ifPresent(blacklist -> {
            String formattedDate = blacklist.getEndDate()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            String message = String.format("이용 제한 사유: %s\n 기간: %s까지",
                blacklist.getReason(), formattedDate);
            throw new CustomException(ErrorCode.BLACKLIST_USER, message);
        });
    }

    private User createNewUser(String email) {
        User newUser = User.builder()
            .loginEmail(email)
            .role(Role.USER)
            .build();

        return userRepository.save(newUser);
    }

    public AuthUserDto getUserByJwt(String token) {
        String email = jwtUtils.getEmail(token);
        Long userId = jwtUtils.getUserId(token);

        checkBlacklist(userId);
        return AuthUserDto.builder()
            .userId(userId)
            .loginEmail(email)
            .build();
    }

    public AuthResponseDto reissueJwt(HttpServletRequest request) {
        String refreshToken = jwtUtils.resolveRefreshToken(request);

        if (!jwtUtils.validateToken(refreshToken)) {
            throw new CustomException(ErrorCode.INVALID_JWT);
        }

        String email = jwtUtils.getEmail(refreshToken);

        User user = userRepository.findByLoginEmail(email)
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (!refreshToken.equals(user.getRefreshToken())) {
            throw new CustomException(ErrorCode.INVALID_JWT);
        }

        String reissuedAccessToken = jwtUtils.generateAccessToken(user.getUserId(), user.getLoginEmail(),
            user.getRole());
        String reissuedRefreshToken = jwtUtils.generateRefreshToken(user.getUserId(), user.getLoginEmail(),
            user.getRole());

        user.setRefreshToken(reissuedRefreshToken);

        return AuthResponseDto.builder()
            .accessToken(reissuedAccessToken)
            .refreshToken(reissuedRefreshToken).build();
    }

    public void saveSchoolEmail(Long userId, String schoolEmail) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        user.setSchoolEmail(schoolEmail);
    }

    public void logout(Long userId, HttpServletRequest request) {
        String accessToken = jwtUtils.resolveToken(request);

        if (!jwtUtils.validateToken(accessToken)) {
            throw new CustomException(ErrorCode.INVALID_JWT);
        }

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        user.setRefreshToken(null);
        redisService.setLogout(accessToken);

        fcmService.deleteFcmTokenAllByUserId(user.getUserId());
    }

    public void withdraw(Long userId, HttpServletRequest request) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        if (user.isDeleted()) {
            throw new CustomException(ErrorCode.ALREADY_WITHDRAW_USER);
        }
        user.setDeleted(true);
        user.setRefreshToken(null);

        String accessToken = jwtUtils.resolveToken(request);

        if (!jwtUtils.validateToken(accessToken)) {
            throw new CustomException(ErrorCode.INVALID_JWT);
        }

        redisService.setLogout(accessToken);
        fcmService.deleteFcmTokenAllByUserId(user.getUserId());

    }
}
