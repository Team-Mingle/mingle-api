package community.mingle.api.domain.member.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QBlockMember is a Querydsl query type for BlockMember
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QBlockMember extends EntityPathBase<BlockMember> {

    private static final long serialVersionUID = -947501259L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QBlockMember blockMember = new QBlockMember("blockMember");

    public final community.mingle.api.entitybase.QAuditLoggingBase _super = new community.mingle.api.entitybase.QAuditLoggingBase(this);

    public final QMember blockedMember;

    public final QMember blockerMember;

    public final NumberPath<Long> blockMemberId = createNumber("blockMemberId", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QBlockMember(String variable) {
        this(BlockMember.class, forVariable(variable), INITS);
    }

    public QBlockMember(Path<? extends BlockMember> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QBlockMember(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QBlockMember(PathMetadata metadata, PathInits inits) {
        this(BlockMember.class, metadata, inits);
    }

    public QBlockMember(Class<? extends BlockMember> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.blockedMember = inits.isInitialized("blockedMember") ? new QMember(forProperty("blockedMember"), inits.get("blockedMember")) : null;
        this.blockerMember = inits.isInitialized("blockerMember") ? new QMember(forProperty("blockerMember"), inits.get("blockerMember")) : null;
    }

}

