package LinkerBell.campus_market_spring.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import LinkerBell.campus_market_spring.dto.AuthUserDto;
import LinkerBell.campus_market_spring.dto.ReviewRequestDto;
import LinkerBell.campus_market_spring.global.error.GlobalExceptionHandler;
import LinkerBell.campus_market_spring.service.ReviewService;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@ExtendWith(MockitoExtension.class)
class ReviewControllerTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final MockLoginArgumentResolver mockLoginArgumentResolver = new MockLoginArgumentResolver();

    @InjectMocks
    private ReviewController reviewController;

    @Mock
    private ReviewService reviewService;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(reviewController)
            .setControllerAdvice(GlobalExceptionHandler.class) // GlobalExceptionHandler 적용
            .setCustomArgumentResolvers(mockLoginArgumentResolver)
            .build();
    }

    @Test
    @DisplayName("POST /api/v1/users/{userId}/reviews - 유효하지 않은 리뷰 요청: description이 200자를 초과함")
    void postReview_InvalidDescriptionTooLong() throws Exception {
        // Given
        String longDescription = "A".repeat(201); // 201자를 넘는 설명

        ReviewRequestDto invalidRequest = ReviewRequestDto.builder()
            .itemId(1L)
            .description(longDescription)
            .rating(5)
            .build();

        // When & Then
        mockMvc.perform(post("/api/v1/users/1/reviews")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
            .andExpect(status().isBadRequest());
    }


    @Test
    @DisplayName("POST /api/v1/users/{userId}/reviews - 유효하지 않은 리뷰 요청: rating 범위 초과")
    void postReview_InvalidRatingTooHigh() throws Exception {
        // Given
        ReviewRequestDto invalidRequest = ReviewRequestDto.builder()
            .itemId(1L)
            .description("Valid description")
            .rating(15) // rating의 유효 범위를 초과
            .build();

        // When & Then
        mockMvc.perform(post("/api/v1/users/1/reviews")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/v1/users/{userId}/reviews - 유효하지 않은 요청: rating 범위 미달")
    void postReview_InvalidRatingTooLow() throws Exception {
        // Given
        ReviewRequestDto invalidRequest = ReviewRequestDto.builder()
            .itemId(1L)
            .description("Valid description")
            .rating(-1) // rating이 너무 낮음
            .build();

        // When & Then
        mockMvc.perform(post("/api/v1/users/1/reviews")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/v1/users/{userId}/reviews - 유효하지 않은 요청: JSON 형식 오류")
    void postReview_InvalidJsonFormat() throws Exception {
        // Given
        String invalidJson = "{invalid}";

        // When & Then
        mockMvc.perform(post("/api/v1/users/1/reviews")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
            .andExpect(status().isBadRequest());
    }

    static class MockLoginArgumentResolver implements HandlerMethodArgumentResolver {

        @Override
        public boolean supportsParameter(MethodParameter parameter) {
            return AuthUserDto.class.isAssignableFrom(parameter.getParameterType());
        }

        @Override
        public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
            return AuthUserDto.builder().userId(1L).build(); // Mock된 사용자 ID 반환
        }
    }
}
