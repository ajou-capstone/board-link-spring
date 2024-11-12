package LinkerBell.campus_market_spring.controller;

import LinkerBell.campus_market_spring.dto.AuthUserDto;
import LinkerBell.campus_market_spring.dto.ItemSearchResponseDto;
import LinkerBell.campus_market_spring.dto.LikeResponseDto;
import LinkerBell.campus_market_spring.dto.LikeSearchResponseDto;
import LinkerBell.campus_market_spring.dto.SliceResponse;
import LinkerBell.campus_market_spring.global.auth.Login;
import LinkerBell.campus_market_spring.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/api/v1/items/{itemId}/likes")
    public ResponseEntity<LikeResponseDto> likeItem(@Login AuthUserDto user,
        @PathVariable("itemId") Long itemId) {
        LikeResponseDto responseDto = likeService.likeItem(user.getUserId(), itemId);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/api/v1/items/likes")
    public ResponseEntity<SliceResponse<LikeSearchResponseDto>> getLikes(@Login AuthUserDto user,
        @PageableDefault(page = 0, size = 10, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable) {
        SliceResponse<LikeSearchResponseDto> responseDto =
            likeService.getLikes(user.getUserId(), pageable);
        return ResponseEntity.ok(responseDto);
    }
}
