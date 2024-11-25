package LinkerBell.campus_market_spring.controller;

import LinkerBell.campus_market_spring.dto.AuthUserDto;
import LinkerBell.campus_market_spring.dto.DealHistoryResponseDto;
import LinkerBell.campus_market_spring.dto.SliceResponse;
import LinkerBell.campus_market_spring.global.auth.Login;
import LinkerBell.campus_market_spring.global.error.ErrorCode;
import LinkerBell.campus_market_spring.global.error.exception.CustomException;
import LinkerBell.campus_market_spring.service.DealHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.data.web.SortDefault.SortDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class DealHistoryController {

    private final DealHistoryService dealHistoryService;

    @GetMapping("/api/v1/items/{userId}/history")
    public ResponseEntity<SliceResponse<DealHistoryResponseDto>> getDealHistory(
        @Login AuthUserDto authUserDto, @PathVariable("userId") Long userId,
        @RequestParam String type,
        @PageableDefault(page = 0, size = 10, sort = "createdDate", direction = Sort.Direction.DESC) @SortDefaults({
            @SortDefault(sort = "createdDate", direction = Direction.DESC),
            @SortDefault(sort = "itemId", direction = Direction.DESC)}) Pageable pageable) {
        SliceResponse<DealHistoryResponseDto> dealHistoryResponse;

        if (type.equals("all")) {
            dealHistoryResponse = dealHistoryService.getAllDealHistory(authUserDto.getUserId(),
                userId, pageable);
        } else if (type.equals("purchase")) {
            dealHistoryResponse = dealHistoryService.getPurchaseDealHistory(authUserDto.getUserId(),
                userId, pageable);
        } else if (type.equals("sales")) {
            dealHistoryResponse = dealHistoryService.getSalesDealHistory(userId, pageable);
        } else {
            throw new CustomException(ErrorCode.INVALID_DEAL_TYPE);
        }

        return ResponseEntity.ok(dealHistoryResponse);
    }
}
