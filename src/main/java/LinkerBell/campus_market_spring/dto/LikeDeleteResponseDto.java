package LinkerBell.campus_market_spring.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LikeDeleteResponseDto {

    private Long itemId;

    @JsonProperty(value = "isLike")
    private boolean isLike;
}
