package LinkerBell.campus_market_spring.service;

import LinkerBell.campus_market_spring.domain.Role;
import LinkerBell.campus_market_spring.domain.User;
import LinkerBell.campus_market_spring.dto.AuthResponseDto;
import LinkerBell.campus_market_spring.dto.AuthUserDto;
import LinkerBell.campus_market_spring.global.error.ErrorCode;
import LinkerBell.campus_market_spring.global.error.exception.CustomException;
import LinkerBell.campus_market_spring.global.jwt.JwtUtils;
import LinkerBell.campus_market_spring.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
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

    public AuthResponseDto userLogin(String googleToken, String firebaseToken) {
        String email = googleAuthService.getEmailWithVerifyIdToken(googleToken);

        User user = userRepository.findByLoginEmail(email)
            .orElseGet(() -> createNewUser(email));

        // TODO: check logout and blacklist

        String accessToken = jwtUtils.generateAccessToken(user.getUserId(), user.getLoginEmail(), user.getRole());
        String refreshToken = jwtUtils.generateRefreshToken(user.getUserId(), user.getLoginEmail(), user.getRole());

        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        fcmService.saveUserFcmToken(firebaseToken, user);

        return AuthResponseDto.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken).build();
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
            log.error("Not matched with db");
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
}
