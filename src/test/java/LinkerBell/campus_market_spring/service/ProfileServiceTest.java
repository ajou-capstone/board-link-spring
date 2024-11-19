package LinkerBell.campus_market_spring.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;


import LinkerBell.campus_market_spring.domain.Campus;
import LinkerBell.campus_market_spring.domain.Role;
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
import javax.print.attribute.standard.MediaSize.Other;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class ProfileServiceTest {
    @Mock
    UserRepository userRepository;

    @Mock
    CampusRepository campusRepository;

    @InjectMocks
    ProfileService profileService;

    private User user;
    private User other;
    private Campus campus;
    private Campus diffCampus;

    @BeforeEach
    public void setUp() {
        user = createUser();
        other = createOther();
        campus = createCampus();
        diffCampus = createDiffCampus();
        ReflectionTestUtils.setField(profileService, "defaultProfileImage", "testDefaultImage");
    }

    @Test
    @DisplayName("프로필 정보 가져오기 테스트")
    public void getProfileTest() {
        // given
        given(userRepository.findById(1L)).willReturn(Optional.ofNullable(user));

        // when
        ProfileResponseDto profileResponseDto = profileService.getMyProfile(1L);

        // then
        assertThat(profileResponseDto).isNotNull();
        assertThat(profileResponseDto.getLoginEmail()).isEqualTo(user.getLoginEmail());
        assertThat(profileResponseDto.getUserId()).isEqualTo(user.getUserId());
    }

    @Test
    @DisplayName("프로필 정보 변경 테스트")
    public void saveProfileTest() {
        // given
        given(userRepository.findById(1L)).willReturn(Optional.ofNullable(user));
        // when
        ProfileResponseDto profileResponseDto = profileService.saveProfile(1L, "new nickname", "imageUrl");
        // then
        assertThat(profileResponseDto).isNotNull();
        assertThat(profileResponseDto.getNickname()).isEqualTo("new nickname");
        assertThat(profileResponseDto.getProfileImage()).isEqualTo("imageUrl");
    }

    @Test
    @DisplayName("프로필 처음 등록할 때 받은 이미지가 없을 때")
    public void saveProfileFirstAndNullImageTest() {
        // given
        user.setProfileImage(null);
        given(userRepository.findById(1L)).willReturn(Optional.ofNullable(user));

        // when
        ProfileResponseDto profileResponseDto = profileService.saveProfile(1L, "new nickname", null);
        // then
        assertThat(profileResponseDto).isNotNull();
        assertThat(profileResponseDto.getNickname()).isEqualTo("new nickname");
        assertThat(profileResponseDto.getProfileImage()).isNotNull();
        assertThat(profileResponseDto.getProfileImage()).isEqualTo("testDefaultImage");
    }

    @Test
    @DisplayName("프로필 변경 시 받은 이미지가 없을 때")
    public void saveProfileAndNullImageTest() {
        // given
        given(userRepository.findById(1L)).willReturn(Optional.ofNullable(user));

        // when
        ProfileResponseDto profileResponseDto = profileService.saveProfile(1L, "new nickname", null);
        // then
        assertThat(profileResponseDto).isNotNull();
        assertThat(profileResponseDto.getNickname()).isEqualTo("new nickname");
        assertThat(profileResponseDto.getProfileImage()).isNotNull();
        assertThat(profileResponseDto.getProfileImage()).isEqualTo(user.getProfileImage());
    }

    @Test
    @DisplayName("닉네임만 변경 테스트")
    public void onlyUpdateNicknameTest() {
        // given
        given(userRepository.findById(Mockito.anyLong())).willReturn(Optional.ofNullable(user));
        // when
        ProfileResponseDto profileResponseDto = profileService.saveProfile(user.getUserId(), "new_nickname", null);

        // then
        assertThat(profileResponseDto).isNotNull();
        assertThat(profileResponseDto.getNickname()).isEqualTo("new_nickname");
        assertThat(profileResponseDto.getProfileImage()).isEqualTo(user.getProfileImage());
    }

    @Test
    @DisplayName("프로필 이미지만 변경 테스트")
    public void onlyUpdateImageUrlTest() {
        // given
        given(userRepository.findById(Mockito.anyLong())).willReturn(Optional.ofNullable(user));

        // when
        ProfileResponseDto profileResponseDto = profileService.saveProfile(user.getUserId(), null, "new_url");

        // then
        assertThat(profileResponseDto).isNotNull();
        assertThat(profileResponseDto.getNickname()).isEqualTo(user.getNickname());
        assertThat(profileResponseDto.getProfileImage()).isEqualTo("new_url");
    }

    @Test
    @DisplayName("캠퍼스 정보 가져오기 테스트")
    public void getCampusTest() {
        // given
        given(campusRepository.findByEmail(Mockito.anyString())).willReturn(Lists.newArrayList(campus));
        given(userRepository.findById(Mockito.anyLong())).willReturn(Optional.ofNullable(user));
        // when
        List<CampusResponseDto> campusList = profileService.getCampusList(user.getUserId());

        // then
        assertThat(campusList.size()).isGreaterThan(0);
    }


    @Test
    @DisplayName("캠퍼스 저장 예외 테스트")
    public void saveCampusErrorTest() {
        // given
        given(userRepository.findById(Mockito.anyLong())).willReturn(Optional.ofNullable(user));
        given(campusRepository.findByEmail(Mockito.anyString())).willReturn(Lists.newArrayList(diffCampus));
        // when & then
        assertThatThrownBy(() -> profileService.saveCampus(user.getUserId(), campus.getCampusId()))
            .isInstanceOf(CustomException.class);
    }

    @Test
    @DisplayName("캠퍼스 저장 테스트")
    public void saveCampusTest() {
        // given
        given(userRepository.findById(Mockito.anyLong())).willReturn(Optional.ofNullable(user));
        given(campusRepository.findByEmail(Mockito.anyString())).willReturn(Lists.newArrayList(campus));
        
        // when
        ProfileResponseDto profileResponseDto = profileService.saveCampus(user.getUserId(), campus.getCampusId());

        // then
        assertThat(profileResponseDto).isNotNull();
    }

    @Test
    @DisplayName("다른 사용자 프로필 정보 가져오기")
    public void getOtherProfileTest() {
        // given
        user.setCampus(campus);
        other.setCampus(campus);

        given(userRepository.findById(user.getUserId())).willReturn(Optional.ofNullable(user));
        given(userRepository.findById(other.getUserId())).willReturn(Optional.ofNullable(other));
        // when
        OtherProfileResponseDto responseDto =
            profileService.getOtherProfile(user.getUserId(), other.getUserId());
        // then
        assertThat(responseDto).isNotNull();
        assertThat(responseDto.getId()).isEqualTo(other.getUserId());
        assertThat(responseDto.getProfileImage()).isEqualTo(other.getProfileImage());
        assertThat(responseDto.getNickname()).isEqualTo(other.getNickname());
        assertThat(responseDto.getRating()).isEqualTo(other.getRating());
    }

    @Test
    @DisplayName("캠퍼스가 다른 사용자 프로필 정보 확인 에러 테스트")
    public void diffCampusOtherProfileErrorTest() {
        // given
        user.setCampus(campus);
        other.setCampus(diffCampus);

        given(userRepository.findById(user.getUserId())).willReturn(Optional.ofNullable(user));
        given(userRepository.findById(other.getUserId())).willReturn(Optional.ofNullable(other));
        // when & then
        assertThatThrownBy(() -> profileService.getOtherProfile(user.getUserId(), other.getUserId()))
            .isInstanceOf(CustomException.class)
            .hasMessageContaining(ErrorCode.NOT_MATCH_USER_CAMPUS.getMessage());
    }

    private Campus createCampus() {
        return Campus.builder()
            .campusId(1L)
            .email("ajou.ac.kr")
            .region("수원시")
            .universityName("아주대학교").build();
    }


    private User createUser() {
        return User.builder()
            .loginEmail("abc@gmail.com")
            .userId(1L)
            .nickname("old_nickname")
            .profileImage("old_url")
            .schoolEmail("abc@ajou.ac.kr")
            .rating(0.0)
            .role(Role.USER).build();
    }

    private Campus createDiffCampus() {
        return Campus.builder()
            .campusId(10L)
            .email("test.ac.kr")
            .region("수원시")
            .universityName("테스트대학교").build();
    }

    private User createOther() {
        return User.builder().userId(11L).schoolEmail("test2.example2.com")
            .profileImage("other's profile image").rating(5.2).nickname("I'm other")
            .role(Role.USER).build();
    }

}