package LinkerBell.campus_market_spring.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QChatProperties is a Querydsl query type for ChatProperties
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QChatProperties extends EntityPathBase<ChatProperties> {

    private static final long serialVersionUID = -1454494117L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QChatProperties chatProperties = new QChatProperties("chatProperties");

    public final QBaseEntity _super = new QBaseEntity(this);

    public final NumberPath<Long> chatPropertiesId = createNumber("chatPropertiesId", Long.class);

    public final QChatRoom chatRoom;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final BooleanPath isAlarm = createBoolean("isAlarm");

    public final BooleanPath isExited = createBoolean("isExited");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> lastModifiedDate = _super.lastModifiedDate;

    public final StringPath title = createString("title");

    public final QUser user;

    public QChatProperties(String variable) {
        this(ChatProperties.class, forVariable(variable), INITS);
    }

    public QChatProperties(Path<? extends ChatProperties> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QChatProperties(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QChatProperties(PathMetadata metadata, PathInits inits) {
        this(ChatProperties.class, metadata, inits);
    }

    public QChatProperties(Class<? extends ChatProperties> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.chatRoom = inits.isInitialized("chatRoom") ? new QChatRoom(forProperty("chatRoom"), inits.get("chatRoom")) : null;
        this.user = inits.isInitialized("user") ? new QUser(forProperty("user"), inits.get("user")) : null;
    }

}

