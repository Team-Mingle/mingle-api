package community.mingle.api.domain.post.service;

import community.mingle.api.domain.member.entity.Member;
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
//        Member member = memberRepository.find(memberId);
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
    public Post updatePost(Long memberIdByJwt, Long postId, String title, String content, Boolean isAnonymous) {

         Post post = postRepository.findById(postId)
                 .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_EXIST));

         if (post.getStatusType().equals(ContentStatusType.DELETED) || post.getStatusType().equals(ContentStatusType.REPORTED)) {
             throw new CustomException(ErrorCode.POST_DELETED_REPORTED);
         }

        if (!Objects.equals(memberIdByJwt, post.getMember().getId())) {
            throw new CustomException(ErrorCode.MODIFY_NOT_AUTHORIZED);
        }

         post.updatePost(title, content, isAnonymous);

         return post;
    }

}
