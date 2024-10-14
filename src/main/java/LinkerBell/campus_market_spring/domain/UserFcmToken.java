package LinkerBell.campus_market_spring.domain;

import jakarta.persistence.*;

import static jakarta.persistence.FetchType.LAZY;

@Entity
public class UserFcmToken extends BaseEntity {

    @Id
    @GeneratedValue
    private Long userFcmTokenId;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String fcmToken;
}
