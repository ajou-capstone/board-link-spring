package LinkerBell.campus_market_spring.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import LinkerBell.campus_market_spring.dto.AuthUserDto;
import LinkerBell.campus_market_spring.dto.ReviewRequestDto;
import LinkerBell.campus_market_spring.dto.ReviewResponseDto;
import LinkerBell.campus_market_spring.dto.SliceResponse;
import LinkerBell.campus_market_spring.global.error.GlobalExceptionHandler;
import LinkerBell.campus_market_spring.service.ReviewService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
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
            .setCustomArgumentResolvers(mockLoginArgumentResolver,
                new PageableHandlerMethodArgumentResolver())
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

    @Test
    @DisplayName("GET /api/v1/users/{userId}/reviews - 리뷰 조회 성공")
    void getReviews_Success() throws Exception {
        // Given
        Long userId = 1L;
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Order.desc("createdDate")));

        // Mocking Service Layer
        SliceResponse<ReviewResponseDto> mockResponse = createMockSliceResponse();
        Mockito.when(reviewService.getReviews(userId, pageable)).thenReturn(mockResponse);

        // When & Then
        mockMvc.perform(get("/api/v1/users/{userId}/reviews", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .queryParam("page", "0")
                .queryParam("size", "10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content.length()").value(10))  // 10개의 리뷰가 반환된다고 예상
            .andExpect(jsonPath("$.content[0].reviewId").value(1))
            .andExpect(jsonPath("$.content[0].nickname").value("User1"))
            .andExpect(jsonPath("$.content[0].description").value("Review description 1"))
            .andExpect(jsonPath("$.content[0].rating").value(5))
            .andExpect(jsonPath("$.content[0].createdAt").value("2024-11-23T10:00:00"));
    }

    private SliceResponse<ReviewResponseDto> createMockSliceResponse() {
        List<ReviewResponseDto> reviews = List.of(
            new ReviewResponseDto(1L, "User1", "image1.jpg", "Review description 1", 5,
                LocalDateTime.parse("2024-11-23T10:00:00")),
            new ReviewResponseDto(2L, "User2", "image2.jpg", "Review description 2", 4,
                LocalDateTime.parse("2024-11-22T10:00:00")),
            new ReviewResponseDto(3L, "User3", "image3.jpg", "Review description 3", 3,
                LocalDateTime.parse("2024-11-21T10:00:00")),
            new ReviewResponseDto(4L, "User4", "image4.jpg", "Review description 4", 2,
                LocalDateTime.parse("2024-11-20T10:00:00")),
            new ReviewResponseDto(5L, "User5", "image5.jpg", "Review description 5", 1,
                LocalDateTime.parse("2024-11-19T10:00:00")),
            new ReviewResponseDto(6L, "User6", "image6.jpg", "Review description 6", 5,
                LocalDateTime.parse("2024-11-18T10:00:00")),
            new ReviewResponseDto(7L, "User7", "image7.jpg", "Review description 7", 4,
                LocalDateTime.parse("2024-11-17T10:00:00")),
            new ReviewResponseDto(8L, "User8", "image8.jpg", "Review description 8", 3,
                LocalDateTime.parse("2024-11-16T10:00:00")),
            new ReviewResponseDto(9L, "User9", "image9.jpg", "Review description 9", 2,
                LocalDateTime.parse("2024-11-15T10:00:00")),
            new ReviewResponseDto(10L, "User10", "image10.jpg", "Review description 10", 1,
                LocalDateTime.parse("2024-11-14T10:00:00"))
        );

        return new SliceResponse<>(new SliceImpl<>(reviews, PageRequest.of(0, 10), true));
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
