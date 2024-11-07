package LinkerBell.campus_market_spring.repository;

import LinkerBell.campus_market_spring.domain.ChatMessage;
import LinkerBell.campus_market_spring.domain.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    ChatMessage findTopByIsReadTrueOrderByCreatedDateDesc();

    @Query("SELECT cm.messageId FROM ChatMessage cm WHERE cm.chatRoom = :chatRoom AND cm.createdDate >= :sevenDaysAgo ORDER BY cm.createdDate DESC")
    List<Long> findMessageIdsByChatRoomAndRecentDays(@Param("chatRoom") ChatRoom chatRoom,
        @Param("sevenDaysAgo") LocalDateTime sevenDaysAgo);
}

