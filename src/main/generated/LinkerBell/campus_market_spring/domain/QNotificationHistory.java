package LinkerBell.campus_market_spring.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QNotificationHistory is a Querydsl query type for NotificationHistory
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QNotificationHistory extends EntityPathBase<NotificationHistory> {

    private static final long serialVersionUID = -166268039L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QNotificationHistory notificationHistory = new QNotificationHistory("notificationHistory");

    public final QBaseEntity _super = new QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final StringPath deeplink = createString("deeplink");

    public final StringPath description = createString("description");

    public final QItem item;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> lastModifiedDate = _super.lastModifiedDate;

    public final NumberPath<Long> notificationHistoryId = createNumber("notificationHistoryId", Long.class);

    public final StringPath title = createString("title");

    public final QUser user;

    public QNotificationHistory(String variable) {
        this(NotificationHistory.class, forVariable(variable), INITS);
    }

    public QNotificationHistory(Path<? extends NotificationHistory> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QNotificationHistory(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QNotificationHistory(PathMetadata metadata, PathInits inits) {
        this(NotificationHistory.class, metadata, inits);
    }

    public QNotificationHistory(Class<? extends NotificationHistory> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.item = inits.isInitialized("item") ? new QItem(forProperty("item"), inits.get("item")) : null;
        this.user = inits.isInitialized("user") ? new QUser(forProperty("user"), inits.get("user")) : null;
    }

}

