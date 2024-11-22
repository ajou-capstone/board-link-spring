package LinkerBell.campus_market_spring.admin.dto;

import LinkerBell.campus_market_spring.domain.ItemStatus;
import com.fasterxml.jackson.annotation.JsonProperty;

public record AdminItemSearchResponseDto(
    Long itemId,
    Long userId,
    String nickname,
    String thumbnail,
    String title,
    Integer price,
    Integer chatCount,
    Integer likeCount,
    ItemStatus itemStatus,
    @JsonProperty(value = "isLiked") Boolean isLiked,
    String universityName,
    String campusRegion,
    Long campusId
) {

}
