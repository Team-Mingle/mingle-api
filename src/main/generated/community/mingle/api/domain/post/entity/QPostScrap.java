package community.mingle.api.domain.post.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPostScrap is a Querydsl query type for PostScrap
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPostScrap extends EntityPathBase<PostScrap> {

    private static final long serialVersionUID = -1483502535L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPostScrap postScrap = new QPostScrap("postScrap");

    public final community.mingle.api.entitybase.QAuditLoggingBase _super = new community.mingle.api.entitybase.QAuditLoggingBase(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final DateTimePath<java.time.LocalDateTime> deletedAt = createDateTime("deletedAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final community.mingle.api.domain.member.entity.QMember member;

    public final QPost post;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QPostScrap(String variable) {
        this(PostScrap.class, forVariable(variable), INITS);
    }

    public QPostScrap(Path<? extends PostScrap> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPostScrap(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPostScrap(PathMetadata metadata, PathInits inits) {
        this(PostScrap.class, metadata, inits);
    }

    public QPostScrap(Class<? extends PostScrap> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.member = inits.isInitialized("member") ? new community.mingle.api.domain.member.entity.QMember(forProperty("member"), inits.get("member")) : null;
        this.post = inits.isInitialized("post") ? new QPost(forProperty("post"), inits.get("post")) : null;
    }

}

