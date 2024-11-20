package LinkerBell.campus_market_spring.admin.controller;

import LinkerBell.campus_market_spring.admin.dto.AdminLoginRequestDto;
import LinkerBell.campus_market_spring.admin.service.AdminService;
import LinkerBell.campus_market_spring.dto.AuthResponseDto;
import LinkerBell.campus_market_spring.dto.AuthUserDto;
import LinkerBell.campus_market_spring.global.auth.Login;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/api/v1")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> adminLogin(@RequestBody AdminLoginRequestDto requestDto) {
        AuthResponseDto authResponseDto = adminService.adminLogin(requestDto.idToken());
        return ResponseEntity.ok(authResponseDto);
    }

    @GetMapping("/test")
    public ResponseEntity<String> test(@Login AuthUserDto user) {
        return ResponseEntity.ok("Hello Admin! " + user.getLoginEmail());
    }
}
