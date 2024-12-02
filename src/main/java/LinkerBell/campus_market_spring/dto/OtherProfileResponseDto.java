package LinkerBell.campus_market_spring.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class OtherProfileResponseDto {

    private Long id;
    private String nickname;
    private String profileImage;
    private Double rating;
    @JsonProperty(value = "isDeleted") Boolean isDeleted;
}
