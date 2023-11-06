package community.mingle.api.domain.member.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUniversity is a Querydsl query type for University
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUniversity extends EntityPathBase<University> {

    private static final long serialVersionUID = 413307680L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QUniversity university = new QUniversity("university");

    public final QCountry country;

    public final StringPath emailDomain = createString("emailDomain");

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final StringPath name = createString("name");

    public QUniversity(String variable) {
        this(University.class, forVariable(variable), INITS);
    }

    public QUniversity(Path<? extends University> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QUniversity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QUniversity(PathMetadata metadata, PathInits inits) {
        this(University.class, metadata, inits);
    }

    public QUniversity(Class<? extends University> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.country = inits.isInitialized("country") ? new QCountry(forProperty("country")) : null;
    }

}

