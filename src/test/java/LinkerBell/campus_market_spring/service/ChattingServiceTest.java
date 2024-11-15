package LinkerBell.campus_market_spring.service;

import LinkerBell.campus_market_spring.domain.ChatMessage;
import LinkerBell.campus_market_spring.domain.ChatRoom;
import LinkerBell.campus_market_spring.domain.ContentType;
import LinkerBell.campus_market_spring.domain.User;
import LinkerBell.campus_market_spring.dto.ChattingRequestDto;
import LinkerBell.campus_market_spring.dto.ChattingResponseDto;

import LinkerBell.campus_market_spring.global.error.ErrorCode;
import LinkerBell.campus_market_spring.global.error.exception.CustomException;
import LinkerBell.campus_market_spring.repository.ChatMessageRepository;
import LinkerBell.campus_market_spring.repository.ChatRoomRepository;
import LinkerBell.campus_market_spring.repository.UserRepository;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ChattingServiceTest {

    @InjectMocks
    private ChattingService chattingService;

    @Mock
    private ChatMessageRepository chatMessageRepository;

    @Mock
    private ChatRoomRepository chatRoomRepository;

    @Mock
    private UserRepository userRepository;

    private User user;
    private ChatRoom chatRoom;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // User 및 ChatRoom 객체 초기화
        user = new User();
        user.setUserId(1L);

        chatRoom = new ChatRoom();
        chatRoom.setChatRoomId(1L);
    }

    @Test
    void makeChattingResponseDto_ShouldSaveMessageAndReturnDto() {
        // Given
        Long userId = user.getUserId();
        Long chatRoomId = chatRoom.getChatRoomId();

        ChattingRequestDto chattingRequestDto = ChattingRequestDto.builder()
            .content("Hello World")
            .contentType(ContentType.TEXT)
            .build();

        // Mocking repository responses
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(chatRoomRepository.findById(chatRoomId)).thenReturn(Optional.of(chatRoom));

        // Prepare the ChatMessage without setting createdDate
        ChatMessage chatMessage = ChatMessage.builder()
            .content(chattingRequestDto.getContent())
            .contentType(chattingRequestDto.getContentType())
            .isRead(false)
            .chatRoom(chatRoom) // chatRoom 추가
            .user(user) // user 추가
            .build();

        // Prepare the savedMessage to return
        ChatMessage savedMessage = ChatMessage.builder()
            .messageId(1L) // Assume an ID is assigned
            .content(chatMessage.getContent())
            .contentType(chatMessage.getContentType())
            .isRead(false)
            .chatRoom(chatRoom)
            .user(user)
            .build();

        // Here we mock the save operation, but we should let JPA handle createdDate
        when(chatMessageRepository.save(any(ChatMessage.class))).thenAnswer(invocation -> {
            ChatMessage msg = invocation.getArgument(0);
            msg.setMessageId(1L); // Setting a mock ID
            msg.setCreatedDate(LocalDateTime.now()); // Simulating createdDate setting
            return msg;
        });

        // When
        ChattingResponseDto responseDto = chattingService.makeChattingResponseDto(userId,
            chatRoomId, chattingRequestDto);

        // Then
        assertThat(responseDto).isNotNull();
        assertThat(responseDto.getMessageId()).isEqualTo(savedMessage.getMessageId());
        assertThat(responseDto.getChatRoomId()).isEqualTo(chatRoomId);
        assertThat(responseDto.getUserId()).isEqualTo(userId);
        assertThat(responseDto.getContent()).isEqualTo("Hello World");
        assertThat(responseDto.getContentType()).isEqualTo(chattingRequestDto.getContentType());
        assertThat(responseDto.getCreatedAt()).isNotNull();

        // Verify interactions with repositories
        verify(userRepository).findById(userId);
        verify(chatRoomRepository).findById(chatRoomId);
        verify(chatMessageRepository).save(any(ChatMessage.class)); // Mocked save call verification
    }


    @Test
    void makeChattingResponseDto_ShouldThrowUserNotFoundException() {
        // Given
        Long userId = 1L;  // 존재하지 않는 사용자 ID
        Long chatRoomId = chatRoom.getChatRoomId();
        ChattingRequestDto chattingRequestDto = ChattingRequestDto.builder()
            .content("Hello World")
            .contentType(ContentType.TEXT)
            .build();

        // Mocking repository responses
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        CustomException thrown = org.junit.jupiter.api.Assertions.assertThrows(
            CustomException.class, () -> {
                chattingService.makeChattingResponseDto(userId, chatRoomId, chattingRequestDto);
            });
        assertThat(thrown.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND);
    }

    @Test
    void makeChattingResponseDto_ShouldThrowChatRoomNotFoundException() {
        // Given
        Long userId = user.getUserId();
        Long chatRoomId = 1L;  // 존재하지 않는 채팅방 ID
        ChattingRequestDto chattingRequestDto = ChattingRequestDto.builder()
            .content("Hello World")
            .contentType(ContentType.TEXT)
            .build();

        // Mocking repository responses
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(chatRoomRepository.findById(chatRoomId)).thenReturn(Optional.empty());

        // When & Then
        CustomException thrown = org.junit.jupiter.api.Assertions.assertThrows(
            CustomException.class, () -> {
                chattingService.makeChattingResponseDto(userId, chatRoomId, chattingRequestDto);
            });
        assertThat(thrown.getErrorCode()).isEqualTo(ErrorCode.CHATROOM_NOT_FOUND);
    }
}
