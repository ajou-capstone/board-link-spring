package LinkerBell.campus_market_spring.repository;

import LinkerBell.campus_market_spring.domain.ItemReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemReportRepository extends JpaRepository<ItemReport, Long> {

}
