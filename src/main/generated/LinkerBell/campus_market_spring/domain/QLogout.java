package LinkerBell.campus_market_spring.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QLogout is a Querydsl query type for Logout
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QLogout extends EntityPathBase<Logout> {

    private static final long serialVersionUID = -370588358L;

    public static final QLogout logout = new QLogout("logout");

    public final QBaseEntity _super = new QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> lastModifiedDate = _super.lastModifiedDate;

    public final NumberPath<Long> logoutId = createNumber("logoutId", Long.class);

    public final StringPath refreshToken = createString("refreshToken");

    public QLogout(String variable) {
        super(Logout.class, forVariable(variable));
    }

    public QLogout(Path<? extends Logout> path) {
        super(path.getType(), path.getMetadata());
    }

    public QLogout(PathMetadata metadata) {
        super(Logout.class, metadata);
    }

}

