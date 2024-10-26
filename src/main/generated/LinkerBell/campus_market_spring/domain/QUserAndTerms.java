package LinkerBell.campus_market_spring.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUserAndTerms is a Querydsl query type for UserAndTerms
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUserAndTerms extends EntityPathBase<UserAndTerms> {

    private static final long serialVersionUID = -425169269L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QUserAndTerms userAndTerms = new QUserAndTerms("userAndTerms");

    public final QBaseEntity _super = new QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final BooleanPath isAgree = createBoolean("isAgree");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> lastModifiedDate = _super.lastModifiedDate;

    public final QTerms terms;

    public final QUser user;

    public final NumberPath<Long> userAndTermsId = createNumber("userAndTermsId", Long.class);

    public QUserAndTerms(String variable) {
        this(UserAndTerms.class, forVariable(variable), INITS);
    }

    public QUserAndTerms(Path<? extends UserAndTerms> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QUserAndTerms(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QUserAndTerms(PathMetadata metadata, PathInits inits) {
        this(UserAndTerms.class, metadata, inits);
    }

    public QUserAndTerms(Class<? extends UserAndTerms> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.terms = inits.isInitialized("terms") ? new QTerms(forProperty("terms")) : null;
        this.user = inits.isInitialized("user") ? new QUser(forProperty("user"), inits.get("user")) : null;
    }

}

