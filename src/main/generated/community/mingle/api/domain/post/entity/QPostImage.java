package community.mingle.api.domain.post.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPostImage is a Querydsl query type for PostImage
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPostImage extends EntityPathBase<PostImage> {

    private static final long serialVersionUID = -1492455997L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPostImage postImage = new QPostImage("postImage");

    public final community.mingle.api.entitybase.QAuditLoggingBase _super = new community.mingle.api.entitybase.QAuditLoggingBase(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QPost post;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public final StringPath url = createString("url");

    public QPostImage(String variable) {
        this(PostImage.class, forVariable(variable), INITS);
    }

    public QPostImage(Path<? extends PostImage> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPostImage(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPostImage(PathMetadata metadata, PathInits inits) {
        this(PostImage.class, metadata, inits);
    }

    public QPostImage(Class<? extends PostImage> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.post = inits.isInitialized("post") ? new QPost(forProperty("post"), inits.get("post")) : null;
    }

}

