package LinkerBell.campus_market_spring.service;

import LinkerBell.campus_market_spring.domain.Campus;
import LinkerBell.campus_market_spring.domain.User;
import LinkerBell.campus_market_spring.dto.CampusResponseDto;
import LinkerBell.campus_market_spring.dto.OtherProfileResponseDto;
import LinkerBell.campus_market_spring.dto.ProfileResponseDto;
import LinkerBell.campus_market_spring.global.error.ErrorCode;
import LinkerBell.campus_market_spring.global.error.exception.CustomException;
import LinkerBell.campus_market_spring.repository.CampusRepository;
import LinkerBell.campus_market_spring.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ProfileService {

    private final UserRepository userRepository;
    private final CampusRepository campusRepository;

    @Transactional(readOnly = true)
    public ProfileResponseDto getMyProfile(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        return createMyProfileResponseDto(user);
    }

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

    @Transactional(readOnly = true)
    public List<CampusResponseDto> getCampusList(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        return findCampusList(user.getSchoolEmail()).stream()
            .map(campus -> new CampusResponseDto(campus.getCampusId(), campus.getRegion()))
            .toList();
    }

    public ProfileResponseDto saveCampus(Long userId, Long campusId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        List<Campus> campusList = findCampusList(user.getSchoolEmail());

        Campus findCampus = campusList.stream()
            .filter(campus -> campus.getCampusId().equals(campusId))
            .findFirst().orElseThrow(() -> new CustomException(ErrorCode.CAMPUS_NOT_FOUND));

        user.setCampus(findCampus);

        return createMyProfileResponseDto(user);
    }

    @Transactional(readOnly = true)
    public OtherProfileResponseDto getOtherProfile(Long userId, Long otherId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        User other = userRepository.findById(otherId)
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (user.getCampus() != other.getCampus()) {
            throw new CustomException(ErrorCode.NOT_MATCH_USER_CAMPUS);
        }
        
        return createOtherProfileResponseDto(other);
    }

    private OtherProfileResponseDto createOtherProfileResponseDto(User user) {
        return OtherProfileResponseDto.builder()
            .id(user.getUserId())
            .nickname(user.getNickname())
            .profileImage(user.getProfileImage())
            .rating(user.getRating()).build();
    }

    private List<Campus> findCampusList(String schoolEmail) {
        String email = extractEmail(schoolEmail);

        List<Campus> campusList = campusRepository.findByEmail(email);

        if (campusList.isEmpty()) {
            throw new CustomException(ErrorCode.INVALID_SCHOOL_EMAIL);
        }

        return campusList;
    }

    private String extractEmail(String schoolEmail) {
        if (schoolEmail == null) {
            throw new CustomException(ErrorCode.SCHOOL_EMAIL_NOT_FOUND);
        }

        return schoolEmail.substring(schoolEmail.indexOf("@") + 1);
    }
}
