package LinkerBell.campus_market_spring.service;

import static LinkerBell.campus_market_spring.global.error.ErrorCode.INVALID_NOTIFICATION_ID;
import static LinkerBell.campus_market_spring.global.error.ErrorCode.NOT_MATCH_USER_ID_WITH_NOTIFICATION_USER_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import LinkerBell.campus_market_spring.domain.Campus;
import LinkerBell.campus_market_spring.domain.Item;
import LinkerBell.campus_market_spring.domain.Keyword;
import LinkerBell.campus_market_spring.domain.NotificationHistory;
import LinkerBell.campus_market_spring.domain.User;
import LinkerBell.campus_market_spring.dto.NotificationHistoryResponseDto;
import LinkerBell.campus_market_spring.dto.SliceResponse;
import LinkerBell.campus_market_spring.global.error.exception.CustomException;
import LinkerBell.campus_market_spring.repository.NotificationHistoryRepository;
import LinkerBell.campus_market_spring.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Sort;

@ExtendWith(MockitoExtension.class)
class NotificationHistoryServiceTest {

    @InjectMocks
    NotificationHistoryService notificationHistoryService;

    @Mock
    NotificationHistoryRepository notificationHistoryRepository;

    @Mock
    UserRepository userRepository;

    String deeplinkKeywordUrl = "deeplink/";

    @Test
    @DisplayName("키워드를 알림 히스토리에 저장하는 테스트")
    void saveNotificationHistory_shouldSaveCorrectly() {
        User user = User.builder().userId(1L).nickname("testUser").build();
        Item item = Item.builder().itemId(1L).title("testItem").build();
        Keyword keyword = Keyword.builder().keywordName("testKeyword").user(user).build();
        List<Keyword> keywords = List.of(keyword);
        List<NotificationHistory> notificationHistories = keywords.stream()
            .map(k -> NotificationHistory.builder()
                .user(k.getUser())
                .item(item)
                .title(k.getKeywordName() + " 키워드 알림")
                .description(item.getTitle())
                .deeplink(deeplinkKeywordUrl + item.getItemId())
                .build())
            .toList();

        when(notificationHistoryRepository.saveAll(anyList())).thenReturn(notificationHistories);

        notificationHistoryService.saveNotificationHistory(keywords, item);

        verify(notificationHistoryRepository, times(1)).saveAll(anyList());
    }

    @Test
    @DisplayName("알림 히스토리를 paging을 활용해서 가져오는 테스트")
    void getNotificationHistory_shouldReturnSliceResponse() {
        Campus campus = Campus.builder().campusId(1L).universityName("testCampus").build();
        User user = User.builder().userId(1L).nickname("testUser").campus(campus).build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        Pageable pageable = PageRequest.of(0, 2, Sort.by(Sort.Order.desc("createdDate"),
            Sort.Order.desc("notificationHistoryId")));
        List<NotificationHistory> notificationHistories = List.of(
            NotificationHistory.builder().title("Notification 1").build(),
            NotificationHistory.builder().title("Notification 2").build()
        );

        SliceImpl<NotificationHistory> mockSlice =
            new SliceImpl<>(notificationHistories, pageable, true);

        when(notificationHistoryRepository.findByUser_UserId(1L, pageable))
            .thenReturn(mockSlice);

        SliceResponse<NotificationHistoryResponseDto> result =
            notificationHistoryService.getNotificationHistory(1L, pageable);

        assertThat(result.getContent().size()).isEqualTo(2);
        assertThat(result.isHasNext()).isTrue();
        verify(notificationHistoryRepository, times(1))
            .findByUser_UserId(1L, pageable);
    }

    @Test
    @DisplayName("특정 알림 히스토리를 삭제하는 테스트")
    void deleteNotification_shouldDeleteCorrectly() {
        Campus campus = Campus.builder().campusId(1L).universityName("testCampus").build();
        User user = User.builder().userId(1L).nickname("testUser").campus(campus).build();
        NotificationHistory notificationHistory = NotificationHistory.builder()
            .notificationHistoryId(1L)
            .user(user)
            .title("Test Notification")
            .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(notificationHistoryRepository.findByIdWithUserAndItem(1L))
            .thenReturn(Optional.of(notificationHistory));

        notificationHistoryService.deleteNotification(1L, 1L);

        verify(notificationHistoryRepository, times(1)).delete(notificationHistory);
    }

    @Test
    @DisplayName("특정 알림 히스토리를 삭제하려는데 삭제할 알림 히스토리가 없을 때 테스트")
    void deleteNotification_shouldNotDeleteBecauseNotificationIsNotFound() {
        Campus campus = Campus.builder().campusId(1L).universityName("testCampus").build();
        User user = User.builder().userId(1L).nickname("testUser").campus(campus).build();
        NotificationHistory notificationHistory = NotificationHistory.builder()
            .notificationHistoryId(1L)
            .user(user)
            .title("Test Notification")
            .build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(notificationHistoryRepository.findByIdWithUserAndItem(1L))
            .thenReturn(Optional.empty());

        assertThatThrownBy(() -> notificationHistoryService.deleteNotification(1L, 1L))
            .isInstanceOf(CustomException.class)
            .hasMessageContaining(INVALID_NOTIFICATION_ID.getMessage());

    }

    @Test
    @DisplayName("특정 알림 히스토리를 삭제하려는데 삭제할 알림 히스토리가 본인이 아닐 때 테스트")
    void deleteNotification_shouldNotDeleteBecauseNotificationIsNotOwner() {
        Campus campus = Campus.builder().campusId(1L).universityName("testCampus").build();
        User user1 = User.builder().userId(1L).nickname("testUser").campus(campus).build();
        User user2 = User.builder().userId(2L).nickname("testUser").campus(campus).build();
        NotificationHistory notificationHistory = NotificationHistory.builder()
            .notificationHistoryId(1L)
            .user(user2)
            .title("Test Notification")
            .build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(notificationHistoryRepository.findByIdWithUserAndItem(1L))
            .thenReturn(Optional.of(notificationHistory));

        assertThatThrownBy(() -> notificationHistoryService.deleteNotification(1L, 1L))
            .isInstanceOf(CustomException.class)
            .hasMessageContaining(NOT_MATCH_USER_ID_WITH_NOTIFICATION_USER_ID.getMessage());
    }

    @Test
    @DisplayName("유저가 가진 전체 알림 히스토리를 제거하는 테스트")
    void deleteAllNotification_shouldDeleteAllCorrectly() {
        Campus campus = Campus.builder().campusId(1L).universityName("testCampus").build();
        User user = User.builder().userId(1L).nickname("testUser").campus(campus).build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(notificationHistoryRepository.deleteByUserId(1L)).thenReturn(3);

        notificationHistoryService.deleteAllNotification(1L);

        verify(notificationHistoryRepository, times(1)).deleteByUserId(1L);
    }

    @Test
    @DisplayName("유저가 가진 전체 알림 히스토리를 제거할 때, 제거할 알림 히스토리 없는 경우 테스트")
    void deleteAllNotification_shouldThrowExceptionIfNoNotificationsDeleted() {
        Campus campus = Campus.builder().campusId(1L).universityName("testCampus").build();
        User user = User.builder().userId(1L).nickname("testUser").campus(campus).build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(notificationHistoryRepository.deleteByUserId(1L)).thenReturn(0);

        assertThatThrownBy(() -> notificationHistoryService.deleteAllNotification(1L))
            .isInstanceOf(CustomException.class)
            .hasMessageContaining(INVALID_NOTIFICATION_ID.getMessage());
    }
}
