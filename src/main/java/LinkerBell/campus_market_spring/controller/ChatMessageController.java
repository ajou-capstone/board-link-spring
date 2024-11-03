package LinkerBell.campus_market_spring.controller;

import LinkerBell.campus_market_spring.dto.AuthUserDto;
import LinkerBell.campus_market_spring.dto.ChatMessageResponseDto;
import LinkerBell.campus_market_spring.dto.CollectionResponse.ChatMessageCollectionResponseDto;
import LinkerBell.campus_market_spring.dto.CollectionResponse.RecentChatMessageCollectionResponseDto;
import LinkerBell.campus_market_spring.dto.RecentChatMessageResponseDto;
import LinkerBell.campus_market_spring.global.auth.Login;
import LinkerBell.campus_market_spring.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ChatMessageController {

    private final ChatMessageService chatMessageService;

    // 최근 7일간 메시지 목록 가져오기
    @GetMapping("api/v1/chat/recent-message")
    public ResponseEntity<RecentChatMessageCollectionResponseDto> getRecentMessage(
        @Login AuthUserDto authUserDto) {
        List<RecentChatMessageResponseDto> recentChatMessageResponseDtoList = chatMessageService.getRecentMessageList(
            authUserDto.getUserId());
        return ResponseEntity.ok(
            RecentChatMessageCollectionResponseDto.from(recentChatMessageResponseDtoList));
    }

    // 메시지 읽음 표시하기
    @PatchMapping("api/v1/chat/read-message")
    public ResponseEntity<Void> readMessage(@RequestBody Long messageId) {
        chatMessageService.readMessage(messageId);
        return ResponseEntity.noContent().build();
    }

    // 메시지 내용들 가져오기
    @GetMapping("api/v1/chat/message")
    public ResponseEntity<ChatMessageCollectionResponseDto> getMessageContents(
        @RequestBody List<Long> messageIdList) {
        List<ChatMessageResponseDto> chatMessageResponseDtoList = chatMessageService.getMessageContents(
            messageIdList);
        return ResponseEntity.ok(ChatMessageCollectionResponseDto.from(chatMessageResponseDtoList));
    }
}
