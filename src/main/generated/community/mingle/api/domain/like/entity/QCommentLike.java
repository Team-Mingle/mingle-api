package community.mingle.api.domain.like.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QCommentLike is a Querydsl query type for CommentLike
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCommentLike extends EntityPathBase<CommentLike> {

    private static final long serialVersionUID = -1268938393L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QCommentLike commentLike = new QCommentLike("commentLike");

    public final QContentLike _super;

    public final community.mingle.api.domain.comment.entity.QComment comment;

    //inherited
    public final EnumPath<community.mingle.api.enums.ContentType> contentType;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> deletedAt;

    //inherited
    public final NumberPath<Long> id;

    // inherited
    public final community.mingle.api.domain.member.entity.QMember member;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt;

    public QCommentLike(String variable) {
        this(CommentLike.class, forVariable(variable), INITS);
    }

    public QCommentLike(Path<? extends CommentLike> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QCommentLike(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QCommentLike(PathMetadata metadata, PathInits inits) {
        this(CommentLike.class, metadata, inits);
    }

    public QCommentLike(Class<? extends CommentLike> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this._super = new QContentLike(type, metadata, inits);
        this.comment = inits.isInitialized("comment") ? new community.mingle.api.domain.comment.entity.QComment(forProperty("comment"), inits.get("comment")) : null;
        this.contentType = _super.contentType;
        this.createdAt = _super.createdAt;
        this.deletedAt = _super.deletedAt;
        this.id = _super.id;
        this.member = _super.member;
        this.updatedAt = _super.updatedAt;
    }

}

