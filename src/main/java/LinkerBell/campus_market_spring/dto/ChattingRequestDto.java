package LinkerBell.campus_market_spring.dto;

import LinkerBell.campus_market_spring.domain.ContentType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChattingRequestDto {

    private String content;
    private ContentType contentType;
}
