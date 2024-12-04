package LinkerBell.campus_market_spring.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChatRoomResponseDto {

    private Long chatRoomId;
    private Long userId;
    private Long itemId;
    private String title;
    private String thumbnail;
    @JsonProperty(value = "isAlarm")
    private Boolean isAlarm;
}
