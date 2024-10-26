package LinkerBell.campus_market_spring.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChatRoomDataResponseDto {
    private Long chatRoomId;
    private Long userId;
    private Long itemId;
    private String title;
    private boolean isAlarm;
    private Long messageId;
}
