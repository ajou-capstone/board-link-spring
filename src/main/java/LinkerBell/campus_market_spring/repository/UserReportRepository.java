package LinkerBell.campus_market_spring.repository;

import LinkerBell.campus_market_spring.domain.ItemReport;
import LinkerBell.campus_market_spring.domain.UserReport;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserReportRepository extends JpaRepository<UserReport, Long> {

    @Query(value = "select ur from UserReport ur ")
    Slice<UserReport> findUserReports(Pageable pageable);

    @Query(value = "select ur from UserReport ur "
        + "where ur.isCompleted= :status ")
    Slice<UserReport> findUserReportsByStatus(@Param("status") Boolean status, Pageable pageable);
}
