package LinkerBell.campus_market_spring.controller;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import LinkerBell.campus_market_spring.dto.AuthUserDto;
import LinkerBell.campus_market_spring.dto.TermsResponseDto;
import LinkerBell.campus_market_spring.global.error.GlobalExceptionHandler;
import LinkerBell.campus_market_spring.service.TermsService;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@ExtendWith(MockitoExtension.class)
class TermsControllerTest {

    MockMvc mockMvc;

    private MockLoginArgumentResolver mockLoginArgumentResolver = new MockLoginArgumentResolver();

    @InjectMocks
    TermsController termsController;

    @Mock
    TermsService termsService;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(termsController)
            .setControllerAdvice(GlobalExceptionHandler.class)
            .setCustomArgumentResolvers(mockLoginArgumentResolver)
            .build();
    }

    @Test
    @DisplayName("사용자 약관 동의 정보 가져오기 테스트")
    public void getTermsInfoTest() throws Exception {
        // given
        TermsResponseDto termsResponseDto = TermsResponseDto.builder()
            .id(1L).title("test").url("test_url").isRequired(true).isAgree(false).build();
        given(termsService.getTermsAndUserAgreementInfo(anyLong()))
            .willReturn(Lists.newArrayList(termsResponseDto));
        // when
        ResultActions resultActions = mockMvc.perform(get("/api/v1/terms"));

        // then
        resultActions.andExpect(status().isOk()).andExpect(jsonPath("$.terms[0].url")
            .value("test_url")).andDo(print()).andReturn();

        then(termsService).should(times(1)).getTermsAndUserAgreementInfo(anyLong());
    }

    @Test
    @DisplayName("사용자 약관 동의 정보 업데이트 테스트")
    public void updateTermsUserInfoTest() throws Exception {
        // given
        String termRequestJson = """
            {
                "terms" : [
                    {
                        "id" : 1,
                        "isAgree" : true
                    }
                ]
            }
            """;

        willDoNothing().given(termsService).agreeTerms(anyLong(), anyList());

        // when
        ResultActions resultActions = mockMvc.perform(post("/api/v1/terms/agreement")
            .contentType(MediaType.APPLICATION_JSON).content(termRequestJson));

        // then
        resultActions.andDo(print()).andReturn();

        then(termsService).should(times(1)).agreeTerms(anyLong(), anyList());
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