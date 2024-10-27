package LinkerBell.campus_market_spring.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ChatRoomRequestDto {
    private Long userId;
    private Long itemId;
}
