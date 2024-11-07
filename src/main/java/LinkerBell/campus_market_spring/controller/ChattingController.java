package LinkerBell.campus_market_spring.controller;

import LinkerBell.campus_market_spring.dto.AuthUserDto;
import LinkerBell.campus_market_spring.dto.ChattingRequestDto;
import LinkerBell.campus_market_spring.dto.ChattingResponseDto;
import LinkerBell.campus_market_spring.global.auth.Login;
import LinkerBell.campus_market_spring.service.ChattingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Slf4j
@RequiredArgsConstructor
@Controller
public class ChattingController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChattingService chattingService;

    @MessageMapping("/chat/{chatRoomId}")
    public void sendMessage(@Login AuthUserDto authUserDto, @DestinationVariable Long chatRoomId,
        ChattingRequestDto chattingRequestDto) {

        ChattingResponseDto chattingResponseDto = chattingService.makeChattingResponseDto(
            authUserDto.getUserId(), chatRoomId, chattingRequestDto);

        messagingTemplate.convertAndSend("/send/chat/" + chatRoomId, chattingResponseDto);
    }
}
