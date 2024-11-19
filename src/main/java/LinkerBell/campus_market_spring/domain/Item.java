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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Item extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long itemId;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "campus_id", nullable = false)
    private Campus campus;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_buyer_id", nullable = true)
    private User userBuyer;
    @Column(nullable = false, length = 100)
    private String title;
    @Column(nullable = false, length = 2000)
    private String description;
    @Column(nullable = false)
    private int price;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;

    @Builder.Default
    @Column(nullable = false, length = 50)
    private ItemStatus itemStatus = ItemStatus.FORSALE;
    @Column(nullable = false, length = 1050)
    private String thumbnail;

    @Builder.Default
    @Column(nullable = false)
    private boolean isDeleted = false;

}
