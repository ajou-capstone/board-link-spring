package LinkerBell.campus_market_spring.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class LikeResponseDto {

    private Long likeId;
    private Long itemId;

    @JsonProperty(value = "isLike")
    private boolean isLike;
}
