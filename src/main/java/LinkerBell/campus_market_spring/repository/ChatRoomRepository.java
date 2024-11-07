package LinkerBell.campus_market_spring.repository;

import LinkerBell.campus_market_spring.domain.ChatRoom;
import LinkerBell.campus_market_spring.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    List<ChatRoom> findByUser(User user);
}
