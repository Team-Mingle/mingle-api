package community.mingle.api.domain.report.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QCommentReport is a Querydsl query type for CommentReport
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCommentReport extends EntityPathBase<CommentReport> {

    private static final long serialVersionUID = -2070995897L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QCommentReport commentReport = new QCommentReport("commentReport");

    public final QReport _super;

    public final community.mingle.api.domain.comment.entity.QComment comment;

    //inherited
    public final EnumPath<community.mingle.api.enums.ContentType> contentType;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> deletedAt;

    //inherited
    public final NumberPath<Long> id;

    //inherited
    public final StringPath reason;

    // inherited
    public final community.mingle.api.domain.member.entity.QMember reportedMember;

    // inherited
    public final community.mingle.api.domain.member.entity.QMember reporterMember;

    //inherited
    public final EnumPath<community.mingle.api.enums.ReportType> reportType;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt;

    public QCommentReport(String variable) {
        this(CommentReport.class, forVariable(variable), INITS);
    }

    public QCommentReport(Path<? extends CommentReport> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QCommentReport(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QCommentReport(PathMetadata metadata, PathInits inits) {
        this(CommentReport.class, metadata, inits);
    }

    public QCommentReport(Class<? extends CommentReport> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this._super = new QReport(type, metadata, inits);
        this.comment = inits.isInitialized("comment") ? new community.mingle.api.domain.comment.entity.QComment(forProperty("comment"), inits.get("comment")) : null;
        this.contentType = _super.contentType;
        this.createdAt = _super.createdAt;
        this.deletedAt = _super.deletedAt;
        this.id = _super.id;
        this.reason = _super.reason;
        this.reportedMember = _super.reportedMember;
        this.reporterMember = _super.reporterMember;
        this.reportType = _super.reportType;
        this.updatedAt = _super.updatedAt;
    }

}

