package LinkerBell.campus_market_spring.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class OtherProfileResponseDto {

    private Long id;
    private String nickname;
    private String profileImage;
    private Double rating;
}
