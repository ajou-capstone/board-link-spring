package LinkerBell.campus_market_spring.repository;

import LinkerBell.campus_market_spring.domain.ChatRoom;
import LinkerBell.campus_market_spring.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    List<ChatRoom> findByUser(User user);

    boolean existsByUser_userIdAndItem_itemId(Long userId, Long itemId);

    @Query("SELECT c FROM ChatRoom c WHERE c.user = :user OR c.item.user = :user")
    List<ChatRoom> findByUserOrItemSeller(@Param("user") User user);
}
