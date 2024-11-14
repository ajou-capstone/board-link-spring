package LinkerBell.campus_market_spring.controller;

import LinkerBell.campus_market_spring.dto.ChattingRequestDto;
import LinkerBell.campus_market_spring.dto.ChattingResponseDto;
import LinkerBell.campus_market_spring.global.error.ErrorCode;
import LinkerBell.campus_market_spring.global.error.exception.CustomException;
import LinkerBell.campus_market_spring.service.ChattingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;

@Slf4j
@RequiredArgsConstructor
@Controller
public class ChattingController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChattingService chattingService;

    @MessageMapping("/chat/{chatRoomId}")
    public void sendMessage(SimpMessageHeaderAccessor accessor,
        @DestinationVariable Long chatRoomId,
        ChattingRequestDto chattingRequestDto) {

        log.info("start sendMessage...");

        if (accessor.getUser() == null) {
            log.error("chatting controller : user is null");
            throw new CustomException(ErrorCode.JWT_IS_NULL);
        }

        Long userId = Long.valueOf(((UserDetails) accessor.getUser()).getUsername());

        log.info("userId = " + userId + "chatRoomId = " + chatRoomId);

        ChattingResponseDto chattingResponseDto = chattingService.makeChattingResponseDto(
            userId, chatRoomId, chattingRequestDto);

        messagingTemplate.convertAndSend("/sub/chat/" + chatRoomId, chattingResponseDto);

        log.info("userId {} send to chatRoomId {} : {}", userId, chatRoomId,
            chattingResponseDto.getContent());
    }
}
