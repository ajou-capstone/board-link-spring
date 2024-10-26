package LinkerBell.campus_market_spring.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QItemReport is a Querydsl query type for ItemReport
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QItemReport extends EntityPathBase<ItemReport> {

    private static final long serialVersionUID = 797096407L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QItemReport itemReport = new QItemReport("itemReport");

    public final QBaseEntity _super = new QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final StringPath description = createString("description");

    public final QItem item;

    public final NumberPath<Long> itemReportId = createNumber("itemReportId", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> lastModifiedDate = _super.lastModifiedDate;

    public final QUser user;

    public QItemReport(String variable) {
        this(ItemReport.class, forVariable(variable), INITS);
    }

    public QItemReport(Path<? extends ItemReport> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QItemReport(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QItemReport(PathMetadata metadata, PathInits inits) {
        this(ItemReport.class, metadata, inits);
    }

    public QItemReport(Class<? extends ItemReport> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.item = inits.isInitialized("item") ? new QItem(forProperty("item"), inits.get("item")) : null;
        this.user = inits.isInitialized("user") ? new QUser(forProperty("user"), inits.get("user")) : null;
    }

}

