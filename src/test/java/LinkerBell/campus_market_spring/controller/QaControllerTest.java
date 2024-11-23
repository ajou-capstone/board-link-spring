package LinkerBell.campus_market_spring.controller;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import LinkerBell.campus_market_spring.controller.ProfileControllerTest.MockLoginArgumentResolver;
import LinkerBell.campus_market_spring.domain.QaCategory;
import LinkerBell.campus_market_spring.global.error.GlobalExceptionHandler;
import LinkerBell.campus_market_spring.service.QaService;
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
            .setCustomArgumentResolvers(new MockLoginArgumentResolver())
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
}