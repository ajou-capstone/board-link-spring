package LinkerBell.campus_market_spring.repository;

import LinkerBell.campus_market_spring.domain.Keyword;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface KeywordRepository extends JpaRepository<Keyword, Long> {

    @Query("select distinct k from Keyword k " +
        "join fetch k.user u " +
        "join fetch u.campus")
    List<Keyword> findKeywordsWithUserAndCampus();
}
