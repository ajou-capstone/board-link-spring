package LinkerBell.campus_market_spring.service;

import LinkerBell.campus_market_spring.domain.Campus;
import LinkerBell.campus_market_spring.domain.User;
import LinkerBell.campus_market_spring.dto.ProfileResponseDto;
import LinkerBell.campus_market_spring.global.error.ErrorCode;
import LinkerBell.campus_market_spring.global.error.exception.CustomException;
import LinkerBell.campus_market_spring.repository.UserRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProfileService {

    private final UserRepository userRepository;

    public ProfileResponseDto getMyProfile(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        return createMyProfileResponseDto(user);
    }

    @Transactional
    public ProfileResponseDto saveProfile(Long userId, String nickname, String imageUrl) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        nickname = nickname == null ? user.getNickname() : nickname;
        imageUrl = imageUrl == null ? user.getProfileImage() : imageUrl;

        user.setNickname(nickname);
        user.setProfileImage(imageUrl);

        return createMyProfileResponseDto(user);
    }

    private ProfileResponseDto createMyProfileResponseDto(User user) {

        Long campusId = Optional.ofNullable(user.getCampus()).orElseGet(Campus::new)
            .getCampusId();

        return ProfileResponseDto.builder()
            .userId(user.getUserId())
            .campusId(campusId)
            .loginEmail(user.getLoginEmail())
            .schoolEmail(user.getSchoolEmail())
            .nickname(user.getNickname())
            .profileImage(user.getProfileImage())
            .rating(user.getRating()).build();
    }
}
