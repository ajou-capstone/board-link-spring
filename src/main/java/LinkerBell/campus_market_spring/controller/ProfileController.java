package LinkerBell.campus_market_spring.controller;

import LinkerBell.campus_market_spring.dto.AuthUserDto;
import LinkerBell.campus_market_spring.dto.CollectionResponse.CampusCollectionResponseDto;
import LinkerBell.campus_market_spring.dto.CampusRequestDto;
import LinkerBell.campus_market_spring.dto.CampusResponseDto;
import LinkerBell.campus_market_spring.dto.OtherProfileResponseDto;
import LinkerBell.campus_market_spring.dto.ProfileRequestDto;
import LinkerBell.campus_market_spring.dto.ProfileResponseDto;
import LinkerBell.campus_market_spring.global.auth.Login;
import LinkerBell.campus_market_spring.service.ProfileService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    @GetMapping("/campus")
    public ResponseEntity<CampusCollectionResponseDto> getCampus(@Login AuthUserDto user) {
        List<CampusResponseDto> campusResponseDtoList =
            profileService.getCampusList(user.getUserId());
        return ResponseEntity.ok(CampusCollectionResponseDto.from(campusResponseDtoList));
    }

    @PostMapping("/campus")
    public ResponseEntity<ProfileResponseDto> saveCampus(@Login AuthUserDto user,
        @RequestBody CampusRequestDto campusRequestDto) {
        ProfileResponseDto profileResponseDto = profileService.saveCampus(user.getUserId(),
            campusRequestDto.getCampusId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<OtherProfileResponseDto> getOtherProfile(@Login AuthUserDto user,
        @PathVariable("userId") Long otherId) {
        OtherProfileResponseDto responseDto = profileService.getOtherProfile(user.getUserId(), otherId);
        return ResponseEntity.ok(responseDto);
    }
}
