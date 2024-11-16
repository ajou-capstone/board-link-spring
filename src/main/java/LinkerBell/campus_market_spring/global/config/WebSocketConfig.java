package LinkerBell.campus_market_spring.global.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
@EnableWebSocketMessageBroker
@Configuration
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final WebSocketHandler webSocketHandler;

    private final HttpHandshakeInterceptor httpHandshakeInterceptor;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws/chat").setAllowedOrigins("*")
            .addInterceptors(httpHandshakeInterceptor);

    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/sub");
        registry.setApplicationDestinationPrefixes("/send", "/sub");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(webSocketHandler);
    }

    @Override
    public void configureClientOutboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public void postSend(Message<?> message, MessageChannel channel, boolean sent) {
                // Access headers using SimpMessageHeaderAccessor
                SimpMessageHeaderAccessor accessor = SimpMessageHeaderAccessor.wrap(message);

                // Log when a CONNECT_ACK is detected (if required)
                if (accessor.getMessageType() == SimpMessageType.CONNECT_ACK) {
                    log.info("CONNECTED event detected. Session ID: {}", accessor.getSessionId());
                }

                // Log information for SEND type messages
                if (accessor.getMessageType() == SimpMessageType.MESSAGE) {
                    String destination = accessor.getDestination();
                    String sessionId = accessor.getSessionId();
                    Object payload = message.getPayload();

                    // Attempt to get the user information (if authenticated)
                    String username =
                        accessor.getUser() != null ? accessor.getUser().getName() : "Unknown";

                    log.info(
                        "SEND event: User [{}], Session ID [{}], Destination [{}], Payload [{}]",
                        username, sessionId, destination, payload);
                }
            }
        });
    }

}
