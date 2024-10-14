package LinkerBell.campus_market_spring.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class Terms extends BaseEntity {

    @Id
    @GeneratedValue
    private Long termsId;

    private String title;

    private String description;

    private boolean isRequired;
}
