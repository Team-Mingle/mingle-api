package community.mingle.api.domain.comment.repository;

import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import community.mingle.api.domain.comment.entity.Comment;
import community.mingle.api.domain.comment.entity.QComment;
import community.mingle.api.domain.member.entity.QBlockMember;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CommentQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;
    private final QComment comment = QComment.comment;
    private final QBlockMember blockMember = QBlockMember.blockMember;

    public List<Comment> findComments(Long id, Long memberId, boolean isCoComment) {
        JPAQuery<Comment> commentJPAQuery = jpaQueryFactory.select(comment)
                .from(comment)
                .join(comment.post)
                .on(comment.post.id.eq(id))
                .where(
                        comment.member.id.notIn(
                                JPAExpressions.select(blockMember.blockedMember.id)
                                        .from(blockMember)
                                        .where(blockMember.blockerMember.id.eq(memberId))
                        )
                );
        if (isCoComment) {
            commentJPAQuery
                    .where(comment.parentCommentId.isNotNull());
        } else commentJPAQuery
                .where(comment.parentCommentId.isNull());

        return commentJPAQuery
                .orderBy(comment.createdAt.asc())
                .fetch();
    }
}
