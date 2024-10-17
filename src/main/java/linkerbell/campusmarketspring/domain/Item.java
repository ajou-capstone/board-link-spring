package linkerbell.campusmarketspring.domain;

import jakarta.persistence.*;

import static jakarta.persistence.FetchType.LAZY;

@Entity
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

    private String title;

    private String description;

    private int price;
    @Enumerated(EnumType.STRING)
    private Category category;

    private ItemStatus itemStatus;

    private String thumbnail;

    private boolean isDeleted;

}
