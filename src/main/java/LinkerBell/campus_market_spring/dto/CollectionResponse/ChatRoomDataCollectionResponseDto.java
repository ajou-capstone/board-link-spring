package LinkerBell.campus_market_spring.dto.CollectionResponse;

import LinkerBell.campus_market_spring.dto.ChatRoomDataResponseDto;
import java.util.List;

public record ChatRoomDataCollectionResponseDto(List<ChatRoomDataResponseDto> chatRoomList) {

    public static ChatRoomDataCollectionResponseDto from(
        List<ChatRoomDataResponseDto> chatRoomList) {
        return new ChatRoomDataCollectionResponseDto(chatRoomList);
    }
}
