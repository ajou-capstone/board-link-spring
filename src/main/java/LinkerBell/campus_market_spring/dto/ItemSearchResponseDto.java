package LinkerBell.campus_market_spring.dto;

import LinkerBell.campus_market_spring.domain.ItemStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDateTime;
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
    @JsonProperty(value = "isLiked")
    private boolean isLiked;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime createdDate;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime lastModifiedDate;

    @QueryProjection
    public ItemSearchResponseDto(Long itemId, Long userId, String nickname, String thumbnail,
        String title, Integer price, Integer chatCount, Integer likeCount, ItemStatus itemStatus,
        boolean isLiked, LocalDateTime createdDate, LocalDateTime lastModifiedDate) {
        this.itemId = itemId;
        this.userId = userId;
        this.nickname = nickname;
        this.thumbnail = thumbnail;
        this.title = title;
        this.price = price;
        this.chatCount = chatCount;
        this.likeCount = likeCount;
        this.itemStatus = itemStatus;
        this.isLiked = isLiked;
        this.createdDate = createdDate;
        this.lastModifiedDate = lastModifiedDate;
    }
}
