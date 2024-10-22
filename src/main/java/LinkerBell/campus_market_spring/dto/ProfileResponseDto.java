package LinkerBell.campus_market_spring.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProfileResponseDto {

    private Long userId;
    private Long campusId;
    private String loginEmail;
    private String schoolEmail;
    private String nickname;
    private String profileImage;
    private Double rating;
}
