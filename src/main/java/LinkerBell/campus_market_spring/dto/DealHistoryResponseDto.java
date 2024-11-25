package LinkerBell.campus_market_spring.dto;

import LinkerBell.campus_market_spring.domain.ItemStatus;
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
    private int price;
    private String thumbnail;
    private ItemStatus itemStatus;
}
