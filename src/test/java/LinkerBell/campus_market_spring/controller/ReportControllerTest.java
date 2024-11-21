package LinkerBell.campus_market_spring.controller;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import LinkerBell.campus_market_spring.controller.AuthControllerTest.MockLoginArgumentResolver;
import LinkerBell.campus_market_spring.domain.ItemReportCategory;
import LinkerBell.campus_market_spring.domain.UserReportCategory;
import LinkerBell.campus_market_spring.global.error.GlobalExceptionHandler;
import LinkerBell.campus_market_spring.service.ReportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class ReportControllerTest {

    @InjectMocks
    private ReportController reportController;

    @Mock
    private ReportService reportService;

    MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(reportController)
            .setControllerAdvice(GlobalExceptionHandler.class)
            .setCustomArgumentResolvers(new MockLoginArgumentResolver()).build();
    }

    @Test
    @DisplayName("상품 신고 테스트")
    public void itemReportTest() throws Exception {
        // given
        Long itemId = 1L;
        String desc = "사기 같아요. 확인해주세요.";
        ItemReportCategory reportCategory = ItemReportCategory.FRAUD;
        String request = String.format("""
            {
                "description" : "%s",
                "category" : "%s"
            }
            """, desc, reportCategory.name());

        // when
        mockMvc.perform(post("/api/v1/items/" + itemId + "/report")
            .contentType(MediaType.APPLICATION_JSON)
            .content(request))
            .andExpect(status().isNoContent()).andDo(print());
        // then
        then(reportService).should(times(1)).reportItem(anyLong(), assertArg(id -> {
            assertThat(id).isEqualTo(itemId);
        }), assertArg(description -> {
            assertThat(description).isEqualTo(desc);
        }), assertArg(category -> {
            assertThat(category).isEqualTo(reportCategory);
        }));
    }

    @Test
    @DisplayName("사용자 신고 테스트")
    public void userReportTest() throws Exception {
        // given
        Long userId = 3L;
        String desc = "사기 같아요. 확인해주세요.";
        UserReportCategory reportCategory = UserReportCategory.FRAUD;
        String request = String.format("""
            {
                "description" : "%s",
                "category" : "%s"
            }
            """, desc, reportCategory.name());
        // when
        mockMvc.perform(post("/api/v1/users/" + userId + "/report")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
            .andDo(print());
        // then
        then(reportService).should(times(1)).reportUser(anyLong(), assertArg(id -> {
            assertThat(id).isEqualTo(userId);
        }), assertArg(description -> {
            assertThat(description).isEqualTo(desc);
        }), assertArg(category -> {
            assertThat(category).isEqualTo(reportCategory);
        }));
    }
}