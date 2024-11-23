package LinkerBell.campus_market_spring.admin.service;

import LinkerBell.campus_market_spring.admin.dto.AdminItemSearchResponseDto;
import LinkerBell.campus_market_spring.admin.dto.ItemReportSearchResponseDto;
import LinkerBell.campus_market_spring.admin.dto.UserReportSearchResponseDto;
import LinkerBell.campus_market_spring.domain.Category;
import LinkerBell.campus_market_spring.admin.dto.ItemReportResponseDto;
import LinkerBell.campus_market_spring.admin.dto.UserReportResponseDto;
import LinkerBell.campus_market_spring.domain.ItemReport;
import LinkerBell.campus_market_spring.domain.Role;
import LinkerBell.campus_market_spring.domain.User;
import LinkerBell.campus_market_spring.domain.UserReport;
import LinkerBell.campus_market_spring.dto.AuthResponseDto;
import LinkerBell.campus_market_spring.dto.SliceResponse;
import LinkerBell.campus_market_spring.global.error.ErrorCode;
import LinkerBell.campus_market_spring.global.error.exception.CustomException;
import LinkerBell.campus_market_spring.global.jwt.JwtUtils;
import LinkerBell.campus_market_spring.repository.ItemReportRepository;
import LinkerBell.campus_market_spring.repository.ItemRepository;
import LinkerBell.campus_market_spring.repository.UserReportRepository;
import LinkerBell.campus_market_spring.repository.UserRepository;
import LinkerBell.campus_market_spring.service.GoogleAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AdminService {

    private final GoogleAuthService googleAuthService;
    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;
    private final ItemReportRepository itemReportRepository;
    private final UserReportRepository userReportRepository;
    private final ItemRepository itemRepository;

    public AuthResponseDto adminLogin(String idToken) {
        String email = googleAuthService.getEmailWithVerifyIdToken(idToken);

        User user = userRepository.findByLoginEmail(email).orElseThrow(() ->
            new CustomException(ErrorCode.USER_NOT_FOUND));

        if (user.getRole() != Role.ADMIN) {
            throw new CustomException(ErrorCode.NOT_ADMINISTRATOR);
        }

        String accessToken = jwtUtils.generateAccessToken(user.getUserId(), user.getLoginEmail(),
            user.getRole());
        String refreshToken = jwtUtils.generateRefreshToken(user.getUserId(), user.getLoginEmail(),
            user.getRole());

        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        return AuthResponseDto.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken).build();
    }

    @Transactional(readOnly = true)
    public SliceResponse<ItemReportSearchResponseDto> getItemReports(Pageable pageable) {
        Slice<ItemReport> itemReports = itemReportRepository.findItemReports(pageable);

        return new SliceResponse<>(itemReports.map(ItemReportSearchResponseDto::new));
    }

    @Transactional(readOnly = true)
    public SliceResponse<UserReportSearchResponseDto> getUserReports(Pageable pageable) {
        Slice<UserReport> userReports = userReportRepository.findUserReports(pageable);

        return new SliceResponse<>(userReports.map(UserReportSearchResponseDto::new));
    }

    @Transactional(readOnly = true)
    public SliceResponse<AdminItemSearchResponseDto> getAllItems(Long userId, String name,
        Category category, Integer minPrice, Integer maxPrice, Pageable pageable) {
        return itemRepository.adminItemSearch(userId, name, category, minPrice, maxPrice, pageable);
    }

    @Transactional(readOnly = true)
    public ItemReportResponseDto getItemReport(Long itemReportId) {
        ItemReport itemReport = itemReportRepository.findById(itemReportId)
            .orElseThrow(() -> new CustomException(ErrorCode.ITEM_REPORT_NOT_FOUND));

        return new ItemReportResponseDto(itemReport);
    }

    @Transactional(readOnly = true)
    public UserReportResponseDto getUserReport(Long userReportId) {
        UserReport userReport = userReportRepository.findById(userReportId)
            .orElseThrow(() -> new CustomException(ErrorCode.USER_REPORT_NOT_FOUND));

        return new UserReportResponseDto(userReport);
    }

    public void receiveItemReport(Long itemReportId, boolean isDeleted) {
        ItemReport itemReport = itemReportRepository.findById(itemReportId)
            .orElseThrow(() -> new CustomException(ErrorCode.ITEM_REPORT_NOT_FOUND));

        if (itemReport.getItem() == null) {
            throw new CustomException(ErrorCode.ITEM_NOT_FOUND);
        }
        itemReport.getItem().setDeleted(isDeleted);
        itemReport.setCompleted(true);
        itemReportRepository.save(itemReport);
    }
}
