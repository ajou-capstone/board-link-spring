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
@NoArgsConstructor
@AllArgsConstructor
public class ItemReportResponseDto {

    private Long itemReportId;
    private Long itemId;
    private Long userId;
    private String description;
    private ItemReportCategory category;
    @JsonProperty(value = "isCompleted") private Boolean isCompleted;

    public ItemReportResponseDto(ItemReport itemReport) {
        this.itemId = itemReport.getItem().getItemId();
        this.itemReportId = itemReport.getItemReportId();
        this.userId = itemReport.getUser().getUserId();
        this.description = itemReport.getDescription();
        this.category = itemReport.getCategory();
        this.isCompleted = itemReport.isCompleted();
    }
}
