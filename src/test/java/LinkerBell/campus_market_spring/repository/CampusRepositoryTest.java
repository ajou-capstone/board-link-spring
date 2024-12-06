package LinkerBell.campus_market_spring.repository;

import static org.assertj.core.api.Assertions.assertThat;

import LinkerBell.campus_market_spring.domain.Campus;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class CampusRepositoryTest {

    @Autowired
    CampusRepository campusRepository;

    @BeforeEach
    public void setUp() {
        Campus campus = createCampus();
        Campus campus2 = createCampus2();
        campusRepository.save(campus);
        campusRepository.save(campus2);
    }

    @Test
    @DisplayName("이메일에 맞는 캠퍼스 정보 가져오기 테스트")
    public void findByEmailTest() {
        // given
        String email = "ajou.ac.kr";
        // when
        List<Campus> campusList = campusRepository.findByEmail(email);
        // then
        assertThat(campusList.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("존재하지 않는 이메일 테스트")
    public void findByEmailButNoneTest() {
        // given
        String email = "abc.ac.kr";
        // when
        List<Campus> campusList = campusRepository.findByEmail(email);
        // then
        assertThat(campusList.size()).isEqualTo(0);
        System.out.println(campusList);
    }

    @Test
    @DisplayName("모든 캠퍼스 가져오는 테스트")
    public void getAllCampusTest() {
        // given
        List<Campus> campusList = campusRepository.findAll();
        // when & then
        assertThat(campusList).isNotNull();
        assertThat(campusList.size()).isEqualTo(2);
        System.out.println(campusList);
    }

    private Campus createCampus() {
        return Campus.builder()
            .email("ajou.ac.kr")
            .region("수원시")
            .universityName("아주대학교").build();
    }

    private Campus createCampus2() {
        return Campus.builder()
            .email("ajou.ac.kr")
            .region("수원시2")
            .universityName("아주대학교2").build();
    }
}
