package LinkerBell.campus_market_spring.global.config;

import LinkerBell.campus_market_spring.global.error.ErrorCode;
import LinkerBell.campus_market_spring.global.error.exception.CustomException;
import LinkerBell.campus_market_spring.global.jwt.JwtUtils;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketHandler implements ChannelInterceptor {

    private final JwtUtils jwtUtils;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        log.info("accessor command : {}", accessor.getCommand().name());

        if (accessor.getCommand() == StompCommand.CONNECT) {
            Map<String, Object> headerAttribute = accessor.getSessionAttributes();
            String authToken = (String) headerAttribute.get("authorization");
            if (StringUtils.hasText(authToken) && authToken.startsWith("Bearer ")) {
                authToken = authToken.substring(7);
            }

            if (!jwtUtils.validateToken(authToken)) {
                log.error("Invalid JWT token");
                throw new CustomException(ErrorCode.INVALID_JWT);
            }

            Authentication authentication = jwtUtils.getAuthentication(authToken);

            if (authentication == null) {
                log.error("authentication is null");
            }
            accessor.setUser(authentication);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.info("connected authentication name : {}", authentication.getName());
        }

        if (accessor.getCommand() == StompCommand.SEND) {
            log.info("send");
        }

        if (accessor.getCommand() == StompCommand.SUBSCRIBE) {
            log.info("subscribed");
        }

        if (accessor.getMessage() != null) {
            log.info("message : {}", accessor.getMessage());
        }

        return message;
    }
}