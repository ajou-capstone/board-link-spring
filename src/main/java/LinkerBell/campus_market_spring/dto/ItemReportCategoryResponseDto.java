package LinkerBell.campus_market_spring.dto;

import LinkerBell.campus_market_spring.domain.ItemReportCategory;
import java.io.Serializable;

public record ItemReportCategoryResponseDto(ItemReportCategory[] categories) implements
    Serializable {

}
