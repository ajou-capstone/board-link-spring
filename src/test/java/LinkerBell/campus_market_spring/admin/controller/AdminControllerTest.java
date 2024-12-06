package LinkerBell.campus_market_spring.admin.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.assertArg;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import LinkerBell.campus_market_spring.admin.service.AdminService;
import LinkerBell.campus_market_spring.dto.AuthUserDto;
import LinkerBell.campus_market_spring.global.error.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
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

    @Test
    @DisplayName("모든 아이템 가져오는 컨트롤러 테스트")
    public void getAllItemDefaultTest() throws Exception {
        // when & then
        mockMvc.perform(get("/admin/api/v1/items")
                .param("name", "exampleItem")
                .param("category", "ELECTRONICS_IT")
                .param("minPrice", "100")
                .param("maxPrice", "1000")
                .param("isDeleted", "true")
                .param("campusId", "2")
                .param("page", "1")
                .param("size", "5")
                .param("sort", "price,asc")
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk());

        verify(adminService).getAllItems(
            anyLong(),
            argThat(name -> name.equals("exampleItem")),
            argThat(category -> category != null && category.name().equals("ELECTRONICS_IT")),
            argThat(minPrice -> minPrice != null && minPrice == 100),
            argThat(maxPrice -> maxPrice != null && maxPrice == 1000),
            argThat(isDeleted -> isDeleted != null && isDeleted),
            argThat(campusId -> campusId != null && campusId == 2),
            argThat(pageable -> {
                assertThat(pageable).isNotNull();
                assertThat(pageable.getPageNumber()).isEqualTo(1);
                assertThat(pageable.getPageSize()).isEqualTo(5);
                assertThat(pageable.getSort()).isEqualTo(Sort.by(Sort.Direction.ASC, "price"));
                return true;
            })
        );
    }

    @Test
    @DisplayName("캠퍼스 목록 리스트 테스트")
    public void getCampusListTest() throws Exception {

        // when & then
        mockMvc.perform(get("/admin/api/v1/campuses"))
            .andDo(print())
            .andExpect(status().isOk());
        verify(adminService, times(1)).getCampuses();
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