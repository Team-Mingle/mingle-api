package community.mingle.api.domain.item.repository;

import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import community.mingle.api.domain.item.entity.ItemComment;
import community.mingle.api.domain.item.entity.QItemComment;
import community.mingle.api.domain.member.entity.QBlockMember;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ItemCommentQueryRepository {


    private final JPAQueryFactory jpaQueryFactory;
    private final QItemComment comment = QItemComment.itemComment;
    private final QBlockMember blockMember = QBlockMember.blockMember;


    public List<ItemComment> findComments(Long id, Long memberId, boolean isCoComment) {
        JPAQuery<ItemComment> commentJPAQuery = jpaQueryFactory.select(comment)
                .from(comment)
                .join(comment.item)
                .on(comment.item.id.eq(id))
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
