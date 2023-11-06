package community.mingle.api.domain.like.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QContentLike is a Querydsl query type for ContentLike
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QContentLike extends EntityPathBase<ContentLike> {

    private static final long serialVersionUID = 533344897L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QContentLike contentLike = new QContentLike("contentLike");

    public final community.mingle.api.entitybase.QAuditLoggingBase _super = new community.mingle.api.entitybase.QAuditLoggingBase(this);

    public final EnumPath<community.mingle.api.enums.ContentType> contentType = createEnum("contentType", community.mingle.api.enums.ContentType.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final DateTimePath<java.time.LocalDateTime> deletedAt = createDateTime("deletedAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final community.mingle.api.domain.member.entity.QMember member;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QContentLike(String variable) {
        this(ContentLike.class, forVariable(variable), INITS);
    }

    public QContentLike(Path<? extends ContentLike> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QContentLike(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QContentLike(PathMetadata metadata, PathInits inits) {
        this(ContentLike.class, metadata, inits);
    }

    public QContentLike(Class<? extends ContentLike> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.member = inits.isInitialized("member") ? new community.mingle.api.domain.member.entity.QMember(forProperty("member"), inits.get("member")) : null;
    }

}

