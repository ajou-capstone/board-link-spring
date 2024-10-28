package LinkerBell.campus_market_spring.controller;

import LinkerBell.campus_market_spring.domain.Category;
import LinkerBell.campus_market_spring.dto.AuthUserDto;
import LinkerBell.campus_market_spring.dto.ItemSearchRequestDto;
import LinkerBell.campus_market_spring.dto.ItemSearchResponseDto;
import LinkerBell.campus_market_spring.dto.SliceResponse;
import LinkerBell.campus_market_spring.global.auth.Login;
import LinkerBell.campus_market_spring.service.ItemService;
import com.google.common.base.Enums;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/items")
public class ItemController {
    private final ItemService itemService;

    @GetMapping("/")
    public ResponseEntity<SliceResponse<ItemSearchResponseDto>> itemSearch(@Login AuthUserDto authUserDto,
                                                                           @RequestParam(required = false) String name,
                                                                           @RequestParam(required = false) String category,
                                                                           @RequestParam(required = false, defaultValue = "0") Integer minPrice,
                                                                           @RequestParam(required = false, defaultValue = Integer.MAX_VALUE + "") Integer maxPrice,
                                                                           @PageableDefault(sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable)

    {

        ItemSearchRequestDto itemSearchRequestDto = new ItemSearchRequestDto();
        if(!name.isEmpty()) {
            itemSearchRequestDto.setName(name);
        }

        if (!category.isEmpty()) {
            if(Enums.getIfPresent(Category.class, category).isPresent()) {
                itemSearchRequestDto.setCategory(Category.valueOf(category));
            }
            else {
                itemSearchRequestDto.setCategory(null);
            }
        }

        itemSearchRequestDto.setMinPrice(minPrice);
        itemSearchRequestDto.setMaxPrice(maxPrice);
        itemSearchRequestDto.setPageable(pageable);

        SliceResponse<ItemSearchResponseDto> sliceResponse = itemService.itemSearch(authUserDto.getUserId(), itemSearchRequestDto);
        return ResponseEntity.ok(sliceResponse);
    }
}
