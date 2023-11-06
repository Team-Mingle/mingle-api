package community.mingle.api.domain.notification.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPostNotification is a Querydsl query type for PostNotification
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPostNotification extends EntityPathBase<PostNotification> {

    private static final long serialVersionUID = 133297102L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPostNotification postNotification = new QPostNotification("postNotification");

    public final QNotification _super;

    //inherited
    public final EnumPath<community.mingle.api.enums.BoardType> boardType;

    //inherited
    public final EnumPath<community.mingle.api.enums.CategoryType> categoryType;

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

    public final community.mingle.api.domain.post.entity.QPost post;

    //inherited
    public final BooleanPath read;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt;

    public QPostNotification(String variable) {
        this(PostNotification.class, forVariable(variable), INITS);
    }

    public QPostNotification(Path<? extends PostNotification> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPostNotification(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPostNotification(PathMetadata metadata, PathInits inits) {
        this(PostNotification.class, metadata, inits);
    }

    public QPostNotification(Class<? extends PostNotification> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this._super = new QNotification(type, metadata, inits);
        this.boardType = _super.boardType;
        this.categoryType = _super.categoryType;
        this.createdAt = _super.createdAt;
        this.deletedAt = _super.deletedAt;
        this.id = _super.id;
        this.member = _super.member;
        this.notificationType = _super.notificationType;
        this.post = inits.isInitialized("post") ? new community.mingle.api.domain.post.entity.QPost(forProperty("post"), inits.get("post")) : null;
        this.read = _super.read;
        this.updatedAt = _super.updatedAt;
    }

}

