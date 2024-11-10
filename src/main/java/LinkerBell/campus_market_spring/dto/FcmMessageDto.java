package LinkerBell.campus_market_spring.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
public class FcmMessageDto {

    private String targetToken;
    private String title;
    private String body;
    private String deeplinkUrl;

}
