package LinkerBell.campus_market_spring.admin.dto;

import LinkerBell.campus_market_spring.domain.ItemStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AdminItemSearchResponseDto {

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
    private Boolean isLiked;

    private String universityName;

    private String campusRegion;

    private Long campusId;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdDate;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime lastModifiedDate;

    @JsonProperty(value = "isDeleted") private Boolean isDeleted;
}
