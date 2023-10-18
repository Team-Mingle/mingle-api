package community.mingle.api.domain.post.service;

import community.mingle.api.domain.post.entity.Post;
import community.mingle.api.domain.post.repository.PostRepository;
import community.mingle.api.enums.BoardType;
import community.mingle.api.enums.CategoryType;
import community.mingle.api.enums.ContentStatusType;
import community.mingle.api.global.exception.CustomException;
import community.mingle.api.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;


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


    @Transactional
    public Post updatePost(long postId, String title, String content, boolean isAnonymous) {
        //TODO 권한
//        if (!Objects.equals(memberIdByJwt, totalPost.getMember().getId())) { // 2/17 핫픽스
//            throw new BaseException(MODIFY_NOT_AUTHORIZED);
//        }
         Post post = postRepository.findById(postId)
                 .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_EXIST));

         if (post.getStatusType().equals(ContentStatusType.DELETED) || post.getStatusType().equals(ContentStatusType.REPORTED)) {
             throw new CustomException(ErrorCode.POST_DELETED_REPORTED);
         }
         post.updatePost(title, content, isAnonymous);

         return post;
    }

}

