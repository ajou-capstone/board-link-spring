package LinkerBell.campus_market_spring.admin.controller;

import LinkerBell.campus_market_spring.admin.dto.AdminItemReportRequestDto;
import LinkerBell.campus_market_spring.admin.dto.AdminItemSearchResponseDto;
import LinkerBell.campus_market_spring.admin.dto.AdminLoginRequestDto;
import LinkerBell.campus_market_spring.admin.dto.AdminQaRequestDto;
import LinkerBell.campus_market_spring.admin.dto.AdminQaResponseDto;
import LinkerBell.campus_market_spring.admin.dto.AdminQaSearchResponseDto;
import LinkerBell.campus_market_spring.admin.dto.AdminUserReportRequestDto;
import LinkerBell.campus_market_spring.admin.dto.ItemReportResponseDto;
import LinkerBell.campus_market_spring.admin.dto.ItemReportSearchResponseDto;
import LinkerBell.campus_market_spring.admin.dto.UserReportSearchResponseDto;
import LinkerBell.campus_market_spring.admin.dto.UserReportResponseDto;
import LinkerBell.campus_market_spring.admin.service.AdminService;
import LinkerBell.campus_market_spring.domain.Category;
import LinkerBell.campus_market_spring.dto.AuthResponseDto;
import LinkerBell.campus_market_spring.dto.AuthUserDto;
import LinkerBell.campus_market_spring.dto.ItemDetailsViewResponseDto;
import LinkerBell.campus_market_spring.dto.OtherProfileResponseDto;
import LinkerBell.campus_market_spring.dto.SliceResponse;
import LinkerBell.campus_market_spring.global.auth.Login;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.data.web.SortDefault.SortDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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
            adminService.getAllItems(user.getUserId(), name, category, minPrice, maxPrice,
                pageable);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/items/report")
    public ResponseEntity<SliceResponse<ItemReportSearchResponseDto>> getItemReports(
        @RequestParam(defaultValue = "all", name = "status") String status,
        @PageableDefault(page = 0, size = 10)
        @SortDefaults({
            @SortDefault(sort = "createdDate", direction = Direction.DESC),
            @SortDefault(sort = "itemReportId", direction = Direction.DESC)}) Pageable pageable) {
        SliceResponse<ItemReportSearchResponseDto> response = adminService.getItemReports(status, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/users/report")
    public ResponseEntity<SliceResponse<UserReportSearchResponseDto>> getUserReports(
        @RequestParam(defaultValue = "all", name = "status") String status,
        @PageableDefault(page = 0, size = 10)
        @SortDefaults({
            @SortDefault(sort = "createdDate", direction = Direction.DESC),
            @SortDefault(sort = "userReportId", direction = Direction.DESC)}) Pageable pageable) {
        SliceResponse<UserReportSearchResponseDto> response = adminService.getUserReports(status, pageable);
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

    @PatchMapping("/items/report/{itemReportId}")
    public ResponseEntity<?> receiveItemReport(@PathVariable("itemReportId") Long itemReportId,
        @Valid @RequestBody AdminItemReportRequestDto requestDto) {
        adminService.receiveItemReport(itemReportId, requestDto.isDeleted());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/users/report/{userReportId}")
    public ResponseEntity<?> receiveUserReport(@PathVariable("userReportId") Long userReportId,
        @Valid @RequestBody AdminUserReportRequestDto requestDto) {
        adminService.receiveUserReport(userReportId, requestDto.isSuspended(),
            requestDto.suspendReason(), requestDto.suspendPeriod());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/qa")
    public ResponseEntity<SliceResponse<AdminQaSearchResponseDto>> getQuestions(
        @RequestParam(defaultValue = "all", name = "status") String status,
        @PageableDefault(page = 0, size = 10)
        @SortDefaults({
            @SortDefault(sort = "createdDate", direction = Direction.DESC),
            @SortDefault(sort = "qaId", direction = Direction.DESC)}) Pageable pageable) {
        SliceResponse<AdminQaSearchResponseDto> response = adminService.getQuestions(status, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/qa/{qaId}")
    public ResponseEntity<AdminQaResponseDto> getQuestionDetails(@PathVariable("qaId") Long qaId) {
        AdminQaResponseDto response = adminService.getQuestion(qaId);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/qa/{qaId}")
    public ResponseEntity<?> answerQuestion(@PathVariable("qaId") Long qaId,
        @Valid @RequestBody AdminQaRequestDto requestDto) {
        adminService.answerQuestion(qaId, requestDto.answerDescription());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/items/{itemId}")
    public ResponseEntity<ItemDetailsViewResponseDto> getItemDetails(@Login AuthUserDto user,
        @PathVariable("itemId") Long itemId) {
        ItemDetailsViewResponseDto response = adminService.getItemDetails(itemId, user.getUserId());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<?> deleteItem(@PathVariable("itemId") Long itemId) {
        adminService.deleteItem(itemId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/profile/{userId}")
    public ResponseEntity<OtherProfileResponseDto> getUserProfile(
        @PathVariable("userId") Long userId) {
        OtherProfileResponseDto response = adminService.getUserProfile(userId);
        return ResponseEntity.ok(response);
    }
}
