package community.mingle.api.domain.post.repository;

import community.mingle.api.domain.post.entity.Post;
import community.mingle.api.enums.BoardType;
import community.mingle.api.enums.CategoryType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long>{

    public Page<Post> findAllByBoardTypeAndCategoryType(BoardType boardType, CategoryType categoryType, PageRequest pageRequest);

    Page<Post> findAllByBoardType(BoardType boardType, PageRequest pageRequest);
}
