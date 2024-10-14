package LinkerBell.campus_market_spring.domain;

import jakarta.persistence.*;

import java.time.LocalDate;

import static jakarta.persistence.FetchType.*;

@Entity
public class Blacklist extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long blacklistId;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String reason;

    private LocalDate endDate;
}
