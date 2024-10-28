package LinkerBell.campus_market_spring.domain;

import jakarta.persistence.*;
import lombok.*;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Table(name = "Users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;
    @ManyToOne (fetch = LAZY)
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

    private boolean isDeleted = false;

}
