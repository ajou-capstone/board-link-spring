package LinkerBell.campus_market_spring.domain;

import static jakarta.persistence.FetchType.LAZY;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class NotificationHistory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notificationHistoryId;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;
    @Column(nullable = false, length = 100)
    private String title;
    @Column(nullable = false, length = 200)
    private String description;
    @Column(nullable = false, length = 200)
    private String deeplink;
}

