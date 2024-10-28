package LinkerBell.campus_market_spring.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class RecentChatMessageResponseDto {
    private Long chatRoomId;
    private List<Long> messageIdList;
}
