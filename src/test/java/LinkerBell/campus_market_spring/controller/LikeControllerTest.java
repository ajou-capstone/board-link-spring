package LinkerBell.campus_market_spring.controller;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import LinkerBell.campus_market_spring.dto.AuthUserDto;
import LinkerBell.campus_market_spring.dto.LikeDeleteResponseDto;
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
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
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
            .setCustomArgumentResolvers(
                new PageableHandlerMethodArgumentResolver(),
                mockLoginArgumentResolver)
            .build();
    }

    @Test
    @DisplayName("좋아요 추가하기 테스트")
    public void doLikeTest() throws Exception {
        // given
        Long itemId = 12L;
        Long likeId = 1L;
        LikeResponseDto likeResponseDto = LikeResponseDto.builder()
            .likeId(likeId).isLike(true).itemId(itemId).build();
        given(likeService.likeItem(anyLong(), anyLong())).willReturn(likeResponseDto);
        // when & then
        mockMvc.perform(post("/api/v1/items/" + itemId + "/likes"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.likeId").value(likeResponseDto.getLikeId()))
            .andExpect(jsonPath("$.itemId").value(likeResponseDto.getItemId()))
            .andExpect(jsonPath("$.isLike").value(likeResponseDto.isLike()))
            .andDo(print());

        then(likeService).should(times(1)).likeItem(anyLong(), assertArg(id -> {
            assertThat(id).isEqualTo(itemId);
        }));
    }

    @Test
    @DisplayName("좋아요 목록 가져오기 테스트")
    public void getLikeListTest() throws Exception {
        // given
        // when & then
        mockMvc.perform(get("/api/v1/items/likes")
                .queryParam("page", "0")
                .queryParam("size", "10"))
            .andExpect(status().isOk())
            .andDo(print());


    }

    @Test
    @DisplayName("좋아요 삭제하기 테스트")
    public void deleteLikeTest() throws Exception {
        // given
        Long itemId = 11L;
        LikeDeleteResponseDto responseDto = LikeDeleteResponseDto.builder()
            .itemId(itemId).isLike(false).build();
        given(likeService.deleteLike(anyLong(), anyLong())).willReturn(responseDto);
        // when & then
        mockMvc.perform(delete("/api/v1/items/" + itemId + "/likes"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.itemId").value(responseDto.getItemId()))
            .andExpect(jsonPath("$.isLike").value(responseDto.isLike()))
            .andDo(print());

        then(likeService).should(times(1)).deleteLike(anyLong(), assertArg(id -> {
            assertThat(id).isEqualTo(itemId);
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
