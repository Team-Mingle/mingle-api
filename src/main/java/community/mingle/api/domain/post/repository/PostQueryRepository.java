package community.mingle.api.domain.post.repository;

import community.mingle.api.domain.member.entity.Member;
import community.mingle.api.domain.post.entity.Post;
import community.mingle.api.enums.BoardType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PostQueryRepository {
    Page<Post> findBestPostWithMemberLikeComment (BoardType boardType, Member viewMember, Pageable pageable);
    List<Post> findRecentPost(BoardType boardType, Member viewMember);
}
