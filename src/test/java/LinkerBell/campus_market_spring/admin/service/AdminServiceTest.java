package LinkerBell.campus_market_spring.admin.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.anyBoolean;
import static org.mockito.BDDMockito.anyLong;
import static org.mockito.BDDMockito.anyString;
import static org.mockito.BDDMockito.assertArg;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.times;

import LinkerBell.campus_market_spring.admin.dto.AdminCampusesResponseDto;
import LinkerBell.campus_market_spring.domain.Campus;
import LinkerBell.campus_market_spring.domain.ItemReport;
import LinkerBell.campus_market_spring.domain.QA;
import LinkerBell.campus_market_spring.domain.Role;
import LinkerBell.campus_market_spring.domain.User;
import LinkerBell.campus_market_spring.domain.UserReport;
import LinkerBell.campus_market_spring.dto.AuthResponseDto;
import LinkerBell.campus_market_spring.global.error.ErrorCode;
import LinkerBell.campus_market_spring.global.error.exception.CustomException;
import LinkerBell.campus_market_spring.global.jwt.JwtUtils;
import LinkerBell.campus_market_spring.repository.BlacklistRepository;
import LinkerBell.campus_market_spring.repository.CampusRepository;
import LinkerBell.campus_market_spring.repository.ItemReportRepository;
import LinkerBell.campus_market_spring.repository.QaRepository;
import LinkerBell.campus_market_spring.repository.UserReportRepository;
import LinkerBell.campus_market_spring.repository.UserRepository;
import LinkerBell.campus_market_spring.service.GoogleAuthService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @InjectMocks
    private AdminService adminService;

    @Mock
    private GoogleAuthService googleAuthService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserReportRepository userReportRepository;

    @Mock
    private BlacklistRepository blacklistRepository;

    @Mock
    private QaRepository qaRepository;

    @Mock
    private ItemReportRepository itemReportRepository;

    @Mock
    private CampusRepository campusRepository;

    @Mock
    private JwtUtils jwtUtils;

    @Test
    @DisplayName("관리자 로그인 테스트")
    public void adminLoginTest() {
        // given
        User admin = createAdmin();
        String accessToken = "testAccessToken";
        String refreshToken = "testRefreshToken";
        given(googleAuthService.getEmailWithVerifyIdToken(anyString()))
            .willReturn("test@example.com");
        given(userRepository.findByLoginEmail("test@example.com")).willReturn(
            Optional.ofNullable(admin));
        given(jwtUtils.generateAccessToken(anyLong(), anyString(), any(Role.class)))
            .willReturn(accessToken);
        given(jwtUtils.generateRefreshToken(anyLong(), anyString(), any(Role.class)))
            .willReturn(refreshToken);
        // when
        AuthResponseDto responseDto = adminService.adminLogin("testIdToken");
        // then
        assertThat(responseDto).isNotNull();
        assertThat(responseDto.getAccessToken()).isEqualTo(accessToken);
        assertThat(responseDto.getRefreshToken()).isEqualTo(refreshToken);
    }

    @Test
    @DisplayName("관리자가 아닌 사용자 로그인 테스트")
    public void adminLoginWithoutAdminRoleTest() {
        // given
        User user = createUser(100L);
        given(googleAuthService.getEmailWithVerifyIdToken(anyString()))
            .willReturn("test@example.com");
        given(userRepository.findByLoginEmail("test@example.com")).willReturn(
            Optional.ofNullable(user));
        // when & then
        assertThatThrownBy(() -> adminService.adminLogin("testIdToken"))
            .isInstanceOf(CustomException.class)
            .hasMessage(ErrorCode.NOT_ADMINISTRATOR.getMessage());
    }

    @Test
    @DisplayName("사용자 신고 처리 테스트")
    public void receiveUserReportTest() {
        // given
        User user = createUser(100L);
        User target = createUser(101L);
        UserReport userReport = createUserReport(user, target, 1L);
        boolean isSuspended = true;
        int suspendPeriod = 5;
        String suspendReason = "test Reason";

        given(userReportRepository.findById(anyLong())).willReturn(Optional.ofNullable(userReport));
        given(blacklistRepository.findByUser(any(User.class))).willReturn(Optional.empty());

        // when
        adminService.receiveUserReport(userReport.getUserReportId(), isSuspended, suspendReason, suspendPeriod);

        // then
        then(blacklistRepository).should(times(1)).save(assertArg(b -> {
            assertThat(b).isNotNull();
            assertThat(b.getEndDate()).isAfter(LocalDateTime.now());
            assertThat(b.getReason()).isEqualTo(suspendReason);
        }));
    }

    @Test
    @DisplayName("문의 전체 목록 가져오기 테스트")
    public void getAllQuestionsTest() {
        // given
        Sort sort = Sort.by(Direction.DESC, "createdDate")
            .and(Sort.by(Direction.DESC, "qaId"));
        Pageable pageable = PageRequest.of(0, 20, sort);
        String status = "all";
        Page<QA> pageResponse = new PageImpl<>(List.of(), pageable, 0);

        given(qaRepository.findQAs(any(Pageable.class))).willReturn(pageResponse);
        // when
        adminService.getQuestions(status, pageable);
        // then
        then(qaRepository).should(times(1)).findQAs(assertArg((page) -> {
            assertThat(page).isNotNull();
            assertThat(page).isInstanceOf(Pageable.class);
        }));
        then(qaRepository).should(times(0)).findQAsByStatus(any(), any());
    }

    @Test
    @DisplayName("처리된 문의 목록 가져오기 테스트")
    public void getCompletedQuestionsTest() {
        // given
        Sort sort = Sort.by(Direction.DESC, "createdDate")
            .and(Sort.by(Direction.DESC, "qaId"));
        Pageable pageable = PageRequest.of(0, 20, sort);
        String status = "done";

        Page<QA> pageResponse = new PageImpl<>(List.of(), pageable, 0);
        given(qaRepository.findQAsByStatus(anyBoolean(), any(Pageable.class))).willReturn(
            pageResponse);
        // when
        adminService.getQuestions(status, pageable);
        // then
        then(qaRepository).should(times(0)).findQAs(any());
        then(qaRepository).should(times(1)).findQAsByStatus(assertArg(qaStatus -> {
            assertThat(qaStatus).isNotNull();
            assertThat(qaStatus).isTrue();
        }), assertArg(page -> {
            assertThat(page).isNotNull();
            assertThat(page).isInstanceOf(Pageable.class);
        }));
    }

    @Test
    @DisplayName("처리가 되지 않은 문의 목록 가져오기 테스트")
    public void getInProgressQuestionsTest() {
        // given
        Sort sort = Sort.by(Direction.DESC, "createdDate")
            .and(Sort.by(Direction.DESC, "qaId"));
        Pageable pageable = PageRequest.of(0, 20, sort);
        String status = "inprogress";

        Page<QA> pageResponse = new PageImpl<>(List.of(), pageable, 0);
        given(qaRepository.findQAsByStatus(anyBoolean(), any(Pageable.class))).willReturn(
            pageResponse);
        // when
        adminService.getQuestions(status, pageable);
        // then
        then(qaRepository).should(times(0)).findQAs(any());
        then(qaRepository).should(times(1)).findQAsByStatus(assertArg(qaStatus -> {
            assertThat(qaStatus).isNotNull();
            assertThat(qaStatus).isFalse();
        }), assertArg(page -> {
            assertThat(page).isNotNull();
            assertThat(page).isInstanceOf(Pageable.class);
        }));
    }

    @Test
    @DisplayName("전체 상품 신고 목록 가져오기 테스트")
    public void getAllItemReportsTest() {
        // given
        Sort sort = Sort.by(Direction.DESC, "createdDate")
            .and(Sort.by(Direction.DESC, "itemReportId"));
        Pageable pageable = PageRequest.of(0, 20, sort);
        String status = "all";
        Page<ItemReport> pageResponse = new PageImpl<>(List.of(), pageable, 0);
        given(itemReportRepository.findItemReports(any(Pageable.class))).willReturn(pageResponse);
        // when
        adminService.getItemReports(status, pageable);
        // then
        then(itemReportRepository).should(times(1)).findItemReports(assertArg(p -> {
            assertThat(p).isNotNull();
        }));
        then(itemReportRepository).should(times(0)).findItemReportsByStatus(any(), any());
    }

    @Test
    @DisplayName("처리된 상품 신고 목록 가져오기 테스트")
    public void getCompletedItemReportsTest() {
        // given
        Sort sort = Sort.by(Direction.DESC, "createdDate")
            .and(Sort.by(Direction.DESC, "itemReportId"));
        Pageable pageable = PageRequest.of(0, 20, sort);
        String status = "done";
        Page<ItemReport> pageResponse = new PageImpl<>(List.of(), pageable, 0);
        given(itemReportRepository.findItemReportsByStatus(anyBoolean(),
            any(Pageable.class))).willReturn(pageResponse);
        // when
        adminService.getItemReports(status, pageable);
        // then
        then(itemReportRepository).should(times(0)).findItemReports(any());
        then(itemReportRepository).should(times(1)).findItemReportsByStatus(assertArg(st -> {
            assertThat(st).isNotNull();
            assertThat(st).isTrue();
        }), any());
    }

    @Test
    @DisplayName("처리되지 않은 상품 신고 목록 가져오기 테스트")
    public void getInProgressItemReportsTest() {
        // given
        Sort sort = Sort.by(Direction.DESC, "createdDate")
            .and(Sort.by(Direction.DESC, "itemReportId"));
        Pageable pageable = PageRequest.of(0, 20, sort);
        String status = "inprogress";
        Page<ItemReport> pageResponse = new PageImpl<>(List.of(), pageable, 0);
        given(itemReportRepository.findItemReportsByStatus(anyBoolean(),
            any(Pageable.class))).willReturn(pageResponse);
        // when
        adminService.getItemReports(status, pageable);
        // then
        then(itemReportRepository).should(times(0)).findItemReports(any());
        then(itemReportRepository).should(times(1)).findItemReportsByStatus(assertArg(st -> {
            assertThat(st).isNotNull();
            assertThat(st).isFalse();
        }), any());
    }

    @Test
    @DisplayName("전체 사용자 신고 목록 가져오기 테스트")
    public void getAllUserReportsTest() {
        // given
        Sort sort = Sort.by(Direction.DESC, "createdDate")
            .and(Sort.by(Direction.DESC, "userReportId"));
        Pageable pageable = PageRequest.of(0, 20, sort);
        String status = "all";
        Page<UserReport> pageResponse = new PageImpl<>(List.of(), pageable, 0);
        given(userReportRepository.findUserReports(any(Pageable.class))).willReturn(pageResponse);
        // when
        adminService.getUserReports(status, pageable);
        // then
        then(userReportRepository).should(times(1)).findUserReports(assertArg(p -> {
            assertThat(p).isNotNull();
        }));
        then(userReportRepository).should(times(0)).findUserReportsByStatus(any(), any());
    }

    @Test
    @DisplayName("처리되지 않은 사용자 신고 목록 가져오기 테스트")
    public void getInProgressUserReportsTest() {
        // given
        Sort sort = Sort.by(Direction.DESC, "createdDate")
            .and(Sort.by(Direction.DESC, "userReportId"));
        Pageable pageable = PageRequest.of(0, 20, sort);
        String status = "inprogress";
        Page<UserReport> pageResponse = new PageImpl<>(List.of(), pageable, 0);
        given(userReportRepository.findUserReportsByStatus(anyBoolean(),
            any(Pageable.class))).willReturn(pageResponse);
        // when
        adminService.getUserReports(status, pageable);
        // then
        then(userReportRepository).should(times(0)).findUserReports(any());
        then(userReportRepository).should(times(1)).findUserReportsByStatus(assertArg(st -> {
            assertThat(st).isNotNull();
            assertThat(st).isFalse();
        }), any());
    }

    @Test
    @DisplayName("처리된 사용자 신고 목록 가져오기 테스트")
    public void getCompletedUserReportsTest() {
        // given
        Sort sort = Sort.by(Direction.DESC, "createdDate")
            .and(Sort.by(Direction.DESC, "userReportId"));
        Pageable pageable = PageRequest.of(0, 20, sort);
        String status = "done";
        Page<UserReport> pageResponse = new PageImpl<>(List.of(), pageable, 0);
        given(userReportRepository.findUserReportsByStatus(anyBoolean(),
            any(Pageable.class))).willReturn(pageResponse);
        // when
        adminService.getUserReports(status, pageable);
        // then
        then(userReportRepository).should(times(0)).findUserReports(any());
        then(userReportRepository).should(times(1)).findUserReportsByStatus(assertArg(st -> {
            assertThat(st).isNotNull();
            assertThat(st).isTrue();
        }), any());
    }

    @Test
    @DisplayName("캠퍼스 목록 가져오기 테스트")
    public void getCampusesTest() {
        // given
        Campus campus1 = Campus.builder()
            .campusId(1L)
            .universityName("Seoul National University")
            .region("Seoul")
            .build();

        Campus campus2 = Campus.builder()
            .campusId(2L)
            .universityName("Korea University")
            .region("Seoul")
            .build();

        List<Campus> campusList = List.of(campus1, campus2);
        given(campusRepository.findAll()).willReturn(campusList);

        // when
        AdminCampusesResponseDto responseDto = adminService.getCampuses();

        // then
        assertThat(responseDto).isNotNull();
        assertThat(responseDto.getCampuses()).isNotNull();
        assertThat(responseDto.getCampuses()).hasSize(2);

        assertThat(responseDto.getCampuses().get(0).getCampusId()).isEqualTo(1L);
        assertThat(responseDto.getCampuses().get(0).getCampusName())
            .isEqualTo("Seoul National University Seoul");

        assertThat(responseDto.getCampuses().get(1).getCampusId()).isEqualTo(2L);
        assertThat(responseDto.getCampuses().get(1).getCampusName())
            .isEqualTo("Korea University Seoul");

        then(campusRepository).should(times(1)).findAll();
    }

    private User createAdmin() {
        return User.builder()
            .loginEmail("test@example.com")
            .role(Role.ADMIN)
            .userId(1L).build();
    }

    private User createUser(Long userId) {
        return User.builder()
            .loginEmail("test" + userId + "@example.com")
            .role(Role.USER)
            .userId(userId).build();
    }

    private UserReport createUserReport(User user, User target, Long id) {
        return UserReport.builder()
            .userReportId(id)
            .user(user)
            .target(target)
            .description("test description" + id)
            .isCompleted(false).build();
    }
}
