package LinkerBell.campus_market_spring.repository;

import LinkerBell.campus_market_spring.domain.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    ChatMessage findTopByIsReadTrueOrderByCreatedDateDesc();
}
