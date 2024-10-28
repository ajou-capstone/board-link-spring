package LinkerBell.campus_market_spring.repository;

import LinkerBell.campus_market_spring.domain.Category;
import LinkerBell.campus_market_spring.dto.ItemSearchResponseDto;
import LinkerBell.campus_market_spring.dto.SliceResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ItemRepositoryCustom {
    SliceResponse<ItemSearchResponseDto> itemSearch(Long userId, String name, Category category, Integer minPrice, Integer maxPrice, Pageable pageable);
}
