package LinkerBell.campus_market_spring.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import LinkerBell.campus_market_spring.domain.QA;
import LinkerBell.campus_market_spring.domain.QaCategory;
import LinkerBell.campus_market_spring.domain.User;
import LinkerBell.campus_market_spring.repository.QaRepository;
import LinkerBell.campus_market_spring.repository.UserRepository;
import java.util.Optional;
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

    @Test
    @DisplayName("문의 작성 테스트")
    public void postQuestionTest() {
        // given
        User user = User.builder().userId(1L).loginEmail("test@example.com").build();
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

}