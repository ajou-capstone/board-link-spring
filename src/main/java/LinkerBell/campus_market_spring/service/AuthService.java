package LinkerBell.campus_market_spring.service;

import LinkerBell.campus_market_spring.domain.Role;
import LinkerBell.campus_market_spring.domain.User;
import LinkerBell.campus_market_spring.dto.AuthResponseDto;
import LinkerBell.campus_market_spring.global.jwt.JwtUtils;
import LinkerBell.campus_market_spring.repository.UserRepository;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    @Value("${google.client}")
    private String GOOGLE_CLIENT_ID;
    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;

    @Transactional
    public ResponseEntity<AuthResponseDto> googleLogin(String googleToken) {
        GoogleIdToken idToken = null;
        log.info("google Token: " + googleToken);
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    GsonFactory.getDefaultInstance())
                    .setAudience(Collections.singletonList(GOOGLE_CLIENT_ID))
                    .build();
            idToken = verifier.verify(googleToken);

        } catch (GeneralSecurityException | IOException e) {
            log.error("token verify error");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (IllegalArgumentException e) {
            log.error("token is bad");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (idToken == null) {
            log.error("token is null");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        GoogleIdToken.Payload payload = idToken.getPayload();

        String email = payload.getEmail();
        boolean emailVerified = Boolean.valueOf(payload.getEmailVerified());
        if (!emailVerified) {
            log.info("email is not verified");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        // idToken is verified
        Optional<User> userOpt = userRepository.findByLoginEmail(email);
        userOpt.ifPresentOrElse(
                user -> {},
                () -> {
                    userRepository.save(User.builder()
                            .loginEmail(email)
                            .role(Role.GUEST)
                            .build()
                    );
                }
        );
        User user = userRepository.findByLoginEmail(email).orElseThrow(
                () -> new RuntimeException("User not found")
        );

        String accessToken = jwtUtils.generateAccessToken(user.getLoginEmail(), user.getRole());
        String refreshToken = jwtUtils.generateRefreshToken(user.getLoginEmail(), user.getRole());

        user.setRefreshToken(refreshToken);

        AuthResponseDto authResponseDto = AuthResponseDto.builder()
                                            .accessToken(accessToken)
                                            .refreshToken(refreshToken)
                                            .build();

        return ResponseEntity.ok(authResponseDto);
    }
}
