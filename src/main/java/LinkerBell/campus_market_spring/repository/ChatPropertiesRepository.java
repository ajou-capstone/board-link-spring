package LinkerBell.campus_market_spring.repository;

import LinkerBell.campus_market_spring.domain.ChatProperties;
import LinkerBell.campus_market_spring.domain.ChatRoom;
import LinkerBell.campus_market_spring.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatPropertiesRepository extends JpaRepository<ChatProperties, Long> {
    ChatProperties findByUserAndChatRoom(User user, ChatRoom chatRoom);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE ChatProperties cp SET cp.isExited = true WHERE cp.user = :user AND cp.chatRoom = :chatRoom")
    void updateIsExitedTrueByUserAndChatRoom(@Param("user") User user, @Param("chatRoom") ChatRoom chatRoom);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE ChatProperties cp SET cp.isAlarm = :isAlarm WHERE cp.user = :user AND cp.chatRoom = :chatRoom")
    void updateIsAlarmByUserAndChatRoom(@Param("user") User user, @Param("chatRoom") ChatRoom chatRoom, @Param("isAlarm") boolean isAlarm);
}
