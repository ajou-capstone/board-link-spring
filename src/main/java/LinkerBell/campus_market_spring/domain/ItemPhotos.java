package LinkerBell.campus_market_spring.domain;

import jakarta.persistence.*;

import static jakarta.persistence.FetchType.*;

@Entity
public class ItemPhotos extends BaseEntity {
    @Id
    @GeneratedValue
    private Long itemPhotosId;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    private String imageAddress;
}
