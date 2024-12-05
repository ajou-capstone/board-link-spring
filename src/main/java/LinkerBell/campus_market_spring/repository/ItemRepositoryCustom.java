package LinkerBell.campus_market_spring.repository;

import LinkerBell.campus_market_spring.admin.dto.AdminItemSearchResponseDto;
import LinkerBell.campus_market_spring.domain.Category;
import LinkerBell.campus_market_spring.domain.ItemStatus;
import LinkerBell.campus_market_spring.dto.ItemDetailsViewResponseDto;
import LinkerBell.campus_market_spring.dto.ItemSearchResponseDto;
import LinkerBell.campus_market_spring.dto.SliceResponse;
import org.springframework.data.domain.Pageable;

public interface ItemRepositoryCustom {

    SliceResponse<ItemSearchResponseDto> itemSearch(Long userId, Long campusId, String name,
        Category category, Integer minPrice, Integer maxPrice, ItemStatus itemStatus,
        Pageable pageable);

    ItemDetailsViewResponseDto findByItemDetails(Long userId, Long itemId);

    SliceResponse<AdminItemSearchResponseDto> adminItemSearch(Long userId, String name,
        Category category, Integer minPrice, Integer maxPrice, Pageable pageable);
}
