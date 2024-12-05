package LinkerBell.campus_market_spring.dto;

import LinkerBell.campus_market_spring.domain.ItemStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DealHistoryResponseDto {

    private Long itemId;
    private String title;
    private Long userId;
    private String nickname;
    private int price;
    private String thumbnail;
    private ItemStatus itemStatus;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime modifiedAt;
    @JsonProperty(value = "isReviewed")
    private Boolean isReviewed;
}
