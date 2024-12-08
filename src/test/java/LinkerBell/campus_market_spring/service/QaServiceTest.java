package LinkerBell.campus_market_spring.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import LinkerBell.campus_market_spring.domain.QA;
import LinkerBell.campus_market_spring.domain.QaCategory;
import LinkerBell.campus_market_spring.domain.User;
import LinkerBell.campus_market_spring.dto.QaResponseDto;
import LinkerBell.campus_market_spring.global.error.ErrorCode;
import LinkerBell.campus_market_spring.global.error.exception.CustomException;
import LinkerBell.campus_market_spring.repository.QaRepository;
import LinkerBell.campus_market_spring.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class QaServiceTest {

    @InjectMocks
    private QaService qaService;

    @Mock
    private QaRepository qaRepository;
    @Mock
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    public void setUp() {
        user = User.builder().userId(1L).loginEmail("test@example.com").build();

    }

    @Test
    @DisplayName("문의 작성 테스트")
    public void postQuestionTest() {
        // given
        given(userRepository.findById(anyLong())).willReturn(Optional.ofNullable(user));
        QA qa = QA.builder().user(user).title("test Q").category(QaCategory.ACCOUNT_INQUIRY)
            .description("test").isCompleted(false).build();
        // when
        qaService.postQuestion(user.getUserId(), qa.getTitle(), qa.getDescription(), qa.getCategory());
        // then
        then(qaRepository).should(times(1)).save(assertArg(entity -> {
            assertThat(entity).isNotNull();
            assertThat(entity.getTitle()).isEqualTo(qa.getTitle());
        }));
    }

    @Test
    @DisplayName("문의 답변 내용 요청 테스트")
    public void getQuestionAnswerTest() {
        // given
        QA qa = QA.builder().user(user).category(QaCategory.ACCOUNT_INQUIRY).isCompleted(true)
            .qaId(1L).description("test Q").answerDescription("Test answer").answerDate(
                LocalDateTime.now()).build();
        given(qaRepository.findById(anyLong())).willReturn(Optional.ofNullable(qa));
        // when
        QaResponseDto response = qaService.getAnswerDetails(user.getUserId(), qa.getQaId());

        // then
        assertThat(response).isNotNull();
        assertThat(response.getUserId()).isEqualTo(qa.getQaId());
        assertThat(response.getAnswerDescription()).isEqualTo(qa.getAnswerDescription());
    }

    @Test
    @DisplayName("문의 답변 내용 요청 에러 테스트, 문의 작성자와 사용자가 다를 때")
    public void getQuestionAnswerWithErrorTest() {
        // given
        QA qa = QA.builder().user(user).category(QaCategory.ACCOUNT_INQUIRY).isCompleted(true)
            .qaId(1L).description("test Q").answerDescription("Test answer").answerDate(
                LocalDateTime.now()).build();
        given(qaRepository.findById(anyLong())).willReturn(Optional.ofNullable(qa));
        // when & then
        assertThatThrownBy(() -> qaService.getAnswerDetails(123123L, qa.getQaId()))
            .isInstanceOf(CustomException.class)
            .hasMessageContaining(ErrorCode.NOT_MATCH_QA_USER.getMessage());
    }
}
