package LinkerBell.campus_market_spring.repository;

import LinkerBell.campus_market_spring.domain.Campus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CampusRepository extends JpaRepository<Campus, Long> {
    List<Campus> findByEmail(String email);
}
