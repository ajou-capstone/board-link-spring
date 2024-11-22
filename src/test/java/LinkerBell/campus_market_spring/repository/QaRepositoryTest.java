package LinkerBell.campus_market_spring.repository;

import static org.assertj.core.api.Assertions.*;

import LinkerBell.campus_market_spring.domain.QA;
import LinkerBell.campus_market_spring.domain.QaCategory;
import LinkerBell.campus_market_spring.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class QaRepositoryTest {

    @Autowired
    private QaRepository qaRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("문의 작성 테스트")
    public void postQuestionTest() {
        // given
        User user = User.builder().userId(1L).loginEmail("test@example.com")
            .build();
        user = userRepository.save(user);
        QA qa = QA.builder().qaId(1L).user(user).title("test Q")
            .category(QaCategory.ACCOUNT_INQUIRY).description("test")
            .isCompleted(false).build();
        // when
        QA findQa = qaRepository.save(qa);
        // then
        assertThat(findQa).isNotNull();
        assertThat(findQa.getTitle()).isEqualTo(qa.getTitle());
    }
}