package LinkerBell.campus_market_spring.controller;

import LinkerBell.campus_market_spring.dto.AuthUserDto;
import LinkerBell.campus_market_spring.dto.ChatRoomDataResponseDto;
import LinkerBell.campus_market_spring.dto.ChatRoomRequestDto;
import LinkerBell.campus_market_spring.dto.ChatRoomResponseDto;
import LinkerBell.campus_market_spring.dto.CollectionResponse.ChatRoomDataCollectionResponseDto;
import LinkerBell.campus_market_spring.global.auth.Login;
import LinkerBell.campus_market_spring.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    // 채팅방 만들기
    @PostMapping("api/v1/chat")
    public ResponseEntity<ChatRoomResponseDto> addChatRoom(
        @RequestBody ChatRoomRequestDto chatRoomRequestDto, @Login AuthUserDto user) {
        ChatRoomResponseDto chatRoomResponseDto = chatRoomService.addChatRoom(user,
            chatRoomRequestDto);
        return ResponseEntity.ok(chatRoomResponseDto);
    }

    // 채팅방 1개 정보 가져오기
    @GetMapping("api/v1/chat/room/{chatRoomId}")
    public ResponseEntity<ChatRoomDataResponseDto> getChatRoom(@Login AuthUserDto authUserDto,
        @PathVariable Long chatRoomId) {
        ChatRoomDataResponseDto chatRoomDataResponseDto = chatRoomService.getChatRoom(
            authUserDto.getUserId(), chatRoomId);

        return ResponseEntity.ok(chatRoomDataResponseDto);
    }

    // 채팅방 목록 가져오기
    @GetMapping("api/v1/chat/rooms")
    public ResponseEntity<ChatRoomDataCollectionResponseDto> getChatRooms(@Login AuthUserDto user) {
        List<ChatRoomDataResponseDto> chatRoomDataResponseDtoList = chatRoomService.getChatRooms(
            user);
        return ResponseEntity.ok(
            ChatRoomDataCollectionResponseDto.from(chatRoomDataResponseDtoList));
    }


    // 채팅방 나가기
    @PatchMapping("api/v1/chat/{chatRoomId}")
    public ResponseEntity<Void> leaveChatRoom(@PathVariable Long chatRoomId,
        @Login AuthUserDto user) {
        chatRoomService.leaveChatRoom(chatRoomId, user);
        return ResponseEntity.noContent().build();
    }
}
