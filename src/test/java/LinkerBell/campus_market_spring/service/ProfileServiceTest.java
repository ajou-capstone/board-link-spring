package LinkerBell.campus_market_spring.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;


import LinkerBell.campus_market_spring.domain.Campus;
import LinkerBell.campus_market_spring.domain.Role;
import LinkerBell.campus_market_spring.domain.User;
import LinkerBell.campus_market_spring.dto.CampusResponseDto;
import LinkerBell.campus_market_spring.dto.ProfileResponseDto;
import LinkerBell.campus_market_spring.global.error.ErrorCode;
import LinkerBell.campus_market_spring.global.error.exception.CustomException;
import LinkerBell.campus_market_spring.repository.CampusRepository;
import LinkerBell.campus_market_spring.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProfileServiceTest {
    @Mock
    UserRepository userRepository;

    @Mock
    CampusRepository campusRepository;

    @InjectMocks
    ProfileService profileService;

    @Test
    @DisplayName("프로필 정보 가져오기 테스트")
    public void getProfileTest() {
        // given
        User user = createUser();
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
        User user = createUser();
        given(userRepository.findById(1L)).willReturn(Optional.ofNullable(user));
        // when
        ProfileResponseDto profileResponseDto = profileService.saveProfile(1L, "new nickname", "imageUrl");
        // then
        assertThat(profileResponseDto).isNotNull();
        assertThat(profileResponseDto.getNickname()).isEqualTo("new nickname");
        assertThat(profileResponseDto.getProfileImage()).isEqualTo("imageUrl");
    }

    @Test
    @DisplayName("닉네임만 변경 테스트")
    public void onlyUpdateNicknameTest() {
        // given
        User user = createUser();
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
        User user = createUser();
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
        User user = createUser();
        Campus campus = createCampus();
        given(campusRepository.findByEmail(Mockito.anyString())).willReturn(Lists.newArrayList(campus));
        given(userRepository.findById(Mockito.anyLong())).willReturn(Optional.ofNullable(user));
        // when
        List<CampusResponseDto> campusList = profileService.getCampusList(user.getUserId());

        // then
        assertThat(campusList.size()).isGreaterThan(0);
    }

//    @Test
//    public void findCampusTest() {
//        // given
//        String schoolEmail = "abc@asd.ac.ad";
//        given(campusRepository.findByEmail(Mockito.anyString())).willReturn(Lists.newArrayList());
//        // when & then
//        assertThatThrownBy(() -> profileService.findCampusList(schoolEmail)).isInstanceOf(CustomException.class);
//    }

    @Test
    @DisplayName("캠퍼스 저장 예외 테스트")
    public void saveCampusErrorTest() {
        // given
        User user = createUser();
        Campus campus = createCampus();
        Campus diffCampus = createDiffCampus();

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
        User user = createUser();
        Campus campus = createCampus();
        given(userRepository.findById(Mockito.anyLong())).willReturn(Optional.ofNullable(user));
        given(campusRepository.findByEmail(Mockito.anyString())).willReturn(Lists.newArrayList(campus));
        given(campusRepository.findById(Mockito.anyLong())).willReturn(Optional.ofNullable(campus));
        // when
        ProfileResponseDto profileResponseDto = profileService.saveCampus(user.getUserId(), campus.getCampusId());

        // then
        assertThat(profileResponseDto).isNotNull();
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
            .role(Role.GUEST).build();
    }

    private Campus createDiffCampus() {
        return Campus.builder()
            .campusId(10L)
            .email("test.ac.kr")
            .region("수원시")
            .universityName("테스트대학교").build();
    }

}