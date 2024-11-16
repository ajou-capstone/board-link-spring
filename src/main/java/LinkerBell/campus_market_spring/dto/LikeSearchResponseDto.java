package LinkerBell.campus_market_spring.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LikeSearchResponseDto {
    private Long likeId;
    private ItemSearchResponseDto item;
}
