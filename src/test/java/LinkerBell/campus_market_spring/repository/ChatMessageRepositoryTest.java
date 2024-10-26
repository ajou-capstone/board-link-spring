package LinkerBell.campus_market_spring.repository;

import LinkerBell.campus_market_spring.domain.ChatMessage;
import LinkerBell.campus_market_spring.domain.ChatRoom;
import LinkerBell.campus_market_spring.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
//@AutoConfigureTestDatabase(replace = Replace.NONE) // 실제 DB 사용 시 추가
class ChatMessageRepositoryTest {

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("findLastReadMessage()로 가장 최근의 읽음 처리된 메시지를 조회")
    void findLastReadMessage_ShouldReturnMostRecentReadMessage() {
        // given
        // 1. 테스트용 User와 ChatRoom 생성 및 저장
        User user = new User();
        user.setNickname("testUser");
        userRepository.save(user);

        ChatRoom chatRoom = ChatRoom.builder()
                .user(user)
                .build();
        chatRoomRepository.save(chatRoom);

        // 2. 여러 개의 ChatMessage 객체 생성 및 저장
        ChatMessage message1 = ChatMessage.builder()
                .chatRoom(chatRoom)
                .user(user)
                .content("First message")
                .isRead(true)
                .build();
        message1.setCreatedDate(LocalDateTime.now().minusMinutes(10)); // 10분 전 생성
        chatMessageRepository.save(message1);

        ChatMessage message2 = ChatMessage.builder()
                .chatRoom(chatRoom)
                .user(user)
                .content("Second message")
                .isRead(false)
                .build();
        message2.setCreatedDate(LocalDateTime.now().minusMinutes(5)); // 5분 전 생성
        chatMessageRepository.save(message2);

        ChatMessage message3 = ChatMessage.builder()
                .chatRoom(chatRoom)
                .user(user)
                .content("Most recent read message")
                .isRead(true)
                .build();
        message3.setCreatedDate(LocalDateTime.now()); // 가장 최근에 생성
        chatMessageRepository.save(message3);

        // when
        ChatMessage lastReadMessage = chatMessageRepository.findTopByIsReadTrueOrderByCreatedDateDesc();

        // then
        assertThat(lastReadMessage).isNotNull(); // 결과가 null이 아닌지 확인
        assertThat(lastReadMessage.getContent()).isEqualTo("Most recent read message"); // 내용이 예상과 일치하는지 확인
        assertThat(lastReadMessage.getCreatedDate()).isEqualTo(message3.getCreatedDate()); // 생성 날짜 확인
        assertThat(lastReadMessage.isRead()).isTrue(); // isRead가 true인지 확인
    }
}

