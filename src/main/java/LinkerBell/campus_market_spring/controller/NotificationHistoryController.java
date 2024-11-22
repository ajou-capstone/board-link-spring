package LinkerBell.campus_market_spring.controller;

import LinkerBell.campus_market_spring.dto.AuthUserDto;
import LinkerBell.campus_market_spring.dto.NotificationHistoryResponseDto;
import LinkerBell.campus_market_spring.dto.SliceResponse;
import LinkerBell.campus_market_spring.global.auth.Login;
import LinkerBell.campus_market_spring.global.error.ErrorCode;
import LinkerBell.campus_market_spring.global.error.exception.CustomException;
import LinkerBell.campus_market_spring.service.NotificationHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notification-history")
public class NotificationHistoryController {

    private final NotificationHistoryService notificationHistoryService;

    @GetMapping
    public ResponseEntity<SliceResponse<NotificationHistoryResponseDto>> getNotificationHistory(
        @Login AuthUserDto user,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        Sort sort = Sort.by(Sort.Order.desc("createdDate"),
            Sort.Order.desc("notificationHistoryId"));
        Pageable pageable = PageRequest.of(page, size, sort);

        SliceResponse<NotificationHistoryResponseDto> notificationHistory = notificationHistoryService.getNotificationHistory(
            user.getUserId(), pageable);

        return ResponseEntity.ok(notificationHistory);
    }

    @DeleteMapping("/{notificationId}")
    public ResponseEntity<?> deleteNotificationHistory(
        @Login AuthUserDto user,
        @PathVariable(name = "notificationId") Long notificationId
    ) {
        validNotificationId(notificationId);
        notificationHistoryService.deleteNotification(user.getUserId(), notificationId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<?> deleteAllNotificationHistory(
        @Login AuthUserDto user
    ) {
        notificationHistoryService.deleteAllNotification(user.getUserId());
        return ResponseEntity.noContent().build();
    }

    private void validNotificationId(Long notificationId) {

        if (notificationId == null) {
            throw new CustomException(ErrorCode.INVALID_NOTIFICATION_ID);
        }
        if (notificationId < 1) {
            throw new CustomException(ErrorCode.INVALID_NOTIFICATION_ID);
        }
    }

}
