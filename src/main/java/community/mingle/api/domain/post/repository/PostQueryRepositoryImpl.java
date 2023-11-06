package community.mingle.api.domain.post.repository;

import com.querydsl.core.QueryResults;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import community.mingle.api.domain.member.entity.Member;
import community.mingle.api.domain.post.entity.Post;
import community.mingle.api.enums.BoardType;
import community.mingle.api.enums.ContentStatusType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static community.mingle.api.domain.member.entity.QBlockMember.blockMember;
import static community.mingle.api.domain.member.entity.QMember.member;
import static community.mingle.api.domain.post.entity.QPost.post;


@Repository
public class PostQueryRepositoryImpl implements PostQueryRepository{
    private final JPAQueryFactory jpaQueryFactory;

    public PostQueryRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public Page<Post> findBestPostWithMemberLikeComment(BoardType boardType, Member viewMember, Pageable pageable) {
        List<Post> postList = jpaQueryFactory
                .select(post)
                .from(post)
                .join(post.member, member)
                .where(
                    post.statusType.eq(ContentStatusType.ACTIVE),
                    post.boardType.eq(boardType),
                    post.postLikeList.size().gt(4),
                    post.member.id.notIn (
                        JPAExpressions
                            .select(blockMember.blockedMember.id)
                            .from(blockMember)
                            .where(blockMember.blockerMember.id.eq(viewMember.getId()))
                        )
                )
                .orderBy(post.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long totalCount = jpaQueryFactory
                .select(post.count())
                .from(post)
                .join(post.member, member)
                .where(
                        post.statusType.eq(ContentStatusType.ACTIVE),
                        post.boardType.eq(boardType),
                        post.postLikeList.size().gt(4),
                        post.member.id.notIn (
                                JPAExpressions
                                        .select(blockMember.blockedMember.id)
                                        .from(blockMember)
                                        .where(blockMember.blockerMember.id.eq(viewMember.getId()))
                        )
                )
                .fetchOne();

        return new PageImpl<>(postList, pageable, totalCount);

    }

    @Override
    public List<Post> findRecentPost(BoardType boardType, Member viewMember) {
        List<Post> postList = jpaQueryFactory
                .select(post)
                .from(post)
                .join(post.member, member)
                .where(
                        post.statusType.eq(ContentStatusType.ACTIVE),
                        post.boardType.eq(boardType),
                        post.member.id.notIn (
                                JPAExpressions
                                        .select(blockMember.blockedMember.id)
                                        .from(blockMember)
                                        .where(blockMember.blockerMember.id.eq(viewMember.getId()))
                        )
                )
                .orderBy(post.createdAt.desc())
                .offset(0)
                .limit(4)
                .fetch();

        return postList;

    }
}
