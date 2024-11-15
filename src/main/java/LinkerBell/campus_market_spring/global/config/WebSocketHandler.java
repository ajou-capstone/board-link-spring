package LinkerBell.campus_market_spring.global.config;

import LinkerBell.campus_market_spring.global.error.ErrorCode;
import LinkerBell.campus_market_spring.global.error.exception.CustomException;
import LinkerBell.campus_market_spring.global.jwt.JwtUtils;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketHandler implements ChannelInterceptor {

    private final JwtUtils jwtUtils;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message,
            StompHeaderAccessor.class);

        if (accessor.getCommand() == StompCommand.CONNECT) {
            Map<String, Object> headerAttribute = accessor.getSessionAttributes();
            String authToken = (String) headerAttribute.get("authorization");
            if (StringUtils.hasText(authToken) && authToken.startsWith("Bearer ")) {
                authToken = authToken.substring(7);
            }

            if (!jwtUtils.validateToken(authToken)) {
                log.error("Connect - Invalid JWT token");
                throw new CustomException(ErrorCode.INVALID_JWT);
            }

            Authentication authentication = jwtUtils.getAuthentication(authToken);

            if (authentication == null) {
                log.error("Connect - authentication is null");
            }
            accessor.setUser(authentication);
        }

        if (accessor.getCommand() == StompCommand.SEND) {
            String destination = accessor.getDestination();
            String username = accessor.getUser().getName();

            log.info("user {} send to destination {}", username, destination);
        }

        if (accessor.getCommand() == StompCommand.SUBSCRIBE) {
            String destination = accessor.getDestination();
            String username = accessor.getUser().getName();

            log.info("user {} subscribed to {}", username, destination);
        }

        if (accessor.getCommand() == StompCommand.UNSUBSCRIBE) {
            String destination = accessor.getDestination();
            String username = accessor.getUser().getName();
            log.info("user {} unsubscribed to {}", username, destination);
        }

        if (accessor.getMessage() != null) {
            log.info("message : {}", accessor.getMessage());
        }

        return message;
    }

    @Override
    public void postSend(Message<?> message, MessageChannel channel, boolean sent) {
        log.info("PostSend -----");
        log.info("Message sent status: {}", sent);

        // 메시지의 Payload를 확인
        Object payload = message.getPayload();
        log.info("postsend - Payload: {}", payload);
        if (payload instanceof byte[]) {
            // 바이트 배열이면 UTF-8로 디코딩
            String decodedPayload = new String((byte[]) payload, StandardCharsets.UTF_8);
            log.info("postsend - Decoded payload (JSON): {}", decodedPayload);
        } else {
            log.info("postsend - Payload: {}", payload);
        }
    }
}
