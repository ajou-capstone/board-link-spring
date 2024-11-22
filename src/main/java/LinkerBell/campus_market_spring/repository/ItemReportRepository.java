package LinkerBell.campus_market_spring.repository;

import LinkerBell.campus_market_spring.domain.ItemReport;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemReportRepository extends JpaRepository<ItemReport, Long> {

    @Query(value = "select ir from ItemReport ir "
    + "where ir.isCompleted=false ")
    Slice<ItemReport> findItemReports(Pageable pageable);
}
