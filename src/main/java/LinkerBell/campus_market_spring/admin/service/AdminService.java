package LinkerBell.campus_market_spring.admin.service;

import LinkerBell.campus_market_spring.admin.dto.ItemReportSearchResponseDto;
import LinkerBell.campus_market_spring.domain.ItemReport;
import LinkerBell.campus_market_spring.domain.Role;
import LinkerBell.campus_market_spring.domain.User;
import LinkerBell.campus_market_spring.dto.AuthResponseDto;
import LinkerBell.campus_market_spring.dto.SliceResponse;
import LinkerBell.campus_market_spring.global.error.ErrorCode;
import LinkerBell.campus_market_spring.global.error.exception.CustomException;
import LinkerBell.campus_market_spring.global.jwt.JwtUtils;
import LinkerBell.campus_market_spring.repository.ItemReportRepository;
import LinkerBell.campus_market_spring.repository.UserRepository;
import LinkerBell.campus_market_spring.service.GoogleAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminService {

    private final GoogleAuthService googleAuthService;
    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;
    private final ItemReportRepository itemReportRepository;

    public AuthResponseDto adminLogin(String idToken) {
        String email = googleAuthService.getEmailWithVerifyIdToken(idToken);

        User user = userRepository.findByLoginEmail(email).orElseThrow(() ->
            new CustomException(ErrorCode.USER_NOT_FOUND));

        if (user.getRole() != Role.ADMIN) {
            throw new CustomException(ErrorCode.NOT_ADMINISTRATOR);
        }

        String accessToken = jwtUtils.generateAccessToken(user.getUserId(), user.getLoginEmail(), user.getRole());
        String refreshToken = jwtUtils.generateRefreshToken(user.getUserId(), user.getLoginEmail(), user.getRole());

        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        return AuthResponseDto.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken).build();
    }

    public SliceResponse<ItemReportSearchResponseDto> getItemReports(Pageable pageable) {
        Slice<ItemReport> itemReports = itemReportRepository.findItemReports(pageable);

        return new SliceResponse<>(itemReports.map(ItemReportSearchResponseDto::new));
    }
}
