package LinkerBell.campus_market_spring.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QChatMessage is a Querydsl query type for ChatMessage
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QChatMessage extends EntityPathBase<ChatMessage> {

    private static final long serialVersionUID = 1305096159L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QChatMessage chatMessage = new QChatMessage("chatMessage");

    public final QBaseEntity _super = new QBaseEntity(this);

    public final QChatRoom chatRoom;

    public final StringPath content = createString("content");

    public final EnumPath<ContentType> contentType = createEnum("contentType", ContentType.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final BooleanPath isRead = createBoolean("isRead");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> lastModifiedDate = _super.lastModifiedDate;

    public final NumberPath<Long> messageId = createNumber("messageId", Long.class);

    public final QUser user;

    public QChatMessage(String variable) {
        this(ChatMessage.class, forVariable(variable), INITS);
    }

    public QChatMessage(Path<? extends ChatMessage> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QChatMessage(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QChatMessage(PathMetadata metadata, PathInits inits) {
        this(ChatMessage.class, metadata, inits);
    }

    public QChatMessage(Class<? extends ChatMessage> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.chatRoom = inits.isInitialized("chatRoom") ? new QChatRoom(forProperty("chatRoom"), inits.get("chatRoom")) : null;
        this.user = inits.isInitialized("user") ? new QUser(forProperty("user"), inits.get("user")) : null;
    }

}

