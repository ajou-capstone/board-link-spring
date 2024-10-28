package LinkerBell.campus_market_spring.service;

import LinkerBell.campus_market_spring.domain.Category;
import LinkerBell.campus_market_spring.dto.ItemSearchRequestDto;
import LinkerBell.campus_market_spring.dto.ItemSearchResponseDto;
import LinkerBell.campus_market_spring.dto.SliceResponse;
import LinkerBell.campus_market_spring.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.print.Pageable;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ItemService {
    private final ItemRepository itemRepository;

    @Transactional(readOnly = true)
    public SliceResponse<ItemSearchResponseDto> itemSearch(Long userId, ItemSearchRequestDto itemSearchRequestDto) {
        return itemRepository.itemSearch(userId, itemSearchRequestDto.getName(),itemSearchRequestDto.getCategory(),itemSearchRequestDto.getMinPrice(),itemSearchRequestDto.getMaxPrice(),itemSearchRequestDto.getPageable());
    }
}
