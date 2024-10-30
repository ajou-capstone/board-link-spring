package LinkerBell.campus_market_spring.controller;

import LinkerBell.campus_market_spring.domain.Category;
import LinkerBell.campus_market_spring.dto.AuthUserDto;
import LinkerBell.campus_market_spring.dto.ItemSearchRequestDto;
import LinkerBell.campus_market_spring.dto.ItemSearchResponseDto;
import LinkerBell.campus_market_spring.dto.SliceResponse;
import LinkerBell.campus_market_spring.global.auth.Login;
import LinkerBell.campus_market_spring.global.error.ErrorCode;
import LinkerBell.campus_market_spring.global.error.exception.CustomException;
import LinkerBell.campus_market_spring.service.ItemService;
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

import java.util.Arrays;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/items")
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public ResponseEntity<SliceResponse<ItemSearchResponseDto>> itemSearch(@Login AuthUserDto user,
                                                                           @RequestParam(required = false) String name,
                                                                           @RequestParam(required = false) Category category,
                                                                           @RequestParam(required = false) Integer minPrice,
                                                                           @RequestParam(required = false) Integer maxPrice,
                                                                           @PageableDefault(page = 0, size = 10, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable) {

        ItemSearchRequestDto itemSearchRequestDto = new ItemSearchRequestDto();
        itemSearchRequestDto.setName(name);
        itemSearchRequestDto.setCategory(category);

        priceValidate(minPrice, maxPrice);

        itemSearchRequestDto.setMinPrice(minPrice);
        itemSearchRequestDto.setMaxPrice(maxPrice);

        pageableValidate(pageable);

        itemSearchRequestDto.setPageable(pageable);

        SliceResponse<ItemSearchResponseDto> sliceResponse = itemService.itemSearch(user.getUserId(), itemSearchRequestDto);
        return ResponseEntity.ok(sliceResponse);
    }

    private void pageableValidate(Pageable pageable) {
        if (pageable.getSort().stream().count() != 1) {
            throw new CustomException(ErrorCode.INVALID_SORT);
        }
        String[] properties = {"price", "createdDate"};
        for (Sort.Order order : pageable.getSort()) {
            if (!Arrays.asList(properties).contains(order.getProperty())) {
                throw new CustomException(ErrorCode.INVALID_SORT);
            }
        }
    }

    private void priceValidate(Integer minPrice, Integer maxPrice) {
        if (minPrice != null && maxPrice != null) {
            if (minPrice < 0 || maxPrice < 0) {
                throw new CustomException(ErrorCode.INVALID_PRICE);
            } else if (minPrice > maxPrice) {
                throw new CustomException(ErrorCode.INVALID_PRICE);
            }
        } else if (minPrice != null) {
            if (minPrice < 0) {
                throw new CustomException(ErrorCode.INVALID_PRICE);
            }
        } else if (maxPrice != null) {
            if (maxPrice < 0) {
                throw new CustomException(ErrorCode.INVALID_PRICE);
            }
        }
    }
}
