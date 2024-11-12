package LinkerBell.campus_market_spring.dto;

import LinkerBell.campus_market_spring.domain.ContentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChattingRequestDto {

    private String content;
    private ContentType contentType;
}
