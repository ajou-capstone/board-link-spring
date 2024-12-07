package LinkerBell.campus_market_spring.dto;

import LinkerBell.campus_market_spring.domain.ItemReportCategory;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ItemReportCategoryResponseDto {

    private ItemReportCategory[] categories;
}
