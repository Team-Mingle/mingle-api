package community.mingle.api.domain.post.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import community.mingle.api.domain.like.entity.QPostLike;
import community.mingle.api.domain.member.entity.*;
import community.mingle.api.domain.post.entity.Post;
import community.mingle.api.domain.post.entity.QPost;
import community.mingle.api.enums.BoardType;
import community.mingle.api.enums.ContentStatusType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PostQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;
    private final QPost post = QPost.post;
    private final QPostLike postLike = QPostLike.postLike;
    private final QBlockMember blockMember = QBlockMember.blockMember;
    private final QMember member = QMember.member;
    private final QUniversity university = QUniversity.university;
    private final QCountry country = QCountry.country;


    private static final int BEST_TOTAL_POST_LIKE_COUNT = 10;
    private static final int BEST_UNIV_POST_LIKE_COUNT = 5;
    private static final int LAST_POSTS_LIMIT = 4;

    public Page<Post> pageBestPosts(Member viewerMember, PageRequest pageRequest) {
        List<Post> result = jpaQueryFactory
                .selectFrom(post)
                .leftJoin(post.postLikeList, postLike).fetchJoin()
                .leftJoin(post.member, member).fetchJoin()
                .leftJoin(member.university, university).fetchJoin()
                .where(
                        postLikeCountGreaterThanOrEqual(BEST_TOTAL_POST_LIKE_COUNT)
                                .and(post.boardType.eq(BoardType.TOTAL)
                                        .and((university.country.name).eq(viewerMember.getUniversity().getCountry().getName())))
                                .or(
                                        postLikeCountGreaterThanOrEqual(BEST_UNIV_POST_LIKE_COUNT)
                                                .and(post.boardType.eq(BoardType.UNIV))
                                                .and(university.id.eq(viewerMember.getUniversity().getId()))
                                ),
                        viewablePostCondition(post, viewerMember)
                )
                .orderBy(post.createdAt.desc())
                .offset(pageRequest.getOffset())
                .limit(pageRequest.getPageSize())
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

        return new PageImpl<>(result, pageRequest, everyBestPostCount);
    }

    public List<Post> findRecentPost(BoardType boardType, Member viewMember) {

        return jpaQueryFactory
                .select(post)
                .from(post)
                .leftJoin(post.member, member)
                .leftJoin(member.university, university)
                .where(
                        post.boardType.eq(boardType)
                                .and(
                                        post.boardType.ne(BoardType.UNIV)
                                                .and((university.country.name).eq(viewMember.getUniversity().getCountry().getName()))
                                                .or(university.id.eq(viewMember.getUniversity().getId()))
                                ),
                        viewablePostCondition(post, viewMember)

                )
                .orderBy(post.createdAt.desc())
                .offset(0)
                .limit(LAST_POSTS_LIMIT)
                .fetch();

    }

    private BooleanExpression viewablePostCondition(QPost post, Member viewerMember) {
        return post.statusType.eq(ContentStatusType.ACTIVE)
                .and(post.member.id.notIn(
                        JPAExpressions
                                .select(blockMember.blockedMember.id)
                                .from(blockMember)
                                .where(blockMember.blockerMember.id.eq(viewerMember.getId()))
                ));
    }

    private BooleanExpression postLikeCountGreaterThanOrEqual(int minPostLikeCount) {
        return post.postLikeList.size().goe(minPostLikeCount);
    }

    public Page<Post> findSearchPosts(String keyword, Member viewerMember, PageRequest pageRequest) {
        List<Post> postList = jpaQueryFactory
                .selectFrom(post)
                .leftJoin(post.member, member)
                .leftJoin(member.university, university)
                .where(
                        post.title.contains(keyword)
                            .or(post.content.contains(keyword)),
                        university.country.name.eq(viewerMember.getUniversity().getCountry().getName()),
                        viewablePostCondition(post, viewerMember)
                )
                .orderBy(post.createdAt.desc())
                .offset(pageRequest.getOffset())
                .limit(pageRequest.getPageSize())
                .fetch();

        long postTotalCount = jpaQueryFactory
                .selectFrom(post)
                .leftJoin(post.member, member)
                .leftJoin(member.university, university)
                .where(
                        post.title.contains(keyword)
                                .or(post.content.contains(keyword)),
                        university.country.name.eq(viewerMember.getUniversity().getCountry().getName()),
                        viewablePostCondition(post, viewerMember)
                )
                .orderBy(post.createdAt.desc())
                .offset(pageRequest.getOffset())
                .limit(pageRequest.getPageSize())
                .stream().count();

        return new PageImpl<>(postList, pageRequest, postTotalCount);
    }
}
