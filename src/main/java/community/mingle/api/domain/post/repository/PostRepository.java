package community.mingle.api.domain.post.repository;

import community.mingle.api.domain.member.entity.Member;
import community.mingle.api.domain.post.entity.Post;
import community.mingle.api.enums.BoardType;
import community.mingle.api.enums.CategoryType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    public Page<Post> findAllByBoardTypeAndCategoryType(BoardType boardType, CategoryType categoryType, PageRequest pageRequest);

    public Page<Post> findAllByBoardTypeAndCategoryTypeAndMemberUniversityId(BoardType boardType, CategoryType categoryType, PageRequest pageRequest, int memberId);

    Page<Post> findAllByBoardTypeAndMember(BoardType boardType, Member member, PageRequest pageRequest);

    @Query("select distinct p from Comment c join c.member m join c.post p where m.id = :memberId and p.boardType = :boardType")
    Page<Post> findAllByCommentMemberIdAndBoardType(Long memberId, BoardType boardType, PageRequest pageRequest);

    @Query("select p from PostScrap ps join ps.member m join ps.post p where m.id = :memberId and p.boardType = :boardType")
    Page<Post> findAllByScrapMemberIdAndBoardType(Long memberId, BoardType boardType, PageRequest pageRequest);

    @Query("select p from PostLike pl join pl.member m join pl.post p where m.id = :memberId and p.boardType = :boardType")
    Page<Post> findAllByLikeMemberIdAndBoardType(Long memberId, BoardType boardType, PageRequest pageRequest);

    Page<Post> findAllByBoardType(BoardType boardType, PageRequest pageRequest);

    Page<Post> findAllByBoardTypeAndMemberUniversityId(BoardType boardType, PageRequest pageRequest, int universityId);
}
