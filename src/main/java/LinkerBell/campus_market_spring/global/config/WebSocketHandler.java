package LinkerBell.campus_market_spring.global.config;

import LinkerBell.campus_market_spring.global.error.ErrorCode;
import LinkerBell.campus_market_spring.global.error.exception.CustomException;
import LinkerBell.campus_market_spring.global.jwt.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

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
            String authToken = accessor.getFirstNativeHeader("Authorization");

            if (authToken == null) {
                log.info("authToken is null");
                throw new CustomException(ErrorCode.JWT_IS_NULL);
            } else if (!jwtUtils.validateToken(authToken)) {
                log.info("authToken is not valid");
                throw new CustomException(ErrorCode.INVALID_JWT);
            }

            Authentication authentication = jwtUtils.getAuthentication(authToken);
            accessor.setUser(authentication);
            log.info("connected authentication name : {}", authentication.getName());
        }

        if (accessor.getCommand() == StompCommand.SEND) {
            log.info("destination : {}", accessor.getDestination());
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
