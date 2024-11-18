package LinkerBell.campus_market_spring.controller;

import LinkerBell.campus_market_spring.domain.Category;
import LinkerBell.campus_market_spring.dto.AuthUserDto;
import LinkerBell.campus_market_spring.dto.ItemCategoryResponseDto;
import LinkerBell.campus_market_spring.dto.ItemDetailsViewResponseDto;
import LinkerBell.campus_market_spring.dto.ItemRegisterRequestDto;
import LinkerBell.campus_market_spring.dto.ItemRegisterResponseDto;
import LinkerBell.campus_market_spring.dto.ItemSearchRequestDto;
import LinkerBell.campus_market_spring.dto.ItemSearchResponseDto;
import LinkerBell.campus_market_spring.dto.ItemStatusChangeRequestDto;
import LinkerBell.campus_market_spring.dto.SliceResponse;
import LinkerBell.campus_market_spring.global.auth.Login;
import LinkerBell.campus_market_spring.global.error.ErrorCode;
import LinkerBell.campus_market_spring.global.error.exception.CustomException;
import LinkerBell.campus_market_spring.service.ItemService;
import jakarta.validation.Valid;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/items")
public class ItemController {

    private final ItemService itemService;

    @Value("${path.default_item_thumbnail}")
    private String defaultItemThumbnail;

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

        SliceResponse<ItemSearchResponseDto> sliceResponse = itemService.itemSearch(
            user.getUserId(), itemSearchRequestDto);
        return ResponseEntity.ok(sliceResponse);
    }

    @PostMapping
    public ResponseEntity<ItemRegisterResponseDto> itemRegister(@Login AuthUserDto authUserDto,
        @Valid @RequestBody ItemRegisterRequestDto itemRegisterRequestDto) {
        validThumbnail(itemRegisterRequestDto);
        validItemPhotos(itemRegisterRequestDto);
        validCategory(itemRegisterRequestDto);

        ItemRegisterResponseDto itemRegisterResponseDto = itemService.itemRegister(
            authUserDto.getUserId(), itemRegisterRequestDto);
        return ResponseEntity.ok(itemRegisterResponseDto);
    }


    @GetMapping("/categories")
    public ResponseEntity<ItemCategoryResponseDto> itemCategoriesReturn(
        @Login AuthUserDto authUserDto) {
        return ResponseEntity.ok(new ItemCategoryResponseDto(Category.values()));
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDetailsViewResponseDto> viewItemDetails(
        @Login AuthUserDto authUserDto,
        @PathVariable("itemId") Long itemId) {
        validItemId(itemId);
        return ResponseEntity.ok(itemService.viewItemDetails(authUserDto.getUserId(), itemId));
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<?> itemUpdate(
        @Login AuthUserDto authUserDto,
        @PathVariable("itemId") Long itemId,
        @Valid @RequestBody ItemRegisterRequestDto itemRegisterRequestDto) {
        validItemId(itemId);
        validThumbnail(itemRegisterRequestDto);
        validItemPhotos(itemRegisterRequestDto);
        validCategory(itemRegisterRequestDto);

        itemService.updateItem(authUserDto.getUserId(), itemId, itemRegisterRequestDto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<?> itemDelete(
        @Login AuthUserDto authUserDto,
        @PathVariable("itemId") Long itemId
    ) {
        validItemId(itemId);
        itemService.deleteItem(authUserDto.getUserId(), itemId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{itemId}/change-status")
    public ResponseEntity<?> itemStatusChange(
        @Login AuthUserDto authUserDto,
        @PathVariable("itemId") Long itemId,
        @Valid @RequestBody ItemStatusChangeRequestDto itemStatusChangeRequestDto
    ) {
        validItemId(itemId);
        itemService.changeItemStatus(authUserDto.getUserId(), itemId, itemStatusChangeRequestDto);
        return ResponseEntity.noContent().build();
    }

    private void validCategory(ItemRegisterRequestDto itemRegisterRequestDto) {
        if (itemRegisterRequestDto.getCategory() == null) {
            itemRegisterRequestDto.setCategory(Category.OTHER);
        }
    }

    private void validItemId(Long itemId) {

        if (itemId == null) {
            throw new CustomException(ErrorCode.INVALID_ITEM_ID);
        }
        if (itemId < 1) {
            throw new CustomException(ErrorCode.INVALID_ITEM_ID);
        }
    }

    private void validThumbnail(ItemRegisterRequestDto itemRegisterRequestDto) {
        if (!StringUtils.hasText(itemRegisterRequestDto.getThumbnail())) {
            throw new CustomException(ErrorCode.EMPTY_ITEM_THUMBNAIL);
        }
    }

    private void validItemPhotos(ItemRegisterRequestDto itemRegisterRequestDto) {
        if (itemRegisterRequestDto.getImages() != null && !itemRegisterRequestDto.getImages()
            .isEmpty()) {
            if (itemRegisterRequestDto.getImages().size() > 5) {
                throw new CustomException(ErrorCode.INVALID_ITEM_PHOTOS_COUNT);
            } else if (itemRegisterRequestDto.getImages().size() !=
                itemRegisterRequestDto.getImages().stream().distinct().count()) {
                throw new CustomException(ErrorCode.DUPLICATE_ITEM_PHOTOS);
            }
        }
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
