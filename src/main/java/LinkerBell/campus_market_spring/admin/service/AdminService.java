package LinkerBell.campus_market_spring.admin.service;

import LinkerBell.campus_market_spring.admin.dto.AdminCampusResponseDto;
import LinkerBell.campus_market_spring.admin.dto.AdminCampusesResponseDto;
import LinkerBell.campus_market_spring.admin.dto.AdminItemSearchResponseDto;
import LinkerBell.campus_market_spring.admin.dto.AdminQaResponseDto;
import LinkerBell.campus_market_spring.admin.dto.AdminQaSearchResponseDto;
import LinkerBell.campus_market_spring.admin.dto.ItemReportSearchResponseDto;
import LinkerBell.campus_market_spring.admin.dto.UserReportSearchResponseDto;
import LinkerBell.campus_market_spring.domain.Blacklist;
import LinkerBell.campus_market_spring.domain.Campus;
import LinkerBell.campus_market_spring.domain.Category;
import LinkerBell.campus_market_spring.admin.dto.ItemReportResponseDto;
import LinkerBell.campus_market_spring.admin.dto.UserReportResponseDto;
import LinkerBell.campus_market_spring.domain.Item;
import LinkerBell.campus_market_spring.domain.ItemReport;
import LinkerBell.campus_market_spring.domain.QA;
import LinkerBell.campus_market_spring.domain.Role;
import LinkerBell.campus_market_spring.domain.User;
import LinkerBell.campus_market_spring.domain.UserReport;
import LinkerBell.campus_market_spring.dto.AuthResponseDto;
import LinkerBell.campus_market_spring.dto.ItemDetailsViewResponseDto;
import LinkerBell.campus_market_spring.dto.OtherProfileResponseDto;
import LinkerBell.campus_market_spring.dto.SliceResponse;
import LinkerBell.campus_market_spring.dto.UserInfoDto;
import LinkerBell.campus_market_spring.global.error.ErrorCode;
import LinkerBell.campus_market_spring.global.error.exception.CustomException;
import LinkerBell.campus_market_spring.global.jwt.JwtUtils;
import LinkerBell.campus_market_spring.repository.BlacklistRepository;
import LinkerBell.campus_market_spring.repository.CampusRepository;
import LinkerBell.campus_market_spring.repository.ItemReportRepository;
import LinkerBell.campus_market_spring.repository.ItemRepository;
import LinkerBell.campus_market_spring.repository.QaRepository;
import LinkerBell.campus_market_spring.repository.UserReportRepository;
import LinkerBell.campus_market_spring.repository.UserRepository;
import LinkerBell.campus_market_spring.service.GoogleAuthService;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
    private final BlacklistRepository blacklistRepository;
    private final QaRepository qaRepository;
    private final CampusRepository campusRepository;

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

        return AuthResponseDto.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken).build();
    }

    @Transactional(readOnly = true)
    public SliceResponse<ItemReportSearchResponseDto> getItemReports(String status,
        Pageable pageable) {
        Slice<ItemReport> itemReports;
        if (StringUtils.equals(status, "all")) {
            itemReports = itemReportRepository.findItemReports(pageable);
        } else {
            Boolean itemReportStatus = StringUtils.equals(status, "done");
            itemReports = itemReportRepository.findItemReportsByStatus(itemReportStatus, pageable);
        }

        return new SliceResponse<>(itemReports.map(ItemReportSearchResponseDto::new));
    }

    @Transactional(readOnly = true)
    public SliceResponse<UserReportSearchResponseDto> getUserReports(String status,
        Pageable pageable) {
        Slice<UserReport> userReports;
        if (StringUtils.equals(status, "all")) {
            userReports = userReportRepository.findUserReports(pageable);
        } else {
            Boolean userReportStatus = StringUtils.equals(status, "done");
            userReports = userReportRepository.findUserReportsByStatus(userReportStatus, pageable);
        }

        return new SliceResponse<>(userReports.map(UserReportSearchResponseDto::new));
    }

    @Transactional(readOnly = true)
    public SliceResponse<AdminItemSearchResponseDto> getAllItems(Long userId, String name,
        Category category, Integer minPrice, Integer maxPrice, Boolean isDeleted, Long campusId,
        Pageable pageable) {
        return itemRepository.adminItemSearch(userId, name, category, minPrice, maxPrice, isDeleted,
            campusId, pageable);
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
    }

    public void receiveUserReport(Long userReportId, boolean isSuspended,
        String suspendReason, int suspendPeriod) {
        UserReport userReport = userReportRepository.findById(userReportId)
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        userReport.setCompleted(true);

        if (!isSuspended) {
            return;
        }
        LocalDateTime period = LocalDateTime.now().plusDays(suspendPeriod);
        User target = userReport.getTarget();
        Blacklist blacklist = blacklistRepository.findByUser(target)
            .orElseGet(() -> Blacklist.builder().reason(suspendReason)
                .user(target).endDate(period).build());

        if (blacklist.getEndDate().isBefore(period)) {
            blacklist.setEndDate(period);
            blacklist.setReason(suspendReason);
        }
        target.setRefreshToken(null);
        blacklistRepository.save(blacklist);
    }

    @Transactional(readOnly = true)
    public SliceResponse<AdminQaSearchResponseDto> getQuestions(String status, Pageable pageable) {
        Slice<QA> qas;
        if (StringUtils.equals(status, "all")) {
            qas = qaRepository.findQAs(pageable);
        } else {
            Boolean qaStatus = StringUtils.equals(status, "done");
            qas = qaRepository.findQAsByStatus(qaStatus, pageable);
        }
        return new SliceResponse<>(qas.map(AdminQaSearchResponseDto::new));
    }

    @Transactional(readOnly = true)
    public AdminQaResponseDto getQuestion(Long questionId) {
        QA qa = qaRepository.findById(questionId)
            .orElseThrow(() -> new CustomException(ErrorCode.QA_NOT_FOUND));

        return new AdminQaResponseDto(qa);
    }

    public void answerQuestion(Long qaId, String answer) {
        QA qa = qaRepository.findById(qaId)
            .orElseThrow(() -> new CustomException(ErrorCode.QA_NOT_FOUND));

        qa.setCompleted(true);
        qa.setAnswerDescription(answer);
        qa.setAnswerDate(LocalDateTime.now());
    }

    @Transactional(readOnly = true)
    public ItemDetailsViewResponseDto getItemDetails(Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId)
            .orElseThrow(() -> new CustomException(ErrorCode.ITEM_NOT_FOUND));

        return itemRepository.findByItemDetails(userId, itemId);
    }

    public void deleteItem(Long itemId) {
        Item item = itemRepository.findById(itemId)
            .orElseThrow(() -> new CustomException(ErrorCode.ITEM_NOT_FOUND));

        item.setDeleted(true);
    }

    @Transactional(readOnly = true)
    public UserInfoDto getUserProfile(Long userId) {
        UserInfoDto userInfo = userRepository.findUserInfoByUserId(userId)
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        return userInfo;
    }

    @Transactional(readOnly = true)
    public AdminCampusesResponseDto getCampuses() {
        List<Campus> campus = campusRepository.findAll();
        List<AdminCampusResponseDto> campuses = campus.stream()
            .map(AdminCampusResponseDto::new).toList();
        return AdminCampusesResponseDto.builder().campuses(campuses).build();
    }

    @Transactional(readOnly = true)
    public SliceResponse<UserInfoDto> getUsers(Pageable pageable) {
        Slice<UserInfoDto> users = userRepository.findUserInfoAll(pageable);
        return new SliceResponse<>(users);
    }
}
