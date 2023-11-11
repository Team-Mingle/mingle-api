package community.mingle.api.domain.auth.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QAuthenticationCode is a Querydsl query type for AuthenticationCode
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAuthenticationCode extends EntityPathBase<AuthenticationCode> {

    private static final long serialVersionUID = 461764453L;

    public static final QAuthenticationCode authenticationCode = new QAuthenticationCode("authenticationCode");

    public final StringPath authToken = createString("authToken");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final StringPath email = createString("email");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public QAuthenticationCode(String variable) {
        super(AuthenticationCode.class, forVariable(variable));
    }

    public QAuthenticationCode(Path<? extends AuthenticationCode> path) {
        super(path.getType(), path.getMetadata());
    }

    public QAuthenticationCode(PathMetadata metadata) {
        super(AuthenticationCode.class, metadata);
    }

}

