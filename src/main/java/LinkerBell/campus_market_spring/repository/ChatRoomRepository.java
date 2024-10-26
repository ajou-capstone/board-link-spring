package LinkerBell.campus_market_spring.repository;

import LinkerBell.campus_market_spring.domain.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    @Modifying(clearAutomatically = true)
    @Query("UPDATE ChatRoom c SET c.userCount = c.userCount - 1 WHERE c.chatRoomId = :chatRoomId AND c.userCount > 0")
    void decrementUserCountByChatRoomId(@Param("chatRoomId") Long chatRoomId);
}
