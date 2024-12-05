package LinkerBell.campus_market_spring.dto;

import LinkerBell.campus_market_spring.domain.Category;
import LinkerBell.campus_market_spring.domain.ItemStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Pageable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemSearchRequestDto {

    private String name;
    private Category category;
    private Integer minPrice;
    private Integer maxPrice;
    private ItemStatus itemStatus;
    private Pageable pageable;
}
