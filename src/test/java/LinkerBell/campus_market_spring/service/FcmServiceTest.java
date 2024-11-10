package LinkerBell.campus_market_spring.service;

import LinkerBell.campus_market_spring.domain.Item;
import LinkerBell.campus_market_spring.domain.Keyword;
import LinkerBell.campus_market_spring.domain.User;
import LinkerBell.campus_market_spring.domain.UserFcmToken;
import LinkerBell.campus_market_spring.dto.FcmMessageDto;
import LinkerBell.campus_market_spring.repository.KeywordRepository;
import LinkerBell.campus_market_spring.repository.UserFcmTokenRepository;
import LinkerBell.campus_market_spring.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class FcmServiceTest {

    @Autowired
    UserFcmTokenRepository userFcmTokenRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    KeywordRepository keywordRepository;

    @Autowired
    FcmService fcmService;


    @Value("${my.fcm_token}")
    String targetTokens;
    String title;
    String body;
    String deeplinkUrl;

    List<Keyword> keywords;

    @BeforeEach
    void beforeEach() {
        keywords = new ArrayList<>();

        title = "바바바 보보보 진짜 이게 title?";
        body = "이게 바로 body 내용이다!";
        //deeplinkUrl = "www.google.com";
        User user = User.builder()
            .nickname("test").build();

        userRepository.save(user);

        UserFcmToken userFcmToken = UserFcmToken.builder()
            .fcmToken(targetTokens)
            .user(user)
            .build();

        userFcmTokenRepository.save(userFcmToken);

        Keyword keyword = Keyword.builder()
            .keywordName("camera")
            .user(user)
            .build();

        keywordRepository.save(keyword);
        keywords.add(keyword);
    }

    @AfterEach
    void afterEach() {
        userRepository.deleteAll();
        userFcmTokenRepository.deleteAll();
        keywordRepository.deleteAll();
    }

    @Test
    @DisplayName("테스트 알림 보내는 테스트")
    public void testFcmSendTest() throws Exception {
        FcmMessageDto message = FcmMessageDto.builder()
            .targetToken(targetTokens)
            .title(title)
            .body(body)
            .deeplinkUrl(deeplinkUrl)
            .build();
        fcmService.sendNotification(message);
    }

    @Test
    @DisplayName("키워드 알림 보내기 테스트")
    public void sendFcmMessageWithKeywordsTest() throws Exception {
        //given
        Item savedItem = Item.builder()
            .itemId(1L)
            .title("sony camera")
            .build();
        //when

        //then
        fcmService.sendFcmMessageWithKeywords(keywords, savedItem);
    }

}