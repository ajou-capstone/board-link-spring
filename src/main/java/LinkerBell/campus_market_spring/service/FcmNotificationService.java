package LinkerBell.campus_market_spring.service;

import LinkerBell.campus_market_spring.dto.FcmMessageDto;
import LinkerBell.campus_market_spring.global.error.ErrorCode;
import LinkerBell.campus_market_spring.global.error.exception.CustomException;
import LinkerBell.campus_market_spring.repository.UserFcmTokenRepository;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.MessagingErrorCode;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class FcmNotificationService {

    private final UserFcmTokenRepository userFcmTokenRepository;

    @Async
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
                userFcmTokenRepository.deleteByFcmToken(fcmMessageDto.getTargetToken());
                throw new CustomException(ErrorCode.FCM_INVALID_ARGUMENT);
            } else if (ex.getMessagingErrorCode().equals(MessagingErrorCode.UNREGISTERED)) {
                userFcmTokenRepository.deleteByFcmToken(fcmMessageDto.getTargetToken());
                throw new CustomException(ErrorCode.FCM_UNREGISTERED);
            } else {
                throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
            }
        }

    }
}
