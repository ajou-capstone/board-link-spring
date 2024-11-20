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
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "campus_id")
    private Campus campus;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_buyer_id", nullable = true)
    private User userBuyer;
    @Column(length = 100)
    private String title;
    @Column(length = 2000)
    private String description;
    private int price;
    @Enumerated(EnumType.STRING)
    private Category category;

    @Builder.Default
    @Column(length = 50)
    private ItemStatus itemStatus = ItemStatus.FORSALE;
    @Column(length = 1050)
    private String thumbnail;

    @Builder.Default
    private boolean isDeleted = false;

}
