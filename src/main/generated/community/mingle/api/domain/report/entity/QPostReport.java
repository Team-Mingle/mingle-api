package community.mingle.api.domain.report.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPostReport is a Querydsl query type for PostReport
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPostReport extends EntityPathBase<PostReport> {

    private static final long serialVersionUID = -314793312L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPostReport postReport = new QPostReport("postReport");

    public final QReport _super;

    //inherited
    public final EnumPath<community.mingle.api.enums.ContentType> contentType;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> deletedAt;

    //inherited
    public final NumberPath<Long> id;

    public final community.mingle.api.domain.post.entity.QPost post;

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

    public QPostReport(String variable) {
        this(PostReport.class, forVariable(variable), INITS);
    }

    public QPostReport(Path<? extends PostReport> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPostReport(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPostReport(PathMetadata metadata, PathInits inits) {
        this(PostReport.class, metadata, inits);
    }

    public QPostReport(Class<? extends PostReport> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this._super = new QReport(type, metadata, inits);
        this.contentType = _super.contentType;
        this.createdAt = _super.createdAt;
        this.deletedAt = _super.deletedAt;
        this.id = _super.id;
        this.post = inits.isInitialized("post") ? new community.mingle.api.domain.post.entity.QPost(forProperty("post"), inits.get("post")) : null;
        this.reason = _super.reason;
        this.reportedMember = _super.reportedMember;
        this.reporterMember = _super.reporterMember;
        this.reportType = _super.reportType;
        this.updatedAt = _super.updatedAt;
    }

}

