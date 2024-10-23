package LinkerBell.campus_market_spring.service;

import LinkerBell.campus_market_spring.domain.ChatProperties;
import LinkerBell.campus_market_spring.domain.ChatRoom;
import LinkerBell.campus_market_spring.domain.Item;
import LinkerBell.campus_market_spring.domain.User;
import LinkerBell.campus_market_spring.dto.ChatRoomRequestDto;
import LinkerBell.campus_market_spring.dto.ChatRoomResponseDto;
import LinkerBell.campus_market_spring.global.error.ErrorCode;
import LinkerBell.campus_market_spring.global.error.exception.CustomException;
import LinkerBell.campus_market_spring.repository.ChatPropertiesRepository;
import LinkerBell.campus_market_spring.repository.ChatRoomRepository;
import LinkerBell.campus_market_spring.repository.ItemRepository;
import LinkerBell.campus_market_spring.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatRoomServiceTest {

    @Mock
    private ChatRoomRepository chatRoomRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private ChatPropertiesRepository chatPropertiesRepository;

    @InjectMocks
    private ChatRoomService chatRoomService;

    @Test
    void addChatRoom_Success() {
        // given
        Long userId = 1L;
        Long itemId = 2L;
        String buyerNickname = "buyerNick";
        String sellerNickname = "sellerNick";

        User buyer = new User();
        buyer.setUserId(userId);
        buyer.setNickname(buyerNickname);

        User seller = new User();
        seller.setNickname(sellerNickname);

        Item item = new Item();
        item.setUser(seller);

        ChatRoomRequestDto requestDto = new ChatRoomRequestDto(userId, itemId);

        // Mocking
        when(userRepository.findById(userId)).thenReturn(Optional.of(buyer));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(chatRoomRepository.save(any(ChatRoom.class))).thenAnswer(invocation -> {
            ChatRoom chatRoom = invocation.getArgument(0);
            chatRoom.setChatRoomId(100L);
            return chatRoom;
        });

        // when
        ChatRoomResponseDto responseDto = chatRoomService.addChatRoom(requestDto);

        // then
        assertNotNull(responseDto);
        assertEquals(100L, responseDto.getChatRoomId());

        // repository 호출이 예상대로 되었는지 확인
        verify(userRepository, times(1)).findById(userId);
        verify(itemRepository, times(1)).findById(itemId);
        verify(chatRoomRepository, times(1)).save(any(ChatRoom.class));
        verify(chatPropertiesRepository, times(2)).save(any(ChatProperties.class)); // buyer와 seller의 ChatProperties가 각각 저장되는지 확인
    }

    @Test
    void addChatRoom_UserNotFound() {
        // given
        Long userId = 1L;
        Long itemId = 2L;
        ChatRoomRequestDto requestDto = new ChatRoomRequestDto(userId, itemId);

        // Mocking: 사용자가 없을 때 Optional.empty() 반환
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // when & then: CustomException이 USER_NOT_FOUND와 함께 발생하는지 테스트
        CustomException exception = assertThrows(CustomException.class, () -> {
            chatRoomService.addChatRoom(requestDto);
        });

        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());

        // userRepository는 호출되지만, 나머지는 호출되지 않아야 함
        verify(userRepository, times(1)).findById(userId);
        verify(itemRepository, never()).findById(any());
        verify(chatRoomRepository, never()).save(any());
        verify(chatPropertiesRepository, never()).save(any());
    }

    @Test
    void addChatRoom_ItemNotFound() {
        // given
        Long userId = 1L;
        Long itemId = 2L;
        User buyer = new User();
        buyer.setUserId(userId);

        ChatRoomRequestDto requestDto = new ChatRoomRequestDto(userId, itemId);

        // Mocking: User는 찾았지만 Item은 없을 때 Optional.empty() 반환
        when(userRepository.findById(userId)).thenReturn(Optional.of(buyer));
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        // when & then: CustomException이 ITEM_NOT_FOUND와 함께 발생하는지 테스트
        CustomException exception = assertThrows(CustomException.class, () -> {
            chatRoomService.addChatRoom(requestDto);
        });

        assertEquals(ErrorCode.ITEM_NOT_FOUND, exception.getErrorCode());

        // userRepository와 itemRepository는 호출되지만, 나머지는 호출되지 않아야 함
        verify(userRepository, times(1)).findById(userId);
        verify(itemRepository, times(1)).findById(itemId);
        verify(chatRoomRepository, never()).save(any());
        verify(chatPropertiesRepository, never()).save(any());
    }
}