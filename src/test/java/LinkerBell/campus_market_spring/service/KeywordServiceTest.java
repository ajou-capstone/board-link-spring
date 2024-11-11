package LinkerBell.campus_market_spring.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import LinkerBell.campus_market_spring.domain.Campus;
import LinkerBell.campus_market_spring.domain.Item;
import LinkerBell.campus_market_spring.domain.Keyword;
import LinkerBell.campus_market_spring.domain.Role;
import LinkerBell.campus_market_spring.domain.User;
import LinkerBell.campus_market_spring.repository.KeywordRepository;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class KeywordServiceTest {

    @InjectMocks
    KeywordService keywordService;

    @Mock
    KeywordRepository keywordRepository;

    List<Keyword> keywords;
    List<Campus> campuses;
    List<User> users;

    @BeforeEach
    void beforeEach() {
        keywords = new ArrayList<>();
        campuses = new ArrayList<>();
        users = new ArrayList<>();

        for (int i = 0; i < 2; i++) {
            Campus campus = Campus.builder()
                .campusId((long) i + 1)
                .universityName("campus" + i)
                .region("수원")
                .email("abc" + i).build();
            campuses.add(campus);
        }

        for (int i = 0; i < 5; i++) {
            User user = User.builder()
                .userId((long) i + 1)
                .nickname("user" + i)
                .campus(i < 1 ? campuses.get(0) : campuses.get(1))
                .role(Role.USER)
                .build();
            users.add(user);
        }

        Keyword keyword1 = Keyword.builder()
            .keywordId(1L)
            .keywordName("camera")
            .user(users.get(0))
            .build();
        Keyword keyword2 = Keyword.builder()
            .keywordId(2L)
            .keywordName("table")
            .user(users.get(0))
            .build();
        Keyword keyword3 = Keyword.builder()
            .keywordId(3L)
            .keywordName("camera")
            .user(users.get(1))
            .build();
        Keyword keyword4 = Keyword.builder()
            .keywordId(4L)
            .keywordName("table")
            .user(users.get(2))
            .build();
        keywords.add(keyword1);
        keywords.add(keyword2);
        keywords.add(keyword3);
        keywords.add(keyword4);
    }

    @Test
    @DisplayName("등록한 아이템과 같은 캠퍼스를 가지며 아이템 제목에 키워드의 입부가 포함되어 있을 때, 테스트")
    public void findKeywordsWithSameItemCampusAndTitleTest() throws Exception {
        //given
        Item savedItem = Item.builder()
            .title("sony camera")
            .user(users.get(0))
            .campus(campuses.get(0))
            .build();
        //when
        when(keywordRepository.findKeywordsWithUserAndCampus()).thenReturn(keywords);

        List<Keyword> targetKeyword = keywordService.findKeywordsWithSameItemCampusAndTitle(
            savedItem);
        //then
        assertThat(targetKeyword.size()).isEqualTo(1);
        assertThat(targetKeyword.get(0).getKeywordName()).isEqualTo("camera");
        assertThat(targetKeyword.get(0).getUser().getUserId()).isEqualTo(users.get(0).getUserId());
    }
}