package LinkerBell.campus_market_spring.admin.controller;

import LinkerBell.campus_market_spring.admin.dto.AdminItemSearchResponseDto;
import LinkerBell.campus_market_spring.admin.dto.AdminLoginRequestDto;
import LinkerBell.campus_market_spring.admin.dto.ItemReportResponseDto;
import LinkerBell.campus_market_spring.admin.dto.ItemReportSearchResponseDto;
import LinkerBell.campus_market_spring.admin.dto.UserReportResponseDto;
import LinkerBell.campus_market_spring.admin.service.AdminService;
import LinkerBell.campus_market_spring.domain.Category;
import LinkerBell.campus_market_spring.dto.AuthResponseDto;
import LinkerBell.campus_market_spring.dto.AuthUserDto;
import LinkerBell.campus_market_spring.dto.ItemSearchResponseDto;
import LinkerBell.campus_market_spring.dto.SliceResponse;
import LinkerBell.campus_market_spring.global.auth.Login;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/api/v1")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> adminLogin(
        @RequestBody AdminLoginRequestDto requestDto) {
        AuthResponseDto authResponseDto = adminService.adminLogin(requestDto.idToken());
        return ResponseEntity.ok(authResponseDto);
    }

    @GetMapping("/items")
    public ResponseEntity<SliceResponse<AdminItemSearchResponseDto>> getAllItems(
        @Login AuthUserDto user,
        @RequestParam(required = false) String name,
        @RequestParam(required = false) Category category,
        @RequestParam(required = false) Integer minPrice,
        @RequestParam(required = false) Integer maxPrice,
        @PageableDefault(page = 0, size = 10, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable) {
        SliceResponse<AdminItemSearchResponseDto> response =
            adminService.getAllItems(user.getUserId(), name, category, minPrice, maxPrice, pageable);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/test")
    public ResponseEntity<String> test(@Login AuthUserDto user) {
        return ResponseEntity.ok("Hello Admin! " + user.getLoginEmail());
    }

    @GetMapping("/items/report")
    public ResponseEntity< SliceResponse<ItemReportSearchResponseDto>> getItemReports(
        @PageableDefault(page = 0, size = 0, sort = "createDate",
        direction = Direction.DESC)Pageable pageable) {
        SliceResponse<ItemReportSearchResponseDto> response = adminService.getItemReports(pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/items/report/{itemReportId}")
    public ResponseEntity<ItemReportResponseDto> getItemReportDetails(
        @PathVariable("itemReportId") Long itemReportId) {
        ItemReportResponseDto response = adminService.getItemReport(itemReportId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/users/report/{userReportId}")
    public ResponseEntity<UserReportResponseDto> getUserReportDetails(
        @PathVariable("userReportId") Long userReportId) {
        UserReportResponseDto response = adminService.getUserReport(userReportId);
        return ResponseEntity.ok(response);
    }
}
