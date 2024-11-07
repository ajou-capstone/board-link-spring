package LinkerBell.campus_market_spring.dto.CollectionResponse;

import LinkerBell.campus_market_spring.dto.RecentChatMessageResponseDto;
import java.util.List;

public record RecentChatMessageCollectionResponseDto(
    List<RecentChatMessageResponseDto> messageIdList) {

    public static RecentChatMessageCollectionResponseDto from(
        List<RecentChatMessageResponseDto> messageIdList) {
        return new RecentChatMessageCollectionResponseDto(messageIdList);
    }
}
