package LinkerBell.campus_market_spring.domain;

import static jakarta.persistence.FetchType.LAZY;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;

@Entity
public class QA extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long qaId;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @Column(nullable = false, length = 1000)
    private String description;
    @Column(length = 1000)
    private String answerDescription;
    private LocalDateTime answerDate;
    @Column(nullable = false, length = 100)
    private String title;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QaCategory category;
    @Column(nullable = false)
    private boolean isCompleted;
}
