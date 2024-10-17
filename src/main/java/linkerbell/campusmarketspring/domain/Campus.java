package linkerbell.campusmarketspring.domain;

import jakarta.persistence.*;

@Entity
public class Campus extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long campusId;

    private String universityName;

    private String region;

    private String email;

}
