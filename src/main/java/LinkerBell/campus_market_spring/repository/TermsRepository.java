package LinkerBell.campus_market_spring.repository;

import LinkerBell.campus_market_spring.domain.Terms;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TermsRepository extends JpaRepository<Terms, Long> {

}
