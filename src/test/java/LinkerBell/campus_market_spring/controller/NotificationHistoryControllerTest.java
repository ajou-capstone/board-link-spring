package LinkerBell.campus_market_spring.controller;

import static LinkerBell.campus_market_spring.global.error.ErrorCode.INVALID_NOTIFICATION_ID;
import static LinkerBell.campus_market_spring.global.error.ErrorCode.INVALID_PAGEABLE_PAGE;
import static LinkerBell.campus_market_spring.global.error.ErrorCode.INVALID_PAGEABLE_SIZE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import LinkerBell.campus_market_spring.dto.AuthUserDto;
import LinkerBell.campus_market_spring.dto.NotificationHistoryResponseDto;
import LinkerBell.campus_market_spring.dto.SliceResponse;
import LinkerBell.campus_market_spring.global.error.GlobalExceptionHandler;
import LinkerBell.campus_market_spring.global.error.exception.CustomException;
import LinkerBell.campus_market_spring.service.NotificationHistoryService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@ExtendWith(MockitoExtension.class)
class NotificationHistoryControllerTest {

    private MockMvc mockMvc;

    private MockLoginArgumentResolver mockLoginArgumentResolver = new MockLoginArgumentResolver();

    @Mock
    NotificationHistoryService notificationHistoryService;

    @InjectMocks
    NotificationHistoryController notificationHistoryController;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(notificationHistoryController)
            .setControllerAdvice(GlobalExceptionHandler.class)
            .setCustomArgumentResolvers(
                new PageableHandlerMethodArgumentResolver(),
                mockLoginArgumentResolver)
            .build();
    }

    @Test
    @DisplayName("notificationHistory 목록 받아오기 테스트")
    void getNotificationHistory_shouldReturnNotifications() throws Exception {
        List<NotificationHistoryResponseDto> mockContent = List.of(
            new NotificationHistoryResponseDto(1L, "title1", "content1", "deeplink1"),
            new NotificationHistoryResponseDto(2L, "title2", "content2", "deeplink2")
        );

        SliceImpl<NotificationHistoryResponseDto> mockSlice = new SliceImpl<>(
            mockContent,
            PageRequest.of(0, 2, Sort.by(Sort.Order.desc("createdDate"),
                Sort.Order.desc("notificationHistoryId"))),
            true
        );

        SliceResponse<NotificationHistoryResponseDto> mockResponse = new SliceResponse<>(mockSlice);

        when(
            notificationHistoryService.getNotificationHistory(any(Long.class), any(Pageable.class)))
            .thenReturn(mockResponse);

        mockMvc.perform(get("/api/v1/notification-history")
                .param("page", "0")
                .param("size", "2")
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].notificationHistoryId").value(1L))
            .andExpect(jsonPath("$.content[0].title").value("title1"))
            .andExpect(jsonPath("$.content[1].notificationHistoryId").value(2L))
            .andExpect(jsonPath("$.content[1].title").value("title2"))
            .andExpect(jsonPath("$.hasNext").value(true));

        verify(notificationHistoryService).getNotificationHistory(any(Long.class),
            any(Pageable.class));
    }

    @Test
    @DisplayName("잘못된 page 값 테스트")
    void getNotificationHistory_shouldThrowWhenInvalidPage() throws Exception {
        mockMvc.perform(get("/api/v1/notification-history")
                .param("page", "xxx")
                .param("size", "2")
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(
                result -> assertThat(result.getResolvedException().getClass()).isAssignableFrom(
                    MethodArgumentTypeMismatchException.class))
            .andExpect(jsonPath("$.code").value(INVALID_PAGEABLE_PAGE.getCode()));

    }

    @Test
    @DisplayName("잘못된 size 값 테스트")
    void getNotificationHistory_shouldThrowWhenInvalidSize() throws Exception {
        mockMvc.perform(get("/api/v1/notification-history")
                .param("page", "0")
                .param("size", "xxx")
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(
                result -> assertThat(result.getResolvedException().getClass()).isAssignableFrom(
                    MethodArgumentTypeMismatchException.class))
            .andExpect(jsonPath("$.code").value(INVALID_PAGEABLE_SIZE.getCode()));

    }

    @Test
    @DisplayName("notificationHistory 1개 delete 테스트")
    void deleteNotificationHistory_shouldDeleteNotification() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/api/v1/notification-history/{notificationId}", 1L)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        verify(notificationHistoryService).deleteNotification(1L, 1L);
    }

    @Test
    @DisplayName("잘못된 NotificationHistory id 검증 테스트")
    void deleteNotificationHistory_shouldThrowWhenInvalidId() throws Exception {
        mockMvc.perform(delete("/api/v1/notification-history/{notificationId}", -1L)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(
                result -> assertThat(result.getResolvedException().getClass()).isAssignableFrom(
                    CustomException.class))
            .andExpect(jsonPath("$.code").value(INVALID_NOTIFICATION_ID.getCode()));
    }

    @Test
    @DisplayName("NotificationHistory 전체 delete 테스트")
    void deleteAllNotificationHistory_shouldDeleteAllNotifications() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/api/v1/notification-history")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        verify(notificationHistoryService).deleteAllNotification(1L);
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