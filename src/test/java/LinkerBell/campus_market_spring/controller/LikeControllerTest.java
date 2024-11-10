package LinkerBell.campus_market_spring.controller;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;
<<<<<<< HEAD

=======
>>>>>>> 059a706 (fix: 좋아요 누르기 기능 수정)
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import LinkerBell.campus_market_spring.dto.AuthUserDto;
import LinkerBell.campus_market_spring.dto.LikeResponseDto;
import LinkerBell.campus_market_spring.global.error.GlobalExceptionHandler;
import LinkerBell.campus_market_spring.service.LikeService;
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
class LikeControllerTest {
    MockMvc mockMvc;

    private MockLoginArgumentResolver mockLoginArgumentResolver = new MockLoginArgumentResolver();

    @InjectMocks
    LikeController likeController;

    @Mock
    LikeService likeService;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(likeController)
            .setControllerAdvice(GlobalExceptionHandler.class)
            .setCustomArgumentResolvers(mockLoginArgumentResolver)
            .build();
    }

    @Test
    @DisplayName("좋아요 추가하기 테스트")
    public void doLikeTest() throws Exception {
        // given
        LikeResponseDto likeResponseDto = LikeResponseDto.builder()
            .likeId(1L).isLike(true).itemId(1L).build();
        given(likeService.likeItem(anyLong(), anyLong())).willReturn(likeResponseDto);
        // when & then
        mockMvc.perform(post("/api/v1/items/" + 1L + "/likes"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.likeId").value(likeResponseDto.getLikeId()))
            .andExpect(jsonPath("$.itemId").value(likeResponseDto.getItemId()))
            .andExpect(jsonPath("$.isLike").value(true))
            .andDo(print());

        then(likeService).should(times(1)).likeItem(anyLong(), assertArg(itemId -> {
            assertThat(itemId).isEqualTo(1L);
        }));
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
