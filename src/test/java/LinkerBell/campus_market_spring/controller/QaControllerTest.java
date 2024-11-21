package LinkerBell.campus_market_spring.controller;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import LinkerBell.campus_market_spring.controller.ProfileControllerTest.MockLoginArgumentResolver;
import LinkerBell.campus_market_spring.global.error.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class QaControllerTest {

    MockMvc mockMvc;

    @InjectMocks
    private QaController qaController;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(qaController)
            .setControllerAdvice(GlobalExceptionHandler.class)
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
}