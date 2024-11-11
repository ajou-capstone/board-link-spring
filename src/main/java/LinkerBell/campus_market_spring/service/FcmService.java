package LinkerBell.campus_market_spring.service;

import LinkerBell.campus_market_spring.domain.Item;
import LinkerBell.campus_market_spring.domain.Keyword;
import LinkerBell.campus_market_spring.dto.FcmMessageDto;
import LinkerBell.campus_market_spring.global.error.ErrorCode;
import LinkerBell.campus_market_spring.global.error.exception.CustomException;
import LinkerBell.campus_market_spring.repository.UserFcmTokenRepository;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.MessagingErrorCode;
import com.google.firebase.messaging.Notification;
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

                sendNotification(sendingKeywordMessage);
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

    public void sendNotification(FcmMessageDto fcmMessageDto) {
        Message.Builder messageBuilder = Message.builder()
            .setToken(fcmMessageDto.getTargetToken())
            .setNotification(Notification.builder()
                .setTitle(fcmMessageDto.getTitle())
                .setBody(fcmMessageDto.getBody())
                .build());

        if (fcmMessageDto.getDeeplinkUrl() != null) {
            messageBuilder.putData("deeplink", fcmMessageDto.getDeeplinkUrl());
        }

        Message message = messageBuilder.build();
        try {
            String response = FirebaseMessaging.getInstance().send(message);
            log.debug("Successfully sent message with deeplink: {}", response);
        } catch (FirebaseMessagingException ex) {
            if (ex.getMessagingErrorCode().equals(MessagingErrorCode.INVALID_ARGUMENT)) {
                throw new CustomException(ErrorCode.FCM_INVALID_ARGUMENT);
            } else if (ex.getMessagingErrorCode().equals(MessagingErrorCode.UNREGISTERED)) {
                throw new CustomException(ErrorCode.FCM_UNREGISTERED);
            } else {
                throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
            }
        }

    }
}
