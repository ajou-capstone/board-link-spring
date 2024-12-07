package LinkerBell.campus_market_spring.admin.dto;

import LinkerBell.campus_market_spring.domain.ItemReport;
import LinkerBell.campus_market_spring.domain.ItemReportCategory;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ItemReportSearchResponseDto {

    private Long itemReportId;
    private Long itemId;
    private Long userId;
    private ItemReportCategory category;
    private String description;
    @JsonProperty(value = "isCompleted") private Boolean isCompleted;

    public ItemReportSearchResponseDto(ItemReport itemReport) {
        this(itemReport.getItemReportId(), itemReport.getItem().getItemId(),
            itemReport.getUser().getUserId(), itemReport.getCategory(),
            itemReport.getDescription(), itemReport.isCompleted());
    }
}
