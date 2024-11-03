package LinkerBell.campus_market_spring.service;

import LinkerBell.campus_market_spring.domain.ChatProperties;
import LinkerBell.campus_market_spring.domain.ChatRoom;
import LinkerBell.campus_market_spring.domain.Item;
import LinkerBell.campus_market_spring.domain.User;
import LinkerBell.campus_market_spring.dto.AuthUserDto;
import LinkerBell.campus_market_spring.dto.ChatRoomDataResponseDto;
import LinkerBell.campus_market_spring.dto.ChatRoomRequestDto;
import LinkerBell.campus_market_spring.dto.ChatRoomResponseDto;
import LinkerBell.campus_market_spring.global.error.ErrorCode;
import LinkerBell.campus_market_spring.global.error.exception.CustomException;
import LinkerBell.campus_market_spring.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ChatPropertiesRepository chatPropertiesRepository;
    private final ChatMessageRepository chatMessageRepository;

    // 채팅방 만들기. 채팅방 설정도 2개 만듦
    @Transactional
    public ChatRoomResponseDto addChatRoom(AuthUserDto user,
        ChatRoomRequestDto chatRoomRequestDto) {
        User buyer = userRepository.findById(user.getUserId())
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        Item item = itemRepository.findById(chatRoomRequestDto.getItemId())
            .orElseThrow(() -> new CustomException(ErrorCode.ITEM_NOT_FOUND));
        User seller = item.getUser();

        ChatRoom chatRoom = ChatRoom.builder().user(buyer).item(item).build();

        chatRoomRepository.save(chatRoom);

        // 채팅방 설정 2개 만들기
        ChatProperties buyerChatProperties = ChatProperties.builder().user(buyer).chatRoom(chatRoom)
            .isAlarm(true).title(seller.getNickname()).isExited(false).build();
        chatPropertiesRepository.save(buyerChatProperties);

        ChatProperties sellerChatProperties = ChatProperties.builder().user(seller)
            .chatRoom(chatRoom).isAlarm(true).title(buyer.getNickname()).isExited(false).build();
        chatPropertiesRepository.save(sellerChatProperties);

        ChatRoomResponseDto chatRoomResponseDto = ChatRoomResponseDto.builder()
            .chatRoomId(chatRoom.getChatRoomId()).userId(chatRoom.getUser().getUserId())
            .itemId(chatRoom.getItem().getItemId()).title(chatRoom.getUser().getNickname())
            .isAlarm(true).build();

        return chatRoomResponseDto;
    }

    // 채팅방 목록 가져오기
    @Transactional(readOnly = true)
    public List<ChatRoomDataResponseDto> getChatRooms(AuthUserDto authUserDto) {
        List<ChatRoomDataResponseDto> chatRoomDataResponseDtoList = new ArrayList<>();
        User user = userRepository.findById(authUserDto.getUserId())
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        chatRoomRepository.findAll().forEach(chatRoom -> {
            // 내가 구매자인 경우
            if (chatRoom.getUser().getUserId().equals(user.getUserId())) {
                ChatRoomDataResponseDto tempChatRoomDataResponseDto = ChatRoomDataResponseDto.builder()
                    .chatRoomId(chatRoom.getChatRoomId())
                    .userId(chatRoom.getItem().getUser().getUserId())
                    .itemId(chatRoom.getItem().getItemId())
                    .title(chatRoom.getItem().getUser().getNickname()) // 판매자의 닉네임이 제목에 보이게
                    .isAlarm(
                        chatPropertiesRepository.findByUserAndChatRoom(user, chatRoom).isAlarm())
                    .messageId(chatMessageRepository.findTopByIsReadTrueOrderByCreatedDateDesc()
                        .getMessageId()).build();

                chatRoomDataResponseDtoList.add(tempChatRoomDataResponseDto);
            } else if (chatRoom.getItem().getUser().getUserId()
                .equals(user.getUserId())) { // 내가 판매자인 경우
                ChatRoomDataResponseDto tempChatRoomDataResponseDto = ChatRoomDataResponseDto.builder()
                    .chatRoomId(chatRoom.getChatRoomId()).userId(chatRoom.getUser().getUserId())
                    .itemId(chatRoom.getItem().getItemId())
                    .title(chatRoom.getUser().getNickname()) // 구매자의 닉네임이 제목에 보이게
                    .isAlarm(
                        chatPropertiesRepository.findByUserAndChatRoom(user, chatRoom).isAlarm())
                    .messageId(chatMessageRepository.findTopByIsReadTrueOrderByCreatedDateDesc()
                        .getMessageId()).build();

                chatRoomDataResponseDtoList.add(tempChatRoomDataResponseDto);
            }
        });

        return chatRoomDataResponseDtoList;
    }

    // 채팅방 나가기
    @Transactional
    public void leaveChatRoom(Long chatRoomId, AuthUserDto authUserDto) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow();
        User user = userRepository.findById(authUserDto.getUserId()).orElseThrow();

        // isExited = true로 바꾸기
        chatPropertiesRepository.updateIsExitedTrueByUserAndChatRoom(user, chatRoom);

        // 채팅방에서 userCount - 1 하기
        chatRoomRepository.decrementUserCountByChatRoomId(chatRoomId);
    }
}
