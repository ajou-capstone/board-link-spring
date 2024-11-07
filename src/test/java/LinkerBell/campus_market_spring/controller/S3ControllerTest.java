package LinkerBell.campus_market_spring.controller;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import LinkerBell.campus_market_spring.dto.AuthUserDto;
import LinkerBell.campus_market_spring.dto.S3ResponseDto;
import LinkerBell.campus_market_spring.global.error.GlobalExceptionHandler;
import LinkerBell.campus_market_spring.service.S3Service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@ExtendWith(MockitoExtension.class)
class S3ControllerTest {

    MockMvc mockMvc;

    MockLoginArgumentResolver mockLoginArgumentResolver = new MockLoginArgumentResolver();

    @Mock
    S3Service s3Service;

    @InjectMocks
    S3Controller s3Controller;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(s3Controller)
            .setControllerAdvice(GlobalExceptionHandler.class)
            .setCustomArgumentResolvers(mockLoginArgumentResolver)
            .build();
    }

    @Test
    @DisplayName("presigned Put Url 발급 테스트")
    public void getPresignedPutUrlTest() throws Exception {
        // given
        S3ResponseDto s3ResponseDto = new S3ResponseDto("test-presigned-url", "test-s3-url");
        given(s3Service.createPreSignedPutUrl(anyString())).willReturn(s3ResponseDto);

        // when & then
        mockMvc.perform(get("/api/v1/s3/presigned-url")
            .param("fileName", "test"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.presignedUrl").value(s3ResponseDto.getPresignedUrl()))
            .andExpect(jsonPath("$.s3url").value(s3ResponseDto.getS3url()))
            .andDo(print());
    }

    @Test
    @DisplayName("파일 이름이 공백일 경우")
    public void fileNameBlankExceptionTest() throws Exception {

        // when & then
        mockMvc.perform(get("/api/v1/s3/presigned-url")
                .param("fileName", " "))
            .andExpect(status().is4xxClientError())
            .andDo(print());
    }

    @Test
    @DisplayName("파일 이름이 들어오지 않을 경우")
    public void fileNameNullExceptionTest() throws Exception {

        // when & then
        mockMvc.perform(get("/api/v1/s3/presigned-url"))
            .andExpect(status().is4xxClientError())
            .andDo(print());
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