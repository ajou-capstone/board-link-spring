package LinkerBell.campus_market_spring.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChatMessageResponseDto {
    private Long messageId;
    private Long chatRoomId;
    private Long userId;
    private String content;
    private String contentType;
}
