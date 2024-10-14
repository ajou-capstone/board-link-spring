package LinkerBell.campus_market_spring.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class University extends BaseEntity {

    @Id
    @GeneratedValue
    private Long universityId;

    private String name;

    private String email;
}
