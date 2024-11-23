package LinkerBell.campus_market_spring.controller;

import static LinkerBell.campus_market_spring.global.error.ErrorCode.INVALID_KEYWORD_ID;
import static LinkerBell.campus_market_spring.global.error.ErrorCode.INVALID_KEYWORD_NAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import LinkerBell.campus_market_spring.controller.ItemControllerTest.MockLoginArgumentResolver;
import LinkerBell.campus_market_spring.dto.KeywordRegisterRequestDto;
import LinkerBell.campus_market_spring.dto.KeywordRegisterResponseDto;
import LinkerBell.campus_market_spring.dto.KeywordResponseDto;
import LinkerBell.campus_market_spring.global.error.GlobalExceptionHandler;
import LinkerBell.campus_market_spring.global.error.exception.CustomException;
import LinkerBell.campus_market_spring.service.KeywordService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.MethodArgumentNotValidException;

@ExtendWith(MockitoExtension.class)
class KeywordControllerTest {

    private MockMvc mockMvc;

    private final MockLoginArgumentResolver mockLoginArgumentResolver = new MockLoginArgumentResolver();

    @Mock
    KeywordService keywordService;
    @InjectMocks
    KeywordController keywordController;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(keywordController)
            .setControllerAdvice(GlobalExceptionHandler.class)
            .setCustomArgumentResolvers(mockLoginArgumentResolver)
            .build();
    }

    @Test
    @DisplayName("정상적인 키워드 등록 테스트")
    void keywordRegister_Success() throws Exception {
        // Given
        String keywordRequestJson = """
            {
              "keywordName": "newKeyword"
            }
            """;

        KeywordRegisterResponseDto responseDto = KeywordRegisterResponseDto.builder()
            .keywordId(1L)
            .build();

        Mockito.when(keywordService.addKeyword(anyLong(), any(KeywordRegisterRequestDto.class)))
            .thenReturn(responseDto);

        // When & Then
        mockMvc.perform(post("/api/v1/keywords")
                .contentType(MediaType.APPLICATION_JSON)
                .content(keywordRequestJson))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.keywordId").value(1L));
    }

    @Test
    @DisplayName("빈 키워드 등록시 에러나는 테스트")
    void keywordRegister_Fail_EmptyKeywordName() throws Exception {
        // Given
        String keywordRequestJson = """
            {
              "keywordName": ""
            }
            """;

        // When & Then
        mockMvc.perform(post("/api/v1/keywords")
                .contentType(MediaType.APPLICATION_JSON)
                .content(keywordRequestJson))
            .andDo(print())
            .andExpect(
                result -> assertThat(result.getResolvedException().getClass()).isAssignableFrom(
                    MethodArgumentNotValidException.class))
            .andExpect(jsonPath("$.code").value(INVALID_KEYWORD_NAME.getCode()));
    }

    @Test
    @DisplayName("최대 키워드범위 이상일 때 에러나는 테스트")
    void keywordRegister_Fail_OverKeywordName() throws Exception {
        // Given
        String keywordRequestJson = """
            {
              "keywordName": "12345678901234567890123456"
            }
            """;

        // When & Then
        mockMvc.perform(post("/api/v1/keywords")
                .contentType(MediaType.APPLICATION_JSON)
                .content(keywordRequestJson))
            .andDo(print())
            .andExpect(
                result -> assertThat(result.getResolvedException().getClass()).isAssignableFrom(
                    MethodArgumentNotValidException.class))
            .andExpect(jsonPath("$.code").value(INVALID_KEYWORD_NAME.getCode()));
    }

    @Test
    @DisplayName("keywordList 성공적으로 가져오는 테스트")
    void getKeywords_Success() throws Exception {
        // Given
        List<KeywordResponseDto> keywords = List.of(
            new KeywordResponseDto(1L, "keyword1"),
            new KeywordResponseDto(2L, "keyword2")
        );
        Mockito.when(keywordService.getKeywords(anyLong())).thenReturn(keywords);

        // When & Then
        mockMvc.perform(get("/api/v1/keywords"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.keywordList[0].keywordId").value(1L))
            .andExpect(jsonPath("$.keywordList[0].keywordName").value("keyword1"))
            .andExpect(jsonPath("$.keywordList[1].keywordId").value(2L))
            .andExpect(jsonPath("$.keywordList[1].keywordName").value("keyword2"));
    }

    @Test
    @DisplayName("빈 keywordList 성공적으로 가져오는 테스트")
    void getKeywords_EmptyList() throws Exception {
        // Given
        Mockito.when(keywordService.getKeywords(anyLong())).thenReturn(List.of());

        // When & Then
        mockMvc.perform(get("/api/v1/keywords"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.keywordList").isEmpty());
    }

    @Test
    @DisplayName("keyword 성공적으로 삭제하는 테스트")
    void deleteKeyword_Success() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/v1/keywords/{keywordId}", 1L))
            .andExpect(status().isNoContent());

        verify(keywordService).deleteKeyword(anyLong(), eq(1L));
    }

    @Test
    @DisplayName("올바르지 않은 keywordId를 삭제하려 할 때 에러나는 테스트")
    void deleteKeyword_Fail_InvalidKeywordId() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/v1/keywords/{keywordId}", -1L))
            .andExpect(
                result -> assertThat(result.getResolvedException().getClass()).isAssignableFrom(
                    CustomException.class))
            .andExpect(jsonPath("$.code").value(INVALID_KEYWORD_ID.getCode()));
    }


}