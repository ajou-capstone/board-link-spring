package LinkerBell.campus_market_spring.repository;

import LinkerBell.campus_market_spring.domain.ItemPhotos;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemPhotosRepository extends JpaRepository<ItemPhotos, Long> {

    List<ItemPhotos> findByItem_itemId(Long itemId);
}
