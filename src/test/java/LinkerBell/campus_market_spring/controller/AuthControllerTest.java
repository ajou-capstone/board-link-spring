package LinkerBell.campus_market_spring.controller;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.handler;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import LinkerBell.campus_market_spring.dto.AuthResponseDto;
import LinkerBell.campus_market_spring.dto.AuthUserDto;
import LinkerBell.campus_market_spring.global.error.ErrorCode;
import LinkerBell.campus_market_spring.global.error.GlobalExceptionHandler;
import LinkerBell.campus_market_spring.global.error.exception.CustomException;
import LinkerBell.campus_market_spring.service.AuthService;
import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.client.RequestMatcher;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    private MockMvc mockMvc;

    private MockLoginArgumentResolver mockLoginArgumentResolver = new MockLoginArgumentResolver();
    @Mock
    AuthService authService;

    @InjectMocks
    AuthController authController;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController)
            .setControllerAdvice(GlobalExceptionHandler.class)
            .setCustomArgumentResolvers(mockLoginArgumentResolver)
            .build();
    }

    @Test
    public void loginTest() throws Exception {
        // given
        RequestDto requestDto = new RequestDto("123", "123");
        AuthResponseDto authResponseDto = AuthResponseDto.builder().accessToken("testAccessToken")
            .refreshToken("testRefreshToken").build();
        given(authService.googleLogin(Mockito.anyString())).willReturn(authResponseDto);

        // when
        ResultActions resultActions = mockMvc.perform(
            post("/api/v1/auth/login").content(new Gson().toJson(requestDto))
                .contentType(MediaType.APPLICATION_JSON));
        // then
        MvcResult mvcResult = resultActions.andExpect(
                jsonPath("$.accessToken").value("testAccessToken"))
            .andExpect(jsonPath("$.refreshToken").value("testRefreshToken"))
            .andExpect(status().isOk()).andDo(print()).andReturn();
    }

    @Test
    public void refreshTest() throws Exception {
        // given
        AuthResponseDto authResponseDto = AuthResponseDto.builder().accessToken("testAccessToken")
            .refreshToken("testRefreshToken").build();
        given(authService.reissueJwt(Mockito.any())).willReturn(authResponseDto);

        // when
        ResultActions resultActions = mockMvc.perform(
            post("/api/v1/auth/refresh").header("refresh", "refreshToken"));

        // then
        MvcResult mvcResult = resultActions.andExpect(
                jsonPath("$.accessToken").value("testAccessToken"))
            .andExpect(jsonPath("$.refreshToken").value("testRefreshToken"))
            .andExpect(status().isOk()).andDo(print()).andReturn();
    }

    @Test
    public void refreshJWTExceptionTest() throws Exception {
        // given
        AuthResponseDto authResponseDto = AuthResponseDto.builder().accessToken("testAccessToken")
            .refreshToken("testRefreshToken").build();
        given(authService.reissueJwt(Mockito.any())).willThrow(new CustomException(ErrorCode.INVALID_JWT));

        // when
        ResultActions resultActions = mockMvc.perform(
            post("/api/v1/auth/refresh").header("refresh", "refreshToken"));

        // then
        MvcResult mvcResult = resultActions.andExpect(status().isUnauthorized()).andDo(print()).andReturn();
    }

    @Test
    public void refreshUserNotFoundExceptionTest() throws Exception {
        // given
        AuthResponseDto authResponseDto = AuthResponseDto.builder().accessToken("testAccessToken")
            .refreshToken("testRefreshToken").build();
        given(authService.reissueJwt(Mockito.any())).willThrow(new CustomException(ErrorCode.USER_NOT_FOUND));

        // when
        ResultActions resultActions = mockMvc.perform(
            post("/api/v1/auth/refresh").header("refresh", "refreshToken"));

        // then
        MvcResult mvcResult = resultActions.andExpect(status().isNotFound()).andDo(print()).andReturn();
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

    static class RequestDto {

        String idToken;
        String firebaseToken;

        public RequestDto() {
        }

        public RequestDto(String idToken, String firebaseToken) {
            this.idToken = idToken;
            this.firebaseToken = firebaseToken;
        }
    }
}