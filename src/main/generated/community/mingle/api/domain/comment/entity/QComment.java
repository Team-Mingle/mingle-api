package community.mingle.api.domain.comment.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QComment is a Querydsl query type for Comment
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QComment extends EntityPathBase<Comment> {

    private static final long serialVersionUID = 1923102014L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QComment comment = new QComment("comment");

    public final community.mingle.api.entitybase.QAuditLoggingBase _super = new community.mingle.api.entitybase.QAuditLoggingBase(this);

    public final BooleanPath anonymous = createBoolean("anonymous");

    public final StringPath content = createString("content");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final DateTimePath<java.time.LocalDateTime> deletedAt = createDateTime("deletedAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final community.mingle.api.domain.member.entity.QMember member;

    public final NumberPath<Long> mentionId = createNumber("mentionId", Long.class);

    public final NumberPath<Long> parentCommentId = createNumber("parentCommentId", Long.class);

    public final community.mingle.api.domain.post.entity.QPost post;

    public final EnumPath<community.mingle.api.enums.ContentStatusType> statusType = createEnum("statusType", community.mingle.api.enums.ContentStatusType.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QComment(String variable) {
        this(Comment.class, forVariable(variable), INITS);
    }

    public QComment(Path<? extends Comment> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QComment(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QComment(PathMetadata metadata, PathInits inits) {
        this(Comment.class, metadata, inits);
    }

    public QComment(Class<? extends Comment> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.member = inits.isInitialized("member") ? new community.mingle.api.domain.member.entity.QMember(forProperty("member"), inits.get("member")) : null;
        this.post = inits.isInitialized("post") ? new community.mingle.api.domain.post.entity.QPost(forProperty("post"), inits.get("post")) : null;
    }

}

