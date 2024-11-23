package LinkerBell.campus_market_spring.service;

import static LinkerBell.campus_market_spring.global.error.ErrorCode.DUPLICATE_KEYWORD_NAME;
import static LinkerBell.campus_market_spring.global.error.ErrorCode.INVALID_KEYWORD_COUNT;
import static LinkerBell.campus_market_spring.global.error.ErrorCode.INVALID_KEYWORD_ID;
import static LinkerBell.campus_market_spring.global.error.ErrorCode.NOT_MATCH_USER_AND_KEYWORD_USER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import LinkerBell.campus_market_spring.domain.Campus;
import LinkerBell.campus_market_spring.domain.Item;
import LinkerBell.campus_market_spring.domain.Keyword;
import LinkerBell.campus_market_spring.domain.Role;
import LinkerBell.campus_market_spring.domain.User;
import LinkerBell.campus_market_spring.dto.KeywordRegisterRequestDto;
import LinkerBell.campus_market_spring.dto.KeywordRegisterResponseDto;
import LinkerBell.campus_market_spring.dto.KeywordResponseDto;
import LinkerBell.campus_market_spring.global.error.exception.CustomException;
import LinkerBell.campus_market_spring.repository.KeywordRepository;
import LinkerBell.campus_market_spring.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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

    @Mock
    UserRepository userRepository;

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

    @Test
    @DisplayName("사용자가 새로운 키워드를 성공적으로 추가할 수 있는지 테스트")
    public void addKeywordTest() {
        // given
        Long userId = users.get(0).getUserId();
        KeywordRegisterRequestDto requestDto = KeywordRegisterRequestDto.builder()
            .keywordName("newKeyword")
            .build();
        List<Keyword> savedKeywords = new ArrayList<>(keywords.subList(0, 2));

        Keyword keywordToSave = Keyword.builder()
            .keywordId((long) keywords.size())
            .user(users.get(0))
            .keywordName(requestDto.getKeywordName())
            .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(users.get(0)));
        when(keywordRepository.findKeywordByUser_UserId(userId)).thenReturn(savedKeywords);
        when(keywordRepository.save(any(Keyword.class))).thenReturn(keywordToSave);

        // when
        KeywordRegisterResponseDto response = keywordService.addKeyword(userId, requestDto);

        // then
        assertThat(response.getKeywordId()).isEqualTo(keywordToSave.getKeywordId());
    }

    @Test
    @DisplayName("사용자가 이미 등록한 키워드를 추가하려고 할 때 예외 발생")
    public void addDuplicateKeywordTest() {
        // given
        Long userId = users.get(0).getUserId();
        KeywordRegisterRequestDto requestDto = KeywordRegisterRequestDto.builder()
            .keywordName("camera")
            .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(users.get(0)));
        when(keywordRepository.findKeywordByUser_UserId(userId)).thenReturn(keywords.subList(0, 2));

        // when, then
        assertThatThrownBy(() -> keywordService.addKeyword(userId, requestDto))
            .isInstanceOf(CustomException.class)
            .hasMessageContaining(DUPLICATE_KEYWORD_NAME.getMessage());
    }

    @Test
    @DisplayName("사용자가 키워드 최대 등록 개수 제한(20개)을 넘어서 키워드를 추가하려고 할 때 예외 발생")
    public void addOverCountKeywordTest() {
        // given
        Long userId = users.get(0).getUserId();
        List<Keyword> overKeyword = new ArrayList<>();
        KeywordRegisterRequestDto requestDto = KeywordRegisterRequestDto.builder()
            .keywordName("newKeywords")
            .build();

        for (int i = 0; i < 20; i++) {
            Keyword testKeyword = Keyword.builder()
                .keywordId((long) i + 1)
                .keywordName("testKeyword" + i)
                .user(users.get(0))
                .build();
            overKeyword.add(testKeyword);
        }

        when(userRepository.findById(userId)).thenReturn(Optional.of(users.get(0)));
        when(keywordRepository.findKeywordByUser_UserId(userId)).thenReturn(overKeyword);

        // when, then
        assertThatThrownBy(() -> keywordService.addKeyword(userId, requestDto))
            .isInstanceOf(CustomException.class)
            .hasMessageContaining(INVALID_KEYWORD_COUNT.getMessage());
    }


    @Test
    @DisplayName("사용자의 모든 키워드를 성공적으로 조회할 수 있는지 테스트")
    public void getKeywordsTest() {
        // given
        Long userId = users.get(0).getUserId();
        List<Keyword> userKeywords = new ArrayList<>(keywords.subList(0, 2));

        when(userRepository.findById(userId)).thenReturn(Optional.of(users.get(0)));
        when(keywordRepository.findKeywordByUser_UserId(userId)).thenReturn(userKeywords);

        // when
        List<KeywordResponseDto> response = keywordService.getKeywords(userId);

        // then
        assertThat(response.size()).isEqualTo(userKeywords.size());
        assertThat(response.get(0).getKeywordName()).isEqualTo(
            userKeywords.get(0).getKeywordName());
        assertThat(response.get(1).getKeywordName()).isEqualTo(
            userKeywords.get(1).getKeywordName());
    }

    @Test
    @DisplayName("사용자가 특정 키워드를 삭제할 수 있는지 테스트")
    public void deleteKeywordTest() {
        // given
        Long userId = users.get(0).getUserId();
        Long keywordId = keywords.get(0).getKeywordId();
        Keyword keywordToDelete = keywords.get(0);

        when(userRepository.findById(userId)).thenReturn(Optional.of(users.get(0)));
        when(keywordRepository.findById(keywordId)).thenReturn(Optional.of(keywordToDelete));

        // when
        keywordService.deleteKeyword(userId, keywordId);

        // then
        verify(keywordRepository).delete(keywordToDelete);
    }

    @Test
    @DisplayName("존재하지 않는 키워드를 삭제하려고 할 때 예외 발생")
    public void deleteInvalidKeywordTest() {
        // given
        Long userId = users.get(0).getUserId();
        Long keywordId = 999L;

        when(userRepository.findById(userId)).thenReturn(Optional.of(users.get(0)));
        when(keywordRepository.findById(keywordId)).thenReturn(Optional.empty());

        // when, then
        assertThatThrownBy(() -> keywordService.deleteKeyword(userId, keywordId))
            .isInstanceOf(CustomException.class)
            .hasMessageContaining(INVALID_KEYWORD_ID.getMessage());
    }

    @Test
    @DisplayName("키워드 작성자가 아닌 유저가 키워드를 삭제하려고 할 때 예외 발생")
    public void deleteNotOwnerUserToKeywordTest() {
        // given
        Long userId = users.get(1).getUserId();
        Long keywordId = keywords.get(0).getKeywordId();

        when(userRepository.findById(userId)).thenReturn(Optional.of(users.get(1)));
        when(keywordRepository.findById(keywordId)).thenReturn(Optional.of(keywords.get(0)));

        // when, then
        assertThatThrownBy(() -> keywordService.deleteKeyword(userId, keywordId))
            .isInstanceOf(CustomException.class)
            .hasMessageContaining(NOT_MATCH_USER_AND_KEYWORD_USER.getMessage());
    }


}