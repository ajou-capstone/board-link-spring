package LinkerBell.campus_market_spring.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class Logout extends BaseEntity {

    @Id
    @GeneratedValue
    private Long logoutId;

    private String refreshToken;
}
