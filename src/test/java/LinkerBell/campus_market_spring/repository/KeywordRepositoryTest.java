package LinkerBell.campus_market_spring.repository;

import static org.assertj.core.api.Assertions.assertThat;

import LinkerBell.campus_market_spring.domain.Campus;
import LinkerBell.campus_market_spring.domain.Keyword;
import LinkerBell.campus_market_spring.domain.Role;
import LinkerBell.campus_market_spring.domain.User;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class KeywordRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    CampusRepository campusRepository;

    @Autowired
    KeywordRepository keywordRepository;

    List<User> users;
    List<Campus> campuses;
    List<Keyword> keywords;

    @BeforeEach
    void beforeEach() {
        users = new ArrayList<>();
        campuses = new ArrayList<>();
        keywords = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            Campus campus = Campus.builder()
                .universityName("campus" + i)
                .region("수원")
                .email("abc" + i).build();
            campusRepository.save(campus);
            campuses.add(campus);
        }

        for (int i = 0; i < 10; i++) {
            User user;

            if (i < 3) {
                user = User.builder()
                    .nickname("user" + i)
                    .campus(campuses.get(0))
                    .role(Role.USER)
                    .build();
            } else if (i < 6) {
                user = User.builder()
                    .nickname("user" + i)
                    .campus(campuses.get(1))
                    .role(Role.USER)
                    .build();
            } else {
                user = User.builder()
                    .nickname("user" + i)
                    .campus(campuses.get(2))
                    .role(Role.USER)
                    .build();
            }
            userRepository.save(user);
            users.add(user);
        }

        for (int i = 0; i < 10; i++) {
            Keyword keyword;

            if (i < 3) {
                keyword = Keyword.builder()
                    .keywordName("first" + i)
                    .user(users.get(0))
                    .build();
            } else if (i < 6) {
                keyword = Keyword.builder()
                    .keywordName("second" + i)
                    .user(users.get(6))
                    .build();
            } else {
                keyword = Keyword.builder()
                    .keywordName("third" + i)
                    .user(users.get(9))
                    .build();
            }
            keywordRepository.save(keyword);
            keywords.add(keyword);
        }
    }

    @AfterEach
    void afterEach() {
        userRepository.deleteAll();
        campusRepository.deleteAll();
        keywordRepository.deleteAll();
    }

    @Test
    @DisplayName("유저와 캠퍼스를 모두 가져오는 함수 test")
    public void findKeywordsWithUserAndCampusTest() throws Exception {
        //given

        //when
        List<Keyword> keywordsWithUserAndCampus = keywordRepository.findKeywordsWithUserAndCampus();
        //then
        assertThat(keywordsWithUserAndCampus.size()).isEqualTo(keywords.size());

    }

    @Test
    @DisplayName("userId를 바탕으로 유저 별로 쓴 키워드 가져오는 함수 test")
    public void findKeywordByUser_UserIdTest() throws Exception {
        //given

        //when
        List<Keyword> keywordsByUserId = keywordRepository.findKeywordByUser_UserId(
            users.get(0).getUserId());
        //then
        assertThat(keywordsByUserId.size()).isEqualTo(3);
        assertThat(keywordsByUserId.get(0)).isEqualTo(keywords.get(0));
        assertThat(keywordsByUserId.get(1)).isEqualTo(keywords.get(1));
        assertThat(keywordsByUserId.get(2)).isEqualTo(keywords.get(2));
    }

}