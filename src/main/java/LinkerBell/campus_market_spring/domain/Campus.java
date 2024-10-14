package LinkerBell.campus_market_spring.domain;

import jakarta.persistence.*;

import static jakarta.persistence.FetchType.*;

@Entity
public class Campus extends BaseEntity {
    @Id
    @GeneratedValue
    private Long campusId;
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "university_id")
    private University university;

    private String name;

}
