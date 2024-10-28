package LinkerBell.campus_market_spring.dto;

import LinkerBell.campus_market_spring.domain.Category;
import lombok.Data;
import org.springframework.data.domain.Pageable;

@Data
public class ItemSearchRequestDto {

    private String name;
    private Category category;
    private Integer minPrice = 0;
    private Integer maxPrice = Integer.MAX_VALUE;
    private Pageable pageable;

}
