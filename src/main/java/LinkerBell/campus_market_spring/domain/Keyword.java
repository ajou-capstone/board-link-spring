package LinkerBell.campus_market_spring.domain;

import jakarta.persistence.*;

import static jakarta.persistence.FetchType.*;

@Entity
public class Keyword extends BaseEntity {

    @Id
    @GeneratedValue
    private Long keywordId;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String keywordName;
}
