package LinkerBell.campus_market_spring.dto;

import LinkerBell.campus_market_spring.domain.Category;
import LinkerBell.campus_market_spring.domain.ItemStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemDetailsViewResponseDto {

    private Long itemId;
    private Long userId;
    private Long campusId;
    private String nickname;
    private String title;
    private String description;
    private Integer price;
    private Category category;
    private String thumbnail;
    private List<String> images;
    private Integer chatCount;
    private Integer likeCount;
    private boolean isLiked;
    private ItemStatus itemStatus;

}
