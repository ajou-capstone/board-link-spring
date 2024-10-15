package LinkerBell.campus_market_spring.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QUniversity is a Querydsl query type for University
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUniversity extends EntityPathBase<University> {

    private static final long serialVersionUID = -971324866L;

    public static final QUniversity university = new QUniversity("university");

    public final QBaseEntity _super = new QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final StringPath email = createString("email");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> lastModifiedDate = _super.lastModifiedDate;

    public final StringPath name = createString("name");

    public final NumberPath<Long> universityId = createNumber("universityId", Long.class);

    public QUniversity(String variable) {
        super(University.class, forVariable(variable));
    }

    public QUniversity(Path<? extends University> path) {
        super(path.getType(), path.getMetadata());
    }

    public QUniversity(PathMetadata metadata) {
        super(University.class, metadata);
    }

}

