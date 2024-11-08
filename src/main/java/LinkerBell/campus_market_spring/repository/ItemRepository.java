package LinkerBell.campus_market_spring.repository;

import LinkerBell.campus_market_spring.domain.Item;
import java.time.LocalDateTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long>, ItemRepositoryCustom {

    @Modifying
    @Query("update Item i set i.createdDate = :createdDate where i.itemId = :itemId")
    void updateCreatedDate(@Param("itemId") Long itemId,
        @Param("createdDate") LocalDateTime createdDate);
}
