package LinkerBell.campus_market_spring.controller;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import LinkerBell.campus_market_spring.controller.ProfileControllerTest.MockLoginArgumentResolver;
import LinkerBell.campus_market_spring.domain.QA;
import LinkerBell.campus_market_spring.domain.QaCategory;
import LinkerBell.campus_market_spring.dto.QaResponseDto;
import LinkerBell.campus_market_spring.global.error.GlobalExceptionHandler;
import LinkerBell.campus_market_spring.service.QaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class QaControllerTest {

    MockMvc mockMvc;

    @InjectMocks
    private QaController qaController;

    @Mock
    private QaService qaService;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(qaController)
            .setControllerAdvice(GlobalExceptionHandler.class)
            .setCustomArgumentResolvers(
                new PageableHandlerMethodArgumentResolver(),
                new MockLoginArgumentResolver())
            .build();
    }

    @Test
    @DisplayName("문의 카테고리 요청 테스트")
    public void qaCategoryTest() throws Exception {

        // when & then
        mockMvc.perform(get("/api/v1/questions"))
            .andDo(print())
            .andExpect(jsonPath("$.categories").isArray())
            .andExpect(jsonPath("$.categories[0]").isString());
    }

    @Test
    @DisplayName("문의 작성 테스트")
    public void postQuestionTest() throws Exception {
        QaCategory category = QaCategory.ACCOUNT_INQUIRY;
        String request = String.format("""
            {
                "title": "test Q",
                "category": "%s",
                "description": "test"
            }
            """, category.name());
        // when & then
        mockMvc.perform(post("/api/v1/questions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
            .andDo(print())
            .andExpect(status().isNoContent());

        then(qaService).should().postQuestion(anyLong(), assertArg(title -> {
            assertThat(title).isNotNull();
        }), assertArg(description -> {
            assertThat(description).isNotNull();
        }), assertArg(qaCategory -> {
            assertThat(qaCategory).isNotNull();
            assertThat(qaCategory).isEqualTo(category);
        }));
    }

    @Test
    @DisplayName("문의 답변 리스트 요청 테스트 + 페이징 요청 X")
    public void getQuestionsTestWithOutPageable() throws Exception {
        // when & then
        mockMvc.perform(get("/api/v1/answers"))
            .andDo(print())
            .andExpect(status().isOk())
            .andReturn();

        then(qaService).should(times(1)).getAnswers(anyLong(), assertArg(
            pageable -> {
                assertThat(pageable).isNotNull();
                assertThat(pageable.getSort()).isEqualTo(Sort.by(Sort.Direction.DESC, "createdDate")
                    .and(Sort.by(Sort.Direction.DESC, "qaId")));
                assertThat(pageable.getPageNumber()).isEqualTo(0);
                assertThat(pageable.getPageSize()).isEqualTo(10);
            }
        ));
    }

    @Test
    @DisplayName("문의 답변 리스트 요청 테스트")
    public void getQuestionsTest() throws Exception {
        // when & then
        mockMvc.perform(get("/api/v1/answers")
                .param("page", "1")
                .param("size", "5"))
            .andDo(print())
            .andExpect(status().isOk())
            .andReturn();

        then(qaService).should(times(1)).getAnswers(anyLong(), assertArg(
            pageable -> {
                assertThat(pageable).isNotNull();
                assertThat(pageable.getSort()).isEqualTo(Sort.by(Sort.Direction.DESC, "createdDate")
                    .and(Sort.by(Sort.Direction.DESC, "qaId")));
                assertThat(pageable.getPageNumber()).isEqualTo(1);
                assertThat(pageable.getPageSize()).isEqualTo(5);
            }
        ));
    }

    @Test
    @DisplayName("문의 답변 내용 보기 테스트")
    public void getQuestionDetailsTest() throws Exception {
        // given
        Long qaId = 1L;

        // when & then
        mockMvc.perform(get("/api/v1/answers/" + qaId))
            .andDo(print())
            .andExpect(status().isOk())
            .andReturn();
        then(qaService).should(times(1)).getAnswerDetails(anyLong(), assertArg(
            id -> {
            assertThat(id).isNotNull();
            assertThat(id).isEqualTo(qaId);
        }));
    }
}