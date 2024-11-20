package LinkerBell.campus_market_spring.domain;

import static jakarta.persistence.FetchType.LAZY;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "campus_id")
    private Campus campus;
    @Column(length = 100)
    private String loginEmail;
    @Column(length = 100)
    private String schoolEmail;
    @Column(length = 20, unique = true)
    private String nickname;
    @Column(length = 1050)
    private String profileImage;

    private double rating;
    @Column(length = 600)
    private String refreshToken;
    @Enumerated(EnumType.STRING)
    private Role role;
    @Lob
    @Column(columnDefinition = "json")
    private String timetable;

    @Builder.Default
    private boolean isDeleted = false;

    public void setTimetableFromJson(Object timetableObject) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            this.timetable = objectMapper.writeValueAsString(timetableObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
