package LinkerBell.campus_market_spring.dto;

import LinkerBell.campus_market_spring.domain.Category;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ItemCategoryResponseDto {

    private Category[] categories;
}
