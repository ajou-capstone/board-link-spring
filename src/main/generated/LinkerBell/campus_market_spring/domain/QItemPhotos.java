package LinkerBell.campus_market_spring.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QItemPhotos is a Querydsl query type for ItemPhotos
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QItemPhotos extends EntityPathBase<ItemPhotos> {

    private static final long serialVersionUID = 742583588L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QItemPhotos itemPhotos = new QItemPhotos("itemPhotos");

    public final QBaseEntity _super = new QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final StringPath imageAddress = createString("imageAddress");

    public final QItem item;

    public final NumberPath<Long> itemPhotosId = createNumber("itemPhotosId", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> lastModifiedDate = _super.lastModifiedDate;

    public QItemPhotos(String variable) {
        this(ItemPhotos.class, forVariable(variable), INITS);
    }

    public QItemPhotos(Path<? extends ItemPhotos> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QItemPhotos(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QItemPhotos(PathMetadata metadata, PathInits inits) {
        this(ItemPhotos.class, metadata, inits);
    }

    public QItemPhotos(Class<? extends ItemPhotos> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.item = inits.isInitialized("item") ? new QItem(forProperty("item"), inits.get("item")) : null;
    }

}

