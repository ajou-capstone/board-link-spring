package LinkerBell.campus_market_spring.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.FetchType.*;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Campus extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long campusId;

    private String universityName;

    private String region;

    private String email;

}
