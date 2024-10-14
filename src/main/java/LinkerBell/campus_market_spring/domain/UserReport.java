package LinkerBell.campus_market_spring.domain;

import jakarta.persistence.*;

import static jakarta.persistence.FetchType.LAZY;

@Entity
public class UserReport extends BaseEntity {

    @Id
    @GeneratedValue
    private Long userReportId;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "target_id")
    private User target;

    private String description;
}
