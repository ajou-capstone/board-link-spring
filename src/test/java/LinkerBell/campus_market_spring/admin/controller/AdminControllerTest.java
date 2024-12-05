package LinkerBell.campus_market_spring.admin.controller;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import LinkerBell.campus_market_spring.admin.service.AdminService;
import LinkerBell.campus_market_spring.dto.AuthUserDto;
import LinkerBell.campus_market_spring.dto.SliceResponse;
import LinkerBell.campus_market_spring.global.error.GlobalExceptionHandler;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@ExtendWith(MockitoExtension.class)
class AdminControllerTest {

    MockMvc mockMvc;

    @InjectMocks
    private AdminController adminController;

    @Mock
    private AdminService adminService;

    private MockLoginArgumentResolver mockLoginArgumentResolver = new MockLoginArgumentResolver();

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(adminController).setControllerAdvice(
                GlobalExceptionHandler.class)
            .setCustomArgumentResolvers(
                new PageableHandlerMethodArgumentResolver(),
                mockLoginArgumentResolver)
            .build();
    }

    @Test
    @DisplayName("상품 신고 목록 리스트 테스트")
    public void getItemReportListTest() throws Exception {

        // when & then
        mockMvc.perform(get("/admin/api/v1/items/report")
                .queryParam("status", "all"))
            .andDo(print());

        then(adminService).should().getItemReports(assertArg(st -> {
            assertThat(st).isNotNull();
        }), assertArg(p -> {
            assertThat(p).isNotNull();
            assertThat(p.getSort()).isEqualTo(Sort.by(Sort.Direction.DESC, "createdDate")
                .and(Sort.by(Sort.Direction.DESC, "itemReportId")));
        }));
    }

    @Test
    @DisplayName("상품 신고 목록 리스트 테스트")
    public void getItemReportListWithNullStatusTest() throws Exception {

        // when & then
        mockMvc.perform(get("/admin/api/v1/items/report"))
            .andDo(print());

        then(adminService).should().getItemReports(assertArg(st -> {
            assertThat(st).isNotNull();
            assertThat(st).isEqualTo("all");
        }), assertArg(p -> {
            assertThat(p).isNotNull();
            assertThat(p.getSort()).isEqualTo(Sort.by(Sort.Direction.DESC, "createdDate")
                .and(Sort.by(Sort.Direction.DESC, "itemReportId")));
        }));
    }

    @Test
    @DisplayName("사용자 신고 목록 리스트 테스트")
    public void getUserReportListTest() throws Exception {

        // when & then
        mockMvc.perform(get("/admin/api/v1/users/report")
                .queryParam("status", "all"))
            .andDo(print());

        then(adminService).should().getUserReports(assertArg(st -> {
            assertThat(st).isNotNull();
        }), assertArg(p -> {
            assertThat(p).isNotNull();
            assertThat(p.getSort()).isEqualTo(Sort.by(Sort.Direction.DESC, "createdDate")
                .and(Sort.by(Sort.Direction.DESC, "userReportId")));
        }));
    }

    @Test
    @DisplayName("문의 목록 리스트 테스트")
    public void getQaListTest() throws Exception {

        // when & then
        mockMvc.perform(get("/admin/api/v1/qa")
                .queryParam("status", "done"))
            .andDo(print());

        then(adminService).should().getQuestions(assertArg(st -> {
            assertThat(st).isNotNull();
            assertThat(st).isEqualTo("done");
        }), assertArg(p -> {
            assertThat(p).isNotNull();
            assertThat(p.getSort()).isEqualTo(Sort.by(Sort.Direction.DESC, "createdDate")
                .and(Sort.by(Sort.Direction.DESC, "qaId")));
        }));
    }


    static class MockLoginArgumentResolver implements HandlerMethodArgumentResolver {

        @Override
        public boolean supportsParameter(MethodParameter parameter) {
            return true;
        }

        @Override
        public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
            return AuthUserDto.builder().userId(1L).build();
        }

    }
}