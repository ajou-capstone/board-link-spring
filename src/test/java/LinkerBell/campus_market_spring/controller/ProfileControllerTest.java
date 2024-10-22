package LinkerBell.campus_market_spring.controller;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import LinkerBell.campus_market_spring.dto.AuthUserDto;
import LinkerBell.campus_market_spring.dto.ProfileRequestDto;
import LinkerBell.campus_market_spring.dto.ProfileResponseDto;
import LinkerBell.campus_market_spring.global.error.GlobalExceptionHandler;
import LinkerBell.campus_market_spring.service.ProfileService;
import com.google.gson.Gson;
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
            .andExpect(jsonPath("$.profileImage").value("test_imageUrl")).andDo(print())
            .andReturn();

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