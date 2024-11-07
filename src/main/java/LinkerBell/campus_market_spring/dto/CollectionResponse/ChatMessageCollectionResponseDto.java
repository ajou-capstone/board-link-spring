package LinkerBell.campus_market_spring.dto.CollectionResponse;

import LinkerBell.campus_market_spring.dto.ChatMessageResponseDto;
import java.util.List;

public record ChatMessageCollectionResponseDto(List<ChatMessageResponseDto> messageList) {

    public static ChatMessageCollectionResponseDto from(
        List<ChatMessageResponseDto> messageList) {
        return new ChatMessageCollectionResponseDto(messageList);
    }
}
