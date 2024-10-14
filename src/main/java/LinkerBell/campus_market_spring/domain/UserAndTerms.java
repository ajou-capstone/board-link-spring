package LinkerBell.campus_market_spring.domain;

import jakarta.persistence.*;

import static jakarta.persistence.FetchType.LAZY;

@Entity
public class UserAndTerms extends BaseEntity {

    @Id
    @GeneratedValue
    private Long userAndTermsId;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "terms_id")
    private Terms terms;

    private boolean isAgree;
}
