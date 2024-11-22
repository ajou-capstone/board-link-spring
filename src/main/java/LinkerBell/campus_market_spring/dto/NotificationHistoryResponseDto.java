package LinkerBell.campus_market_spring.dto;

import LinkerBell.campus_market_spring.domain.NotificationHistory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationHistoryResponseDto {

    private Long notificationHistoryId;
    private String title;
    private String description;
    private String deeplink;

    public static NotificationHistoryResponseDto fromEntity(NotificationHistory entity) {
        return new NotificationHistoryResponseDto(
            entity.getNotificationHistoryId(),
            entity.getTitle(),
            entity.getDescription(),
            entity.getDeeplink()
        );
    }
}
