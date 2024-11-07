package LinkerBell.campus_market_spring.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChatRoomDataResponseDto {

    private Long chatRoomId;
    private Long userId;
    private Long itemId;
    private String title;
    @JsonProperty(value = "isAlarm")
    private Boolean isAlarm;
    private Long messageId;
}
