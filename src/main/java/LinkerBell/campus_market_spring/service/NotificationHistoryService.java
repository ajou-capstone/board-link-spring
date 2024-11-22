package LinkerBell.campus_market_spring.service;

import LinkerBell.campus_market_spring.domain.Item;
import LinkerBell.campus_market_spring.domain.Keyword;
import LinkerBell.campus_market_spring.domain.NotificationHistory;
import LinkerBell.campus_market_spring.domain.User;
import LinkerBell.campus_market_spring.dto.NotificationHistoryResponseDto;
import LinkerBell.campus_market_spring.dto.SliceResponse;
import LinkerBell.campus_market_spring.global.error.ErrorCode;
import LinkerBell.campus_market_spring.global.error.exception.CustomException;
import LinkerBell.campus_market_spring.repository.NotificationHistoryRepository;
import LinkerBell.campus_market_spring.repository.UserRepository;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class NotificationHistoryService {

    private final NotificationHistoryRepository notificationHistoryRepository;
    private final UserRepository userRepository;

    @Value("${deeplink.keyword_url}")
    private String deeplinkKeywordUrl;

    public void saveNotificationHistory(List<Keyword> sendingKeywords, Item savedItem) {
        for (Keyword sendingKeyword : sendingKeywords) {
            NotificationHistory notificationHistory = createNotificationHistory(sendingKeyword,
                savedItem);
            notificationHistoryRepository.save(notificationHistory);
        }
    }

    @Transactional(readOnly = true)
    public SliceResponse<NotificationHistoryResponseDto> getNotificationHistory(Long userId,
        Pageable pageable) {
        User user = getUserWithCampus(userId);
        Slice<NotificationHistory> slice = notificationHistoryRepository.findByUser_UserId(
            userId, pageable);

        List<NotificationHistoryResponseDto> content = slice.getContent()
            .stream()
            .map(NotificationHistoryResponseDto::fromEntity)
            .toList();

        return new SliceResponse<>(new SliceImpl<>(content, pageable, slice.hasNext()));
    }

    public void deleteNotification(Long userId, Long notificationId) {
        User user = getUserWithCampus(userId);
        NotificationHistory notificationHistory = notificationHistoryRepository.findByIdWithUserAndItem(
                notificationId)
            .orElseThrow(() -> new CustomException(ErrorCode.INVALID_NOTIFICATION_ID));

        MatchUserAndNotificationUser(user, notificationHistory);

        notificationHistoryRepository.delete(notificationHistory);
    }

    public void deleteAllNotification(Long userId) {
        User user = getUserWithCampus(userId);
        int deleteCount = notificationHistoryRepository.deleteByUserId(userId);

        if (deleteCount == 0) {
            throw new CustomException(ErrorCode.INVALID_NOTIFICATION_ID);
        }
    }

    private void MatchUserAndNotificationUser(User user, NotificationHistory notificationHistory) {
        if (!Objects.equals(user.getUserId(), notificationHistory.getUser().getUserId())) {
            throw new CustomException(ErrorCode.NOT_MATCH_USER_ID_WITH_NOTIFICATION_USER_ID);
        }
    }

    private User getUserWithCampus(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (user.getCampus() == null) {
            throw new CustomException(ErrorCode.CAMPUS_NOT_FOUND);
        }
        return user;
    }


    private NotificationHistory createNotificationHistory(Keyword sendingKeyword, Item savedItem) {
        return NotificationHistory.builder()
            .user(sendingKeyword.getUser())
            .item(savedItem)
            .title(sendingKeyword.getKeywordName() + " 키워드 알림")
            .description(savedItem.getTitle())
            .deeplink(deeplinkKeywordUrl + savedItem.getItemId())
            .build();
    }

}
