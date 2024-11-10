package LinkerBell.campus_market_spring.controller;

import LinkerBell.campus_market_spring.dto.AuthUserDto;
import LinkerBell.campus_market_spring.dto.LikeResponseDto;
import LinkerBell.campus_market_spring.global.auth.Login;
import LinkerBell.campus_market_spring.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
}
