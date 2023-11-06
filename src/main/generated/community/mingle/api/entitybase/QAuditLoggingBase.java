package community.mingle.api.entitybase;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QAuditLoggingBase is a Querydsl query type for AuditLoggingBase
 */
@Generated("com.querydsl.codegen.DefaultSupertypeSerializer")
public class QAuditLoggingBase extends EntityPathBase<AuditLoggingBase> {

    private static final long serialVersionUID = -1242372096L;

    public static final QAuditLoggingBase auditLoggingBase = new QAuditLoggingBase("auditLoggingBase");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public QAuditLoggingBase(String variable) {
        super(AuditLoggingBase.class, forVariable(variable));
    }

    public QAuditLoggingBase(Path<? extends AuditLoggingBase> path) {
        super(path.getType(), path.getMetadata());
    }

    public QAuditLoggingBase(PathMetadata metadata) {
        super(AuditLoggingBase.class, metadata);
    }

}

