package LinkerBell.campus_market_spring.repository;

import LinkerBell.campus_market_spring.domain.ChatRoom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@Transactional
public class ChatRoomRepositoryTest {

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    private ChatRoom chatRoom;

    @BeforeEach
    void setUp() {
        // 채팅방 초기 설정
        chatRoom = new ChatRoom();
        chatRoom.setUserCount(2); // 초기 userCount 설정
        chatRoomRepository.save(chatRoom);
    }

    @Test
    void decrementUserCountByChatRoomId_Success() {
        // when: userCount가 감소될 때
        chatRoomRepository.decrementUserCountByChatRoomId(chatRoom.getChatRoomId());

        // then: userCount가 1로 감소했는지 확인
        ChatRoom updatedChatRoom = chatRoomRepository.findById(chatRoom.getChatRoomId()).orElseThrow();
        assertEquals(1, updatedChatRoom.getUserCount());
    }

    @Test
    void decrementUserCountByChatRoomId_UserCountZero() {
        // given: userCount가 0인 경우
        chatRoom.setUserCount(0);
        chatRoomRepository.save(chatRoom);

        // when: userCount가 0이므로 감소되지 않아야 함
        chatRoomRepository.decrementUserCountByChatRoomId(chatRoom.getChatRoomId());

        // then: userCount가 그대로 0인지 확인
        ChatRoom updatedChatRoom = chatRoomRepository.findById(chatRoom.getChatRoomId()).orElseThrow();
        assertEquals(0, updatedChatRoom.getUserCount());
    }
}

