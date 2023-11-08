package community.mingle.api.domain.post.service;

import community.mingle.api.domain.member.entity.Member;
import community.mingle.api.domain.member.repository.MemberRepository;
import community.mingle.api.domain.post.controller.response.PostCategoryResponse;
import community.mingle.api.domain.post.entity.Post;
import community.mingle.api.domain.post.repository.PostRepository;
import community.mingle.api.enums.BoardType;
import community.mingle.api.enums.CategoryType;
import community.mingle.api.enums.ContentStatusType;
import community.mingle.api.enums.MemberRole;
import community.mingle.api.global.exception.CustomException;
import community.mingle.api.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;


    public List<PostCategoryResponse> getPostCategory(MemberRole memberRole) {
        return getCategoriesByMemberRole(memberRole).stream()
                .map(PostCategoryResponse::new)
                .collect(Collectors.toList());
    }

    public List<CategoryType> getCategoriesByMemberRole(MemberRole memberRole) {
        return switch (memberRole) {
            case ADMIN -> Arrays.asList(CategoryType.FREE, CategoryType.QNA, CategoryType.KSA, CategoryType.MINGLE);
            case KSA -> Arrays.asList(CategoryType.FREE, CategoryType.QNA, CategoryType.KSA);
            default -> Arrays.asList(CategoryType.FREE, CategoryType.QNA);
        };
    }


    @Transactional
    public Post createPost(
            String title,
            String content,
            BoardType boardType,
            CategoryType categoryType,
            boolean anonymous,
            boolean fileAttached,
            Long memberId
    ) {
        //TODO 멤버세팅
        Member member = memberRepository.findById(memberId).orElseThrow();
        Post post = Post.builder()
                .title(title)
                .content(content)
                .boardType(boardType)
                .categoryType(categoryType)
                .anonymous(anonymous)
                .fileAttached(fileAttached)
                .member(member)
                .statusType(ContentStatusType.ACTIVE)
                .build();

        return postRepository.save(post);
    }


    @Transactional
    public Post updatePost(Long memberIdByJwt, Long postId, String title, String content, Boolean isAnonymous) {

        Post post = findValidPost(postId);

        if (!Objects.equals(memberIdByJwt, post.getMember().getId())) {
            throw new CustomException(ErrorCode.MODIFY_NOT_AUTHORIZED);
        }

        post.updatePost(title, content, isAnonymous);

        return post;
    }

    @Transactional
    public void deletePost(Long memberIdByJwt, Long postId) {

        Post post = findValidPost(postId);

        if (!Objects.equals(memberIdByJwt, post.getMember().getId())) {
            throw new CustomException(ErrorCode.MODIFY_NOT_AUTHORIZED);
        }

        post.deletePost();
    }

    public Post findValidPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_EXIST));

        if (post.getStatusType().equals(ContentStatusType.DELETED) || post.getStatusType().equals(ContentStatusType.REPORTED)) {
            throw new CustomException(ErrorCode.POST_DELETED_REPORTED);
        }
        return post;
    }

}

