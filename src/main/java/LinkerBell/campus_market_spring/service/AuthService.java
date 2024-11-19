package LinkerBell.campus_market_spring.service;

import LinkerBell.campus_market_spring.domain.Role;
import LinkerBell.campus_market_spring.domain.User;
import LinkerBell.campus_market_spring.domain.UserFcmToken;
import LinkerBell.campus_market_spring.dto.AuthResponseDto;
import LinkerBell.campus_market_spring.dto.AuthUserDto;
import LinkerBell.campus_market_spring.global.error.ErrorCode;
import LinkerBell.campus_market_spring.global.error.exception.CustomException;
import LinkerBell.campus_market_spring.global.jwt.JwtUtils;
import LinkerBell.campus_market_spring.repository.UserFcmTokenRepository;
import LinkerBell.campus_market_spring.repository.UserRepository;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    @Value("${google.client}")
    private String GOOGLE_CLIENT_ID;
    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;
    private final UserFcmTokenRepository userFcmTokenRepository;

    public AuthResponseDto googleLogin(String googleToken, String firebaseToken) {
        log.info("google Token: " + googleToken);
        log.info("firebase Token: " + firebaseToken);
        GoogleIdToken idToken = getGoogleIdToken(googleToken);

        if (idToken == null) {
            log.error("idToken is null");
            throw new CustomException(ErrorCode.INVALID_GOOGLE_TOKEN);
        }

        String email = getEmailFromGoogleIdToken(idToken);

        User user = userRepository.findByLoginEmail(email)
            .orElseGet(() -> createNewUser(email));

        // TODO: check logout and blacklist

        String accessToken = jwtUtils.generateAccessToken(user.getUserId(), user.getLoginEmail(), user.getRole());
        String refreshToken = jwtUtils.generateRefreshToken(user.getUserId(), user.getLoginEmail(), user.getRole());

        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        saveUserFcmToken(firebaseToken, user);

        return AuthResponseDto.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken).build();
    }

    private void saveUserFcmToken(String firebaseToken, User user) {
        userFcmTokenRepository.findByFcmToken(firebaseToken).ifPresentOrElse(userFcmToken -> {
                log.info("already exist.");
                userFcmToken.setLastModifiedDate(LocalDateTime.now());
                userFcmTokenRepository.save(userFcmToken);
            },
            () -> {
                log.info("not exist.");
                UserFcmToken userFcmToken = UserFcmToken.builder().fcmToken(firebaseToken)
                    .user(user).build();
                userFcmTokenRepository.save(userFcmToken);
            });
    }

    private User createNewUser(String email) {
        User newUser = User.builder()
            .loginEmail(email)
            .role(Role.GUEST)
            .build();

        return userRepository.save(newUser);
    }

    private String getEmailFromGoogleIdToken(GoogleIdToken idToken) {
        GoogleIdToken.Payload payload = idToken.getPayload();

        boolean emailVerified = payload.getEmailVerified();
        if (!emailVerified) {
            throw new CustomException(ErrorCode.NOT_VERIFIED_EMAIL);
        }

        return payload.getEmail();
    }

    private GoogleIdToken getGoogleIdToken(String googleToken) {
        GoogleIdToken idToken = null;
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                GsonFactory.getDefaultInstance())
                .setAudience(Collections.singletonList(GOOGLE_CLIENT_ID))
                .build();
            idToken = verifier.verify(googleToken);

        } catch (GeneralSecurityException | IOException e) {
            throw new CustomException(ErrorCode.UNVERIFIED_GOOGLE_TOKEN);
        } catch (IllegalArgumentException e) {
            log.error("IllegalArgumentException");
            throw new CustomException(ErrorCode.INVALID_GOOGLE_TOKEN);
        }
        return idToken;
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
