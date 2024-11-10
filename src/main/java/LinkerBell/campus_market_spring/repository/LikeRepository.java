package LinkerBell.campus_market_spring.repository;

import LinkerBell.campus_market_spring.domain.Item;
import LinkerBell.campus_market_spring.domain.Like;
import LinkerBell.campus_market_spring.domain.User;
import LinkerBell.campus_market_spring.dto.SliceResponse;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {

    Slice<Like> findAllByUser(User user, Pageable pageable);

    Optional<Like> findByUserAndItem(User user, Item item);
}
