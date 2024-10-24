package LinkerBell.campus_market_spring.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QCampus is a Querydsl query type for Campus
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCampus extends EntityPathBase<Campus> {

    private static final long serialVersionUID = -641000305L;

    public static final QCampus campus = new QCampus("campus");

    public final QBaseEntity _super = new QBaseEntity(this);

    public final NumberPath<Long> campusId = createNumber("campusId", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final StringPath email = createString("email");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> lastModifiedDate = _super.lastModifiedDate;

    public final StringPath region = createString("region");

    public final StringPath universityName = createString("universityName");

    public QCampus(String variable) {
        super(Campus.class, forVariable(variable));
    }

    public QCampus(Path<? extends Campus> path) {
        super(path.getType(), path.getMetadata());
    }

    public QCampus(PathMetadata metadata) {
        super(Campus.class, metadata);
    }

}

