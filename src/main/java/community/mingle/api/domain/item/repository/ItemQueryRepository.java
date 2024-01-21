package community.mingle.api.domain.item.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import community.mingle.api.domain.item.entity.Item;
import community.mingle.api.domain.item.entity.QItem;
import community.mingle.api.domain.member.entity.Member;
import community.mingle.api.domain.member.entity.QBlockMember;
import community.mingle.api.domain.member.entity.QMember;
import community.mingle.api.domain.member.entity.QUniversity;
import community.mingle.api.enums.ItemStatusType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ItemQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    private final QItem item = QItem.item;
    private final QMember member = QMember.member;
    private final QUniversity university = QUniversity.university;
    private final QBlockMember blockMember = QBlockMember.blockMember;


    public Page<Item> findSearchItems(String keyword, Member viewerMember, PageRequest pageRequest) {
        List<Item> itemList = jpaQueryFactory
                .selectFrom(item)
                .leftJoin(item.member, member)
                .leftJoin(member.university, university)
                .where(
                        item.title.contains(keyword)
                                .or(item.content.contains(keyword)),
                        university.country.name.eq(viewerMember.getUniversity().getCountry().getName()),
                        viewableItemCondition(item, viewerMember)
                )
                .orderBy(item.createdAt.desc())
                .offset(pageRequest.getOffset())
                .limit(pageRequest.getPageSize())
                .fetch();

        long itemTotalCount = jpaQueryFactory
                .selectFrom(item)
                .leftJoin(item.member, member)
                .leftJoin(member.university, university)
                .where(
                        item.title.contains(keyword)
                                .or(item.content.contains(keyword)),
                        university.country.name.eq(viewerMember.getUniversity().getCountry().getName()),
                        viewableItemCondition(item, viewerMember)
                )
                .orderBy(item.createdAt.desc())
                .offset(pageRequest.getOffset())
                .limit(pageRequest.getPageSize())
                .stream().count();

        return new PageImpl<>(itemList, pageRequest, itemTotalCount);
    }

    private BooleanExpression viewableItemCondition(QItem item, Member viewerMember) {
        return item.status.in(ItemStatusType.NOTIFIED, ItemStatusType.RESERVED, ItemStatusType.SELLING, ItemStatusType.SOLDOUT)
                .and(item.member.id.notIn(
                        JPAExpressions
                                .select(blockMember.blockedMember.id)
                                .from(blockMember)
                                .where(blockMember.blockerMember.id.eq(viewerMember.getId()))
                ));
    }
}
