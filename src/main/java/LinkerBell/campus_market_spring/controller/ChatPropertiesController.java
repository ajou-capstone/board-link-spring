package LinkerBell.campus_market_spring.controller;

import LinkerBell.campus_market_spring.dto.AuthUserDto;
import LinkerBell.campus_market_spring.dto.ChatAlarmRequestDto;
import LinkerBell.campus_market_spring.global.auth.Login;
import LinkerBell.campus_market_spring.service.ChatPropertiesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ChatPropertiesController {

    private final ChatPropertiesService chatPropertiesService;

    // 채팅방 알람 설정하기
    @PatchMapping("api/v1/chat/{chatRoomId}/alarm")
    public ResponseEntity<Void> patchAlarm(@PathVariable("chatRoomId") Long chatRoomId,
        @RequestBody ChatAlarmRequestDto chatAlarmRequestDto, @Login AuthUserDto authUserDto) {
        chatPropertiesService.patchAlarm(chatRoomId, chatAlarmRequestDto.getIsAlarm(),
            authUserDto.getUserId());
        return ResponseEntity.noContent().build();
    }
}
