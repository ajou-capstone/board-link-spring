package LinkerBell.campus_market_spring.dto;

import LinkerBell.campus_market_spring.domain.ItemReportCategory;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemReportRequestDto {

    private ItemReportCategory category;
    private String description;
}
