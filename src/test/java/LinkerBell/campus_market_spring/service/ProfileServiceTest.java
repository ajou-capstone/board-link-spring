package LinkerBell.campus_market_spring.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;


import LinkerBell.campus_market_spring.domain.Role;
import LinkerBell.campus_market_spring.domain.User;
import LinkerBell.campus_market_spring.dto.ProfileResponseDto;
import LinkerBell.campus_market_spring.repository.UserRepository;
import java.util.Optional;
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

    private User createUser() {
        return User.builder()
            .loginEmail("abc@gmail.com")
            .userId(1L)
            .nickname("old_nickname")
            .profileImage("old_url")
            .role(Role.GUEST).build();
    }

}