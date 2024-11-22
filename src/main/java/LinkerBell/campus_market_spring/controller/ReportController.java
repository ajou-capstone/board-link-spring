package LinkerBell.campus_market_spring.controller;

import LinkerBell.campus_market_spring.domain.ItemReportCategory;
import LinkerBell.campus_market_spring.domain.UserReportCategory;
import LinkerBell.campus_market_spring.dto.AuthUserDto;
import LinkerBell.campus_market_spring.dto.ItemReportCategoryResponseDto;
import LinkerBell.campus_market_spring.dto.ItemReportRequestDto;
import LinkerBell.campus_market_spring.dto.UserReportCategoryResponseDto;
import LinkerBell.campus_market_spring.dto.UserReportRequestDto;
import LinkerBell.campus_market_spring.global.auth.Login;
import LinkerBell.campus_market_spring.service.ReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @PostMapping("/items/{itemId}/report")
    public ResponseEntity<?> reportItem(@Login AuthUserDto user, @PathVariable("itemId") Long itemId,
        @Valid @RequestBody ItemReportRequestDto requestDto) {
        reportService.reportItem(user.getUserId(), itemId,
            requestDto.getDescription(), requestDto.getCategory());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/users/{userId}/report")
    public ResponseEntity<?> reportUser(@Login AuthUserDto user, @PathVariable("userId") Long targetId,
        @Valid @RequestBody UserReportRequestDto requestDto) {
        reportService.reportUser(user.getUserId(), targetId,
            requestDto.getDescription(), requestDto.getCategory());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/items/report")
    public ResponseEntity<ItemReportCategoryResponseDto> getItemReportCategory() {
        return ResponseEntity.ok(new ItemReportCategoryResponseDto(ItemReportCategory.values()));
    }

    @GetMapping("/users/report")
    public ResponseEntity<UserReportCategoryResponseDto> getUserReportCategory() {
        return ResponseEntity.ok(new UserReportCategoryResponseDto(UserReportCategory.values()));
    }
}
