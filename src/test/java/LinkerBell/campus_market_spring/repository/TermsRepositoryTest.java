package LinkerBell.campus_market_spring.repository;

import static org.assertj.core.api.Assertions.*;

import LinkerBell.campus_market_spring.domain.Terms;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class TermsRepositoryTest {

    @Autowired
    TermsRepository termsRepository;

    @BeforeEach
    public void setUp() {
        Terms terms1 = Terms.builder()
            .termsId(1L).termsUrl("test1").title("test term1").isRequired(true).build();
        Terms terms2 = Terms.builder()
            .termsId(2L).termsUrl("test2").title("test term2").isRequired(false).build();

        termsRepository.save(terms1);
        termsRepository.save(terms2);
    }

    @Test
    public void findAllTest() {
        // given

        // when
        List<Terms> terms = termsRepository.findAll();
        // then
        assertThat(terms.size()).isEqualTo(2);

        assertThat(terms.get(0).getTitle()).isEqualTo("test term1");
    }

}