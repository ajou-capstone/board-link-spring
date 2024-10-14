package LinkerBell.campus_market_spring.domain;

import jakarta.persistence.*;

import static jakarta.persistence.FetchType.*;

@Entity
public class ChatProperties extends BaseEntity {

    @Id
    @GeneratedValue
    private Long chatPropertiesId;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "chat_room_id")
    private ChatRoom chatRoom;

    private boolean isAlarm;

    private String title;

    private boolean isExited;
}
