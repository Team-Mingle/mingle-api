package community.mingle.api.domain.notification.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QNotification is a Querydsl query type for Notification
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QNotification extends EntityPathBase<Notification> {

    private static final long serialVersionUID = 1561100430L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QNotification notification = new QNotification("notification");

    public final community.mingle.api.entitybase.QAuditLoggingBase _super = new community.mingle.api.entitybase.QAuditLoggingBase(this);

    public final EnumPath<community.mingle.api.enums.BoardType> boardType = createEnum("boardType", community.mingle.api.enums.BoardType.class);

    public final EnumPath<community.mingle.api.enums.CategoryType> categoryType = createEnum("categoryType", community.mingle.api.enums.CategoryType.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final DateTimePath<java.time.LocalDateTime> deletedAt = createDateTime("deletedAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final community.mingle.api.domain.member.entity.QMember member;

    public final EnumPath<community.mingle.api.enums.NotificationType> notificationType = createEnum("notificationType", community.mingle.api.enums.NotificationType.class);

    public final BooleanPath read = createBoolean("read");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QNotification(String variable) {
        this(Notification.class, forVariable(variable), INITS);
    }

    public QNotification(Path<? extends Notification> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QNotification(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QNotification(PathMetadata metadata, PathInits inits) {
        this(Notification.class, metadata, inits);
    }

    public QNotification(Class<? extends Notification> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.member = inits.isInitialized("member") ? new community.mingle.api.domain.member.entity.QMember(forProperty("member"), inits.get("member")) : null;
    }

}

