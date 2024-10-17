package LinkerBell.campus_market_spring.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.Type;

import java.util.List;
import java.util.Map;

import static jakarta.persistence.FetchType.*;

@Entity
@Table(name = "Users")
@Builder
@Getter
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;
    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "campus_id")
    private Campus campus;

    private String loginEmail;

    private String schoolEmail;

    private String nickname;

    private String profileImage;

    private double rating;

    private String refreshToken;
    @Enumerated(EnumType.STRING)
    private Role role;
    @Lob
    @Column(columnDefinition = "json")
    private String timetable;

    private boolean idDeleted;

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

}
