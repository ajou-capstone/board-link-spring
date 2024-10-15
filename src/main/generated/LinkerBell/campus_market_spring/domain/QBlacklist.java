package LinkerBell.campus_market_spring.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QBlacklist is a Querydsl query type for Blacklist
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QBlacklist extends EntityPathBase<Blacklist> {

    private static final long serialVersionUID = 741383021L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QBlacklist blacklist = new QBlacklist("blacklist");

    public final QBaseEntity _super = new QBaseEntity(this);

    public final NumberPath<Long> blacklistId = createNumber("blacklistId", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final DatePath<java.time.LocalDate> endDate = createDate("endDate", java.time.LocalDate.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> lastModifiedDate = _super.lastModifiedDate;

    public final StringPath reason = createString("reason");

    public final QUser user;

    public QBlacklist(String variable) {
        this(Blacklist.class, forVariable(variable), INITS);
    }

    public QBlacklist(Path<? extends Blacklist> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QBlacklist(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QBlacklist(PathMetadata metadata, PathInits inits) {
        this(Blacklist.class, metadata, inits);
    }

    public QBlacklist(Class<? extends Blacklist> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.user = inits.isInitialized("user") ? new QUser(forProperty("user"), inits.get("user")) : null;
    }

}

