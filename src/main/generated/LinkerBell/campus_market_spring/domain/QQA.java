package LinkerBell.campus_market_spring.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QQA is a Querydsl query type for QA
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QQA extends EntityPathBase<QA> {

    private static final long serialVersionUID = -1321429600L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QQA qA = new QQA("qA");

    public final QBaseEntity _super = new QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final StringPath description = createString("description");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> lastModifiedDate = _super.lastModifiedDate;

    public final NumberPath<Long> qa_id = createNumber("qa_id", Long.class);

    public final QUser user;

    public QQA(String variable) {
        this(QA.class, forVariable(variable), INITS);
    }

    public QQA(Path<? extends QA> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QQA(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QQA(PathMetadata metadata, PathInits inits) {
        this(QA.class, metadata, inits);
    }

    public QQA(Class<? extends QA> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.user = inits.isInitialized("user") ? new QUser(forProperty("user"), inits.get("user")) : null;
    }

}

