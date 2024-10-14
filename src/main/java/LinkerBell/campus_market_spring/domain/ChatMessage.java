package LinkerBell.campus_market_spring.domain;

import jakarta.persistence.*;

import static jakarta.persistence.FetchType.*;

@Entity
public class ChatMessage extends BaseEntity {
    @Id
    @GeneratedValue
    private Long messageId;
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "chat_room_id")
    private ChatRoom chatRoom;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String content;
    @Enumerated(EnumType.STRING)
    private ContentType contentType;

    private boolean isRead;


}
