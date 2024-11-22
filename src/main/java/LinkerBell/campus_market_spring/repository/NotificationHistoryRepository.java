package LinkerBell.campus_market_spring.repository;

import LinkerBell.campus_market_spring.domain.NotificationHistory;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NotificationHistoryRepository extends JpaRepository<NotificationHistory, Long> {

    Slice<NotificationHistory> findByUser_UserId(Long userId, Pageable pageable);

    @Query("select nh from NotificationHistory nh " +
        "join fetch nh.user " +
        "join fetch nh.item " +
        "where nh.notificationHistoryId = :notificationHistoryId")
    Optional<NotificationHistory> findByIdWithUserAndItem(
        @Param("notificationHistoryId") Long notificationHistoryId);

    @Modifying
    @Query("delete from NotificationHistory nh where nh.user.userId = :userId")
    int deleteByUserId(@Param("userId") Long userId);
}
