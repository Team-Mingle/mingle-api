package community.mingle.api.domain.post.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import community.mingle.api.domain.like.entity.QPostLike;
import community.mingle.api.domain.member.entity.Member;
import community.mingle.api.domain.member.entity.QMember;
import community.mingle.api.domain.post.entity.Post;
import community.mingle.api.domain.post.entity.QPost;
import community.mingle.api.enums.BoardType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PostQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;
    private final QPost post = QPost.post;
    private final QPostLike postLike = QPostLike.postLike;

    private static final int BEST_TOTAL_POST_LIKE_COUNT = 2;
    private static final int BEST_UNIV_POST_LIKE_COUNT = 1;

    public Page<Post> pageBestPosts(Pageable pageable) {
        List<Post> result = jpaQueryFactory
                .selectFrom(post)
                .leftJoin(post.postLikeList, postLike).fetchJoin()
                .where(
                        postLikeCountGreaterThanOrEqual(BEST_TOTAL_POST_LIKE_COUNT)
                                .and(post.boardType.eq(BoardType.TOTAL))
                                .or(
                                        postLikeCountGreaterThanOrEqual(BEST_UNIV_POST_LIKE_COUNT)
                                                .and(post.boardType.eq(BoardType.UNIV))
                                )
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long everyBestPostCount = jpaQueryFactory
                .selectFrom(post)
                .leftJoin(post.postLikeList, postLike)
                .where(
                        postLikeCountGreaterThanOrEqual(BEST_TOTAL_POST_LIKE_COUNT)
                                .and(post.boardType.eq(BoardType.TOTAL))
                                .or(
                                        postLikeCountGreaterThanOrEqual(BEST_UNIV_POST_LIKE_COUNT)
                                                .and(post.boardType.eq(BoardType.UNIV))
                                )
                )
                .stream().count();

        return new PageImpl<>(result, pageable, everyBestPostCount);
    }

    private BooleanExpression postLikeCountGreaterThanOrEqual(int minPostLikeCount) {
        return post.postLikeList.size().goe(minPostLikeCount);
    }
}
