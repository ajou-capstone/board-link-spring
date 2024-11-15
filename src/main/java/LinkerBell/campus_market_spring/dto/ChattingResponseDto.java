package LinkerBell.campus_market_spring.dto;

import LinkerBell.campus_market_spring.domain.ContentType;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ChattingResponseDto {

    private Long chattingId;
    private Long chatRoomId;
    private Long userId;
    private String content;
    private ContentType contentType;
    private LocalDateTime createdAt;
}
