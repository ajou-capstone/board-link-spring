package LinkerBell.campus_market_spring.repository;

import LinkerBell.campus_market_spring.domain.QA;
import LinkerBell.campus_market_spring.domain.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface QaRepository extends JpaRepository<QA, Long> {

    @Query(value = "select q from QA q "
        + "where q.isCompleted= :status ")
    Slice<QA> findQAsByStatus(@Param("status") Boolean status, Pageable pageable);

    Slice<QA> findQASByUser(User user, Pageable pageable);

    @Query(value = "select q from QA q ")
    Slice<QA> findQAs(Pageable pageable);
}
