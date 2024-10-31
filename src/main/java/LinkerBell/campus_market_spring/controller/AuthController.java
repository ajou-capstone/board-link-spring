package LinkerBell.campus_market_spring.controller;

import LinkerBell.campus_market_spring.dto.AuthRequestDto;
import LinkerBell.campus_market_spring.dto.AuthResponseDto;
import LinkerBell.campus_market_spring.dto.AuthUserDto;
import LinkerBell.campus_market_spring.dto.MailRequestDto;
import LinkerBell.campus_market_spring.dto.MailResponseDto;
import LinkerBell.campus_market_spring.dto.VerificationCodeRequestDto;
import LinkerBell.campus_market_spring.global.auth.Login;
import LinkerBell.campus_market_spring.service.AuthService;
import LinkerBell.campus_market_spring.service.SesService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;
    private final SesService sesService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@RequestBody AuthRequestDto authRequestDto) {
        AuthResponseDto authResponseDto = authService.googleLogin(authRequestDto.getIdToken());
        return ResponseEntity.ok(authResponseDto);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponseDto> reissueJwt(HttpServletRequest request) {
        AuthResponseDto authResponseDto = authService.reissueJwt(request);
        return ResponseEntity.ok(authResponseDto);
    }

    @PostMapping("/emails/send")
    public ResponseEntity<MailResponseDto> sendEmail(@RequestBody MailRequestDto mailRequestDto) {
        MailResponseDto mailResponseDto = sesService.processEmail(mailRequestDto.getEmail());
        return ResponseEntity.ok(mailResponseDto);
    }

    @PostMapping("/emails/verify")
    public ResponseEntity<?> verifyCode(@Login AuthUserDto user,
        @RequestBody VerificationCodeRequestDto requestDto) {
        String email = sesService.verifyTokenAndCode(requestDto.getToken(), requestDto.getVerifyCode());
        authService.saveSchoolEmail(user.getUserId(), email);
        return ResponseEntity.noContent().build();
    }
}
