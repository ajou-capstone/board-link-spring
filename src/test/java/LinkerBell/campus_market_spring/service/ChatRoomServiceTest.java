package LinkerBell.campus_market_spring.service;

import LinkerBell.campus_market_spring.domain.*;
import LinkerBell.campus_market_spring.dto.AuthUserDto;
import LinkerBell.campus_market_spring.dto.ChatRoomDataResponseDto;
import LinkerBell.campus_market_spring.dto.ChatRoomRequestDto;
import LinkerBell.campus_market_spring.dto.ChatRoomResponseDto;
import LinkerBell.campus_market_spring.global.error.ErrorCode;
import LinkerBell.campus_market_spring.global.error.exception.CustomException;
import LinkerBell.campus_market_spring.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatRoomServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ChatRoomRepository chatRoomRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private ChatPropertiesRepository chatPropertiesRepository;

    @Mock
    private ChatMessageRepository chatMessageRepository;

    @InjectMocks
    private ChatRoomService chatRoomService;

    private User buyer;
    private User seller;
    private Item item;
    private ChatRoom chatRoom;
    private ChatProperties chatProperties;
    private ChatMessage chatMessage;
    private AuthUserDto authUserDto;
    private ChatRoomRequestDto requestDto;

    @BeforeEach
    void setUp() {
        buyer = new User();
        buyer.setUserId(1L);
        buyer.setNickname("buyerNick");

        seller = new User();
        seller.setUserId(2L);
        seller.setNickname("sellerNick");

        item = new Item();
        item.setItemId(1L);
        item.setUser(seller);

        chatRoom = new ChatRoom();
        chatRoom.setChatRoomId(1L);
        chatRoom.setUser(buyer);
        chatRoom.setItem(item);

        chatProperties = new ChatProperties();
        chatProperties.setAlarm(true);

        chatMessage = new ChatMessage();
        chatMessage.setMessageId(100L);

        authUserDto = new AuthUserDto(buyer.getUserId(), "loginEmail");
        requestDto = new ChatRoomRequestDto(buyer.getUserId(), item.getItemId());
    }

    @Test
    @DisplayName("채팅방 생성 성공 테스트")
    void addChatRoom_Success() {
        // given
        when(userRepository.findById(buyer.getUserId())).thenReturn(Optional.of(buyer));
        when(itemRepository.findById(item.getItemId())).thenReturn(Optional.of(item));
        when(chatRoomRepository.save(any(ChatRoom.class))).thenAnswer(invocation -> {
            ChatRoom chatRoom = invocation.getArgument(0);
            chatRoom.setChatRoomId(100L); // 가상의 ID 할당
            return chatRoom;
        });

        // when
        ChatRoomResponseDto responseDto = chatRoomService.addChatRoom(authUserDto, requestDto);

        // then
        assertNotNull(responseDto);
        assertEquals(100L, responseDto.getChatRoomId());

        // Mock 객체 검증
        verify(userRepository, times(1)).findById(buyer.getUserId());
        verify(itemRepository, times(1)).findById(item.getItemId());
        verify(chatRoomRepository, times(1)).save(any(ChatRoom.class));
        verify(chatPropertiesRepository, times(2)).save(any(ChatProperties.class));
    }

    @Test
    @DisplayName("사용자가 존재하지 않을 때 예외 발생")
    void addChatRoom_UserNotFound() {
        // given
        when(userRepository.findById(buyer.getUserId())).thenReturn(Optional.empty());

        // when & then
        CustomException exception = assertThrows(CustomException.class, () -> {
            chatRoomService.addChatRoom(authUserDto, requestDto);
        });
        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());

        verify(userRepository, times(1)).findById(buyer.getUserId());
        verifyNoMoreInteractions(chatRoomRepository, itemRepository, chatPropertiesRepository);
    }

    @Test
    @DisplayName("아이템이 존재하지 않을 때 예외 발생")
    void addChatRoom_ItemNotFound() {
        // given
        when(userRepository.findById(buyer.getUserId())).thenReturn(Optional.of(buyer));
        when(itemRepository.findById(item.getItemId())).thenReturn(Optional.empty());

        // when & then
        CustomException exception = assertThrows(CustomException.class, () -> {
            chatRoomService.addChatRoom(authUserDto, requestDto);
        });
        assertEquals(ErrorCode.ITEM_NOT_FOUND, exception.getErrorCode());

        verify(userRepository, times(1)).findById(buyer.getUserId());
        verify(itemRepository, times(1)).findById(item.getItemId());
        verifyNoMoreInteractions(chatRoomRepository, chatPropertiesRepository);
    }

    @Test
    @DisplayName("사용자가 참여한 채팅방 목록 조회 테스트")
    void getChatRooms_UserIsBuyer() {
        // given
        when(userRepository.findById(buyer.getUserId())).thenReturn(Optional.of(buyer));
        when(chatRoomRepository.findAll()).thenReturn(List.of(chatRoom));
        when(chatPropertiesRepository.findByUserAndChatRoom(buyer, chatRoom)).thenReturn(
            chatProperties);
        when(chatMessageRepository.findTopByIsReadTrueOrderByCreatedDateDesc()).thenReturn(
            chatMessage);

        // when
        List<ChatRoomDataResponseDto> chatRooms = chatRoomService.getChatRooms(authUserDto);

        // then
        assertThat(chatRooms).hasSize(1);
        ChatRoomDataResponseDto responseDto = chatRooms.get(0);
        assertEquals(chatRoom.getChatRoomId(), responseDto.getChatRoomId());
        assertEquals(seller.getUserId(), responseDto.getUserId());
        assertEquals(item.getItemId(), responseDto.getItemId());
        assertEquals(seller.getNickname(), responseDto.getTitle());
        assertEquals(chatProperties.isAlarm(), responseDto.getIsAlarm());
        assertEquals(chatMessage.getMessageId(), responseDto.getMessageId());
    }

    @Test
    @DisplayName("사용자가 판매자인 경우 채팅방 목록 조회 테스트")
    void getChatRooms_UserIsSeller() {
        // given
        AuthUserDto sellerAuthUserDto = new AuthUserDto(seller.getUserId(), "loginEmail"
        ); // 판매자로 설정
        when(userRepository.findById(seller.getUserId())).thenReturn(Optional.of(seller));
        when(chatRoomRepository.findAll()).thenReturn(List.of(chatRoom));
        when(chatPropertiesRepository.findByUserAndChatRoom(seller, chatRoom)).thenReturn(
            chatProperties);
        when(chatMessageRepository.findTopByIsReadTrueOrderByCreatedDateDesc()).thenReturn(
            chatMessage);

        // when
        List<ChatRoomDataResponseDto> chatRooms = chatRoomService.getChatRooms(sellerAuthUserDto);

        // then
        assertThat(chatRooms).hasSize(1);
        ChatRoomDataResponseDto responseDto = chatRooms.get(0);
        assertEquals(chatRoom.getChatRoomId(), responseDto.getChatRoomId());
        assertEquals(buyer.getUserId(), responseDto.getUserId());
        assertEquals(item.getItemId(), responseDto.getItemId());
        assertEquals(buyer.getNickname(), responseDto.getTitle());
        assertEquals(chatProperties.isAlarm(), responseDto.getIsAlarm());
        assertEquals(chatMessage.getMessageId(), responseDto.getMessageId());
    }

}
