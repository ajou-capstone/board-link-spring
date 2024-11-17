package LinkerBell.campus_market_spring.service;

import LinkerBell.campus_market_spring.domain.Item;
import LinkerBell.campus_market_spring.domain.Keyword;
import LinkerBell.campus_market_spring.dto.FcmMessageDto;
import LinkerBell.campus_market_spring.repository.UserFcmTokenRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class FcmService {

    private final UserFcmTokenRepository userFcmTokenRepository;
    private final FcmNotificationService fcmNotificationService;

    @Value("${deeplink.keyword_url}")
    private String deeplinkKeywordUrl;

    public void sendFcmMessageWithKeywords(List<Keyword> sendingKeywords, Item savedItem) {
        for (Keyword sendingKeyword : sendingKeywords) {
            List<String> fcmTokens = userFcmTokenRepository.findFcmTokenByUser_UserId(
                sendingKeyword.getUser().getUserId());

            for (String fcmToken : fcmTokens) {
                FcmMessageDto sendingKeywordMessage = createKeywordFcmMessage(sendingKeyword,
                    fcmToken,
                    savedItem);

                fcmNotificationService.sendNotification(sendingKeywordMessage);
            }

        }
    }

    private FcmMessageDto createKeywordFcmMessage(Keyword sendingKeyword, String fcmToken,
        Item savedItem) {
        return FcmMessageDto.builder()
            .targetToken(fcmToken)
            .title("키워드 알람 알림")
            .body("등록하신 " + sendingKeyword.getKeywordName() + " 키워드에 대한 아이템이 등록되었어요.")
            .deeplinkUrl(deeplinkKeywordUrl + savedItem.getItemId())
            .build();
    }

}
