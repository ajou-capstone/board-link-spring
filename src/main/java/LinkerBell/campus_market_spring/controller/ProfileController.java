package LinkerBell.campus_market_spring.controller;

import LinkerBell.campus_market_spring.dto.AuthUserDto;
import LinkerBell.campus_market_spring.dto.ProfileRequestDto;
import LinkerBell.campus_market_spring.dto.ProfileResponseDto;
import LinkerBell.campus_market_spring.global.auth.Login;
import LinkerBell.campus_market_spring.service.ProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/profile")
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping("/me")
    public ResponseEntity<ProfileResponseDto> getMyProfile(@Login AuthUserDto user) {
        ProfileResponseDto profileResponseDto = profileService.getMyProfile(user.getUserId());
        return ResponseEntity.ok(profileResponseDto);
    }

    @PostMapping("/me")
    public ResponseEntity<ProfileResponseDto> updateMyProfile(@Login AuthUserDto user,
        @RequestBody ProfileRequestDto profileRequestDto) {
        ProfileResponseDto profileResponseDto = profileService.saveProfile(user.getUserId(),
            profileRequestDto.getNickname(), profileRequestDto.getProfileImage());
        return ResponseEntity.ok(profileResponseDto);
    }

}