package LinkerBell.campus_market_spring.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QCampus is a Querydsl query type for Campus
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCampus extends EntityPathBase<Campus> {

    private static final long serialVersionUID = -641000305L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QCampus campus = new QCampus("campus");

    public final QBaseEntity _super = new QBaseEntity(this);

    public final NumberPath<Long> campusId = createNumber("campusId", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> lastModifiedDate = _super.lastModifiedDate;

    public final StringPath name = createString("name");

    public final QUniversity university;

    public QCampus(String variable) {
        this(Campus.class, forVariable(variable), INITS);
    }

    public QCampus(Path<? extends Campus> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QCampus(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QCampus(PathMetadata metadata, PathInits inits) {
        this(Campus.class, metadata, inits);
    }

    public QCampus(Class<? extends Campus> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.university = inits.isInitialized("university") ? new QUniversity(forProperty("university")) : null;
    }

}

