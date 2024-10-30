package LinkerBell.campus_market_spring.dto;

import LinkerBell.campus_market_spring.domain.ItemStatus;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ItemSearchResponseDto {
    private Long itemId;
    private Long userId;
    private String nickname;
    private String thumbnail;
    private String title;
    private Integer price;
    private Integer chatCount;
    private Integer likeCount;
    private ItemStatus itemStatus;

    @QueryProjection
    public ItemSearchResponseDto(Long itemId, Long userId, String nickname, String thumbnail, String title, Integer price, Integer chatCount, Integer likeCount, ItemStatus itemStatus) {
        this.itemId = itemId;
        this.userId = userId;
        this.nickname = nickname;
        this.thumbnail = thumbnail;
        this.title = title;
        this.price = price;
        this.chatCount = chatCount;
        this.likeCount = likeCount;
        this.itemStatus = itemStatus;
    }
}
