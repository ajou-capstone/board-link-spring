package LinkerBell.campus_market_spring.repository;

import static org.assertj.core.api.Assertions.*;

import LinkerBell.campus_market_spring.domain.Campus;
import LinkerBell.campus_market_spring.domain.QA;
import LinkerBell.campus_market_spring.domain.QaCategory;
import LinkerBell.campus_market_spring.domain.Role;
import LinkerBell.campus_market_spring.domain.User;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.jdbc.core.JdbcTemplate;

@DataJpaTest
class QaRepositoryTest {

    @Autowired
    private QaRepository qaRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CampusRepository campusRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private Campus campus;

    private User user;

    @BeforeEach
    public void setUp() {
        campus = createCampus();
        campus = campusRepository.save(campus);
        user = createUser(1L, campus);
        user = userRepository.save(user);

        List<QA> qas = new ArrayList<>();
        for (int i = 1; i <= 20; i++) {
            QA qa = QA.builder()
                .isCompleted(i % 2 == 0).user(user)
                .description("test reason " + i)
                .category(QaCategory.values()[(i - 1) % QaCategory.values().length]).build();
            qas.add(qa);
        }
        qas = qaRepository.saveAll(qas);
        int i = 0;
        for(QA qa : qas) {
            LocalDateTime date = LocalDateTime.of(2023, LocalDate.now().getMonth(), 25 - i, 0 ,0);
            Timestamp timestamp = Timestamp.valueOf(date);
            String sql = "UPDATE qa SET created_date=? WHERE qa_id= ?;";
            jdbcTemplate.update(sql, timestamp, qa.getQaId());
            i += 1;
            qa.setCreatedDate(date);
            qaRepository.save(qa);
        }
    }

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

    @Test
    @DisplayName("관리자용 문의 목록 가져오기 테스트")
    public void getQuestionsForAdminTest() {
        // given
        Sort sort = Sort.by(Direction.DESC, "createdDate")
            .and(Sort.by(Direction.DESC, "qaId"));
        Pageable pageable = PageRequest.of(0, 20, sort);
        // when
        Slice<QA> slice = qaRepository.findQAs(pageable);
        // then
        assertThat(slice).isNotNull();
        assertThat(slice.getContent().size()).isEqualTo(10);
        assertThat(slice.getContent().get(0).getCategory()).isEqualTo(QaCategory.values()[0]);
        assertThat(slice.getContent().get(0).getCreatedDate()).isAfter(slice.getContent().get(1).getCreatedDate());
    }

    @Test
    @DisplayName("문의 내용 가져오기 테스트")
    public void getQuestionDetailsTest() {
        // given
        QA qa = QA.builder()
            .user(user).isCompleted(false).title("Test title")
            .category(QaCategory.ACCOUNT_INQUIRY).description("Test description").build();
        qa = qaRepository.save(qa);
        // when
        Optional<QA> qaOpt = qaRepository.findById(qa.getQaId());
        // then
        assertThat(qaOpt).isPresent();
        assertThat(qaOpt.get().getTitle()).isEqualTo(qa.getTitle());
    }

    @Test
    @DisplayName("문의 답변 작성 테스트")
    public void answerQuestionTest() {
        // given
        QA qa = QA.builder()
            .user(user).isCompleted(false).title("Test title")
            .category(QaCategory.ACCOUNT_INQUIRY).description("Test description").build();
        qa = qaRepository.save(qa);
        // when
        qa.setCompleted(true);
        qa.setAnswerDescription("Test answer");
        qa.setAnswerDate(LocalDateTime.now());
        qa = qaRepository.save(qa);
        QA answeredQa = qaRepository.findById(qa.getQaId()).orElse(null);
        // then
        assertThat(answeredQa).isNotNull();
        assertThat(answeredQa.getAnswerDescription()).isEqualTo(qa.getAnswerDescription());
    }

    @Test
    @DisplayName("작성한 문의 리스트 가져오기 테스트")
    public void getQuestionListTest() {
        // given
        Sort sort = Sort.by(Direction.DESC, "createdDate")
            .and(Sort.by(Direction.DESC, "qaId"));
        Pageable pageable = PageRequest.of(0, 20, sort);
        // when
        Slice<QA> slice = qaRepository.findQASByUser(user, pageable);
        // then
        assertThat(slice).isNotNull();
        assertThat(slice.getContent().size()).isEqualTo(20);
        assertThat(slice.getContent().get(0).getCategory()).isEqualTo(QaCategory.values()[0]);
        assertThat(slice.getContent().get(0).getCreatedDate()).isAfter(slice.getContent().get(1).getCreatedDate());
    }

    private Campus createCampus() {
        return Campus.builder()
            .campusId(1L)
            .email("testUniv@example.com")
            .region("korea")
            .universityName("testUniv")
            .build();
    }

    private User createUser(Long userId, Campus campus) {
        return User.builder()
            .userId(userId)
            .role(Role.USER)
            .loginEmail("testEmail")
            .campus(campus)
            .schoolEmail("testSchool")
            .nickname("user" + userId).build();
    }
}
