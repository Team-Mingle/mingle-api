package community.mingle.api.domain.notification.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QCommentNotification is a Querydsl query type for CommentNotification
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCommentNotification extends EntityPathBase<CommentNotification> {

    private static final long serialVersionUID = 1628434407L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QCommentNotification commentNotification = new QCommentNotification("commentNotification");

    public final QNotification _super;

    //inherited
    public final EnumPath<community.mingle.api.enums.BoardType> boardType;

    //inherited
    public final EnumPath<community.mingle.api.enums.CategoryType> categoryType;

    public final community.mingle.api.domain.comment.entity.QComment comment;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> deletedAt;

    //inherited
    public final NumberPath<Long> id;

    // inherited
    public final community.mingle.api.domain.member.entity.QMember member;

    //inherited
    public final EnumPath<community.mingle.api.enums.NotificationType> notificationType;

    //inherited
    public final BooleanPath read;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt;

    public QCommentNotification(String variable) {
        this(CommentNotification.class, forVariable(variable), INITS);
    }

    public QCommentNotification(Path<? extends CommentNotification> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QCommentNotification(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QCommentNotification(PathMetadata metadata, PathInits inits) {
        this(CommentNotification.class, metadata, inits);
    }

    public QCommentNotification(Class<? extends CommentNotification> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this._super = new QNotification(type, metadata, inits);
        this.boardType = _super.boardType;
        this.categoryType = _super.categoryType;
        this.comment = inits.isInitialized("comment") ? new community.mingle.api.domain.comment.entity.QComment(forProperty("comment"), inits.get("comment")) : null;
        this.createdAt = _super.createdAt;
        this.deletedAt = _super.deletedAt;
        this.id = _super.id;
        this.member = _super.member;
        this.notificationType = _super.notificationType;
        this.read = _super.read;
        this.updatedAt = _super.updatedAt;
    }

}

