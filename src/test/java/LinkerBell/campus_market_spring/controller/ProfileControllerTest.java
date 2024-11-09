package LinkerBell.campus_market_spring.controller;

import static org.mockito.BDDMockito.*;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import LinkerBell.campus_market_spring.dto.AuthUserDto;
import LinkerBell.campus_market_spring.dto.CampusRequestDto;
import LinkerBell.campus_market_spring.dto.CampusResponseDto;
import LinkerBell.campus_market_spring.dto.OtherProfileResponseDto;
import LinkerBell.campus_market_spring.dto.ProfileRequestDto;
import LinkerBell.campus_market_spring.dto.ProfileResponseDto;
import LinkerBell.campus_market_spring.global.error.GlobalExceptionHandler;
import LinkerBell.campus_market_spring.service.ProfileService;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@ExtendWith(MockitoExtension.class)
class ProfileControllerTest {

    private MockMvc mockMvc;

    private MockLoginArgumentResolver mockLoginArgumentResolver = new MockLoginArgumentResolver();
    @Mock
    ProfileService profileService;

    @InjectMocks
    ProfileController profileController;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(profileController)
            .setControllerAdvice(GlobalExceptionHandler.class)
            .setCustomArgumentResolvers(mockLoginArgumentResolver)
            .build();
    }

    @Test
    @DisplayName("본인 프로필 정보 가져오기 테스트")
    public void getProfileTest() throws Exception {
        // given
        ProfileResponseDto profileResponseDto = ProfileResponseDto.builder().userId(1L)
            .nickname("nickname").loginEmail("abc@gmail.com").campusId(123L).build();
        given(profileService.getMyProfile(Mockito.anyLong())).willReturn(profileResponseDto);

        // when
        ResultActions resultActions = mockMvc.perform(get("/api/v1/profile/me"));

        // then
        MvcResult mvcResult = resultActions.andExpect(status().isOk())
            .andExpect(jsonPath("$.userId").value(1L)).andDo(print()).andReturn();
        System.out.println("mvcResult : " + mvcResult.getResponse().getContentAsString());
    }

    @Test
    @DisplayName("닉네임, 사진 업데이트하기 테스트")
    public void updateProfileTest() throws Exception {
        // given
        ProfileRequestDto profileRequestDto = ProfileRequestDto.builder().nickname("new_nickname")
            .profileImage("test_imageUrl").build();
        ProfileResponseDto profileResponseDto = ProfileResponseDto.builder().userId(1L)
            .nickname("new_nickname").profileImage("test_imageUrl").build();
        given(profileService.saveProfile(Mockito.anyLong(), Mockito.anyString(),
            Mockito.anyString())).willReturn(profileResponseDto);

        // when
        ResultActions resultActions = mockMvc.perform(post("/api/v1/profile/me").contentType(
            MediaType.APPLICATION_JSON).content(new Gson().toJson(profileRequestDto)));

        // then
        MvcResult mvcResult = resultActions.andExpect(status().isOk())
            .andExpect(jsonPath("$.nickname").value("new_nickname"))
            .andExpect(jsonPath("$.profileImage").value("test_imageUrl"))
            .andDo(print()).andReturn();
    }

    @Test
    @DisplayName("캠퍼스 정보 가져오기 테스트")
    public void getCampusTest() throws Exception {
        // given
        List<CampusResponseDto> campusResponseDtoList = createCampusList();
        given(profileService.getCampusList(Mockito.anyLong()))
            .willReturn(Lists.newArrayList(campusResponseDtoList));
        // when
        ResultActions resultActions = mockMvc.perform(get("/api/v1/profile/campus"));
        // then
        MvcResult mvcResult = resultActions.andExpect(status().isOk())
            .andExpect(jsonPath("$.campuses").isArray()).andExpect(jsonPath("$.campuses[0].region")
                .value("수원")).andDo(print()).andReturn();
    }

    @Test
    @DisplayName("캠퍼스 정보 저장하기 테스트")
    public void saveCampusTest() throws Exception {
        // given
        ProfileResponseDto profileResponseDto = ProfileResponseDto.builder().userId(1L).campusId(1L)
            .build();
        CampusRequestDto campusRequestDto = CampusRequestDto.builder().campusId(1L).build();
        given(profileService.saveCampus(Mockito.anyLong(), Mockito.anyLong()))
            .willReturn(profileResponseDto);
        // when
        ResultActions resultActions = mockMvc.perform(
            post("/api/v1/profile/campus").contentType(MediaType.APPLICATION_JSON)
                .content(new Gson().toJson(campusRequestDto)));

        // then
        MvcResult mvcResult = resultActions.andExpect(status().isNoContent()).andDo(print())
            .andReturn();
    }

    @Test
    @DisplayName("다른 사용자 프로필 가져오기 테스트")
    public void getOtherProfileTest() throws Exception {
        // given
        OtherProfileResponseDto responseDto = OtherProfileResponseDto.builder()
            .id(1L).rating(0.0).profileImage("test_url").nickname("test_user").build();
        given(profileService.getOtherProfile(anyLong(), anyLong())).willReturn(responseDto);

        // when
        mockMvc.perform(get("/api/v1/profile/" + responseDto.getId()))
            .andExpect(status().isOk()).andDo(print()).andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.profileImage").value("test_url"))
                    .andExpect(jsonPath("$.nickname").value("test_user"))
                        .andExpect(jsonPath("$.rating").value(0.0));

        // then
        then(profileService).should(times(1)).getOtherProfile(anyLong(),argThat(v -> {
            assertThat(v).isEqualTo(responseDto.getId());
            return true;
        }));
    }

    private List<CampusResponseDto> createCampusList() {
        List<CampusResponseDto> campusResponseDtoList = new ArrayList<>();
        campusResponseDtoList.add(new CampusResponseDto(1L, "수원"));
        campusResponseDtoList.add(new CampusResponseDto(2L, "서울"));
        return campusResponseDtoList;
    }

    static class MockLoginArgumentResolver implements HandlerMethodArgumentResolver {

        @Override
        public boolean supportsParameter(MethodParameter parameter) {
            return true;
        }

        @Override
        public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
            return AuthUserDto.builder().userId(1L).build();
        }

    }
}
