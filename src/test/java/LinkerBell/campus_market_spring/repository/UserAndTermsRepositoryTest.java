package LinkerBell.campus_market_spring.repository;

import static org.assertj.core.api.Assertions.*;

import LinkerBell.campus_market_spring.domain.Terms;
import LinkerBell.campus_market_spring.domain.User;
import LinkerBell.campus_market_spring.domain.UserAndTerms;
import java.util.List;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class UserAndTermsRepositoryTest {

    @Autowired
    UserAndTermsRepository userAndTermsRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    TermsRepository termsRepository;

    User user;

    Terms terms1;

    Terms terms2;

    UserAndTerms userAndTerms1;
    UserAndTerms userAndTerms2;


    @BeforeEach
    public void setUp() {
        user = createUser();

        List<Terms> terms = createTerms();
        terms1 = terms.get(0);
        terms2 = terms.get(1);

        userAndTerms1 = UserAndTerms.builder().user(user).terms(terms1).isAgree(false).build();
        userAndTerms2 = UserAndTerms.builder().user(user).terms(terms2).isAgree(false).build();

        userRepository.save(user);
        termsRepository.save(terms1);
        termsRepository.save(terms2);
        userAndTermsRepository.save(userAndTerms1);
        userAndTermsRepository.save(userAndTerms2);
    }

    @Test
    @DisplayName("사용자 약관 동의 정보 가져오기 테스트")
    public void findByUserIdTest() {
        // given

        // when
        List<UserAndTerms> userAndTerms = userAndTermsRepository.findAllByUserId(user.getUserId());
        // then

        assertThat(userAndTerms.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("약관 동의 테스트")
    public void updateUTTest() {
        // given
        List<UserAndTerms> userAndTerms = Lists.newArrayList(userAndTerms1, userAndTerms2);
        // when
        for (UserAndTerms ut : userAndTerms) {
            ut.setAgree(true);
        }

        userAndTermsRepository.saveAll(userAndTerms);
        // then
        assertThat(userAndTerms.get(0).isAgree()).isTrue();
        assertThat(userAndTerms.get(1).isAgree()).isTrue();
    }

    private User createUser() {
        return User.builder()
            .loginEmail("test@example.com")
            .build();
    }

    private List<Terms> createTerms() {
        Terms term1 = Terms.builder().title("test1").termsUrl("test1_url")
            .isRequired(true).build();
        Terms term2 = Terms.builder().title("test2").termsUrl("test2_url")
            .isRequired(false).build();
        return Lists.newArrayList(term1, term2);
    }

}
