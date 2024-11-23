package LinkerBell.campus_market_spring.admin.dto;

import LinkerBell.campus_market_spring.domain.ItemReport;
import LinkerBell.campus_market_spring.domain.ItemReportCategory;
import com.fasterxml.jackson.annotation.JsonProperty;

public record ItemReportResponseDto(
    Long itemReportId,
    Long itemId,
    Long userId,
    String description,
    ItemReportCategory category,
    @JsonProperty(value = "isCompleted") Boolean isCompleted) {

    public ItemReportResponseDto(ItemReport itemReport) {
        this(itemReport.getItemReportId(), itemReport.getItem().getItemId(),
            itemReport.getUser().getUserId(), itemReport.getDescription(),
            itemReport.getCategory(), itemReport.isCompleted());
    }
}
