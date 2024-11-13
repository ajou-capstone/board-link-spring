package LinkerBell.campus_market_spring.global.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class WebSocketHandler implements ChannelInterceptor {

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        log.info("accessor command : {}", accessor.getCommand().name());

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
