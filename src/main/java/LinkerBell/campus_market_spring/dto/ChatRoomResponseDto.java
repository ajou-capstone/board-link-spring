package LinkerBell.campus_market_spring.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChatRoomResponseDto {
    private Long chatRoomId;
    private Long userId;
    private Long itemId;
    private String title;
    private boolean isAlarm;
}
