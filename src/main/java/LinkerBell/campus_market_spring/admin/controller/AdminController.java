package LinkerBell.campus_market_spring.admin.controller;

import LinkerBell.campus_market_spring.admin.dto.AdminItemSearchResponseDto;
import LinkerBell.campus_market_spring.admin.dto.AdminLoginRequestDto;
import LinkerBell.campus_market_spring.admin.service.AdminService;
import LinkerBell.campus_market_spring.domain.Category;
import LinkerBell.campus_market_spring.dto.AuthResponseDto;
import LinkerBell.campus_market_spring.dto.AuthUserDto;
import LinkerBell.campus_market_spring.dto.ItemSearchResponseDto;
import LinkerBell.campus_market_spring.dto.SliceResponse;
import LinkerBell.campus_market_spring.global.auth.Login;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
}
