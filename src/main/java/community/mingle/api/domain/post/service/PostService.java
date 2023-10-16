package community.mingle.api.domain.post.service;

import community.mingle.api.domain.post.entity.Post;
import community.mingle.api.domain.post.repository.PostRepository;
import community.mingle.api.enums.BoardType;
import community.mingle.api.enums.CategoryType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;

    @Transactional
    public Post createPost(
                String title,
                String content,
                BoardType boardType,
                CategoryType categoryType,
                boolean anonymous,
                boolean fileAttached
    ) {
        //TODO 멤버세팅
        //Member member = memberRepository.find(memberId);

        Post post = Post.builder()
                        .title(title)
                        .content(content)
                        .boardType(boardType)
                        .categoryType(categoryType)
                        .anonymous(anonymous)
                        .fileAttached(fileAttached)
                        .build();

        return postRepository.save(post);
    }

}


}

