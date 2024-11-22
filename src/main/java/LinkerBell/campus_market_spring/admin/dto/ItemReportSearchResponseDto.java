package LinkerBell.campus_market_spring.admin.dto;

import LinkerBell.campus_market_spring.domain.ItemReport;
import LinkerBell.campus_market_spring.domain.ItemReportCategory;
import com.fasterxml.jackson.annotation.JsonProperty;

public record ItemReportSearchResponseDto(
    Long itemReportId,
    Long itemId,
    Long userId,
    ItemReportCategory category,
    String description,
    @JsonProperty(value = "isCompleted") Boolean isCompleted
) {

    public ItemReportSearchResponseDto(ItemReport itemReport) {
        this(itemReport.getItemReportId(), itemReport.getItem().getItemId(),
            itemReport.getUser().getUserId(), itemReport.getCategory(),
            itemReport.getDescription(), itemReport.isCompleted());
    }
}
