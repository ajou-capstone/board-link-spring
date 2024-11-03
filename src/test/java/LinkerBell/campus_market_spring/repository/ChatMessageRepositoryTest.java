package LinkerBell.campus_market_spring.repository;

import LinkerBell.campus_market_spring.domain.ChatMessage;
import LinkerBell.campus_market_spring.domain.ChatRoom;
import LinkerBell.campus_market_spring.domain.ContentType;
import LinkerBell.campus_market_spring.domain.User;
import jakarta.persistence.EntityManager;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
class ChatMessageRepositoryTest {

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void findMessageIdsByChatRoomAndRecentDays_ShouldReturnMessagesInLastSevenDays() {
        // given
        ChatRoom chatRoom = chatRoomRepository.save(new ChatRoom());
        User user = userRepository.save(new User());

        // Save default messages
        ChatMessage recentMessage = ChatMessage.builder()
            .chatRoom(chatRoom)
            .user(user)
            .content("Recent message")
            .contentType(ContentType.TEXT)
            .isRead(true)
            .build();

        ChatMessage oldMessage = ChatMessage.builder()
            .chatRoom(chatRoom)
            .user(user)
            .content("Old message")
            .contentType(ContentType.TEXT)
            .isRead(true)
            .build();

        chatMessageRepository.save(recentMessage);
        chatMessageRepository.save(oldMessage);

        // Update createdDate explicitly for testing
        chatMessageRepository.updateCreatedDateByMessageId(recentMessage.getMessageId(),
            LocalDateTime.now().minusDays(1)); // 1 day ago
        chatMessageRepository.updateCreatedDateByMessageId(oldMessage.getMessageId(),
            LocalDateTime.now().minusDays(10)); // 10 days ago

        // when
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        List<Long> messageIds = chatMessageRepository.findMessageIdsByChatRoomAndRecentDays(
            chatRoom, sevenDaysAgo);

        // print for debugging
        System.out.println("seven days ago : " + sevenDaysAgo);
        for (Long id : messageIds) {
            System.out.println(
                "Message ID: " + id + ", Created Date: " + chatMessageRepository.findById(id).get()
                    .getCreatedDate());
        }

        // then
        assertThat(messageIds).containsExactly(
            recentMessage.getMessageId()); // Should only contain recentMessage
        assertThat(messageIds).doesNotContain(
            oldMessage.getMessageId()); // Should not contain oldMessage
    }
}
