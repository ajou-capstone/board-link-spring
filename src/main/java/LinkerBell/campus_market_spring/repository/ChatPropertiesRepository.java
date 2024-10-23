package LinkerBell.campus_market_spring.repository;

import LinkerBell.campus_market_spring.domain.ChatProperties;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatPropertiesRepository extends JpaRepository<ChatProperties, Long> {
}
