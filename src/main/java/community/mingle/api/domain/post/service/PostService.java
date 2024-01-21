package community.mingle.api.domain.post.service;

import community.mingle.api.domain.comment.entity.Comment;
import community.mingle.api.domain.member.entity.Member;
import community.mingle.api.domain.member.repository.MemberRepository;
import community.mingle.api.domain.post.entity.Post;
import community.mingle.api.domain.post.entity.PostImage;
import community.mingle.api.domain.post.repository.*;
import community.mingle.api.domain.report.entity.PostReport;
import community.mingle.api.domain.report.entity.Report;
import community.mingle.api.domain.report.repository.PostReportRepository;
import community.mingle.api.dto.post.PostStatusDto;
import community.mingle.api.enums.*;
import community.mingle.api.global.exception.CustomException;
import community.mingle.api.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static community.mingle.api.enums.ContentStatusType.REPORTED;
import static community.mingle.api.global.exception.ErrorCode.MEMBER_NOT_FOUND;
import static community.mingle.api.global.exception.ErrorCode.POST_NOT_EXIST;


@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final PostLikeRepository postLikeRepository;
    private final PostScrapRepository postScrapRepository;
    private final PostReportRepository postReportRepository;
    private final PostQueryRepository postQueryRepository;

    @Transactional
    public Post createPost(
            Long memberId,
            String title,
            String content,
            BoardType boardType,
            CategoryType categoryType,
            boolean anonymous,
            boolean fileAttached
    ) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        Post post = Post.builder()
                .title(title)
                .content(content)
                .boardType(boardType)
                .categoryType(categoryType)
                .anonymous(anonymous)
                .member(member)
                .statusType(ContentStatusType.ACTIVE)
                .fileAttached(fileAttached)
                .statusType(ContentStatusType.ACTIVE)
                .build();

        return postRepository.save(post);
    }

    public Post getPost(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(POST_NOT_EXIST));
    }

    public Page<Post> getBestPostList(Long viewerMemberId, PageRequest pageRequest) {
        Member viewerMember = memberRepository.findById(viewerMemberId).orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));
        return postQueryRepository.pageBestPosts(viewerMember, pageRequest);
    }

    public List<Post> getRecentPostList(BoardType boardType, Long memberId) {
        Member viewMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        return postQueryRepository.findRecentPost(boardType, viewMember);
    }

    public ReportType getReportedPostReason(Long postId) {
        List<PostReport> postReportList = postReportRepository.findAllByContentId(postId);

        if (postReportList == null || postReportList.isEmpty()) return null;

        return postReportList.stream()
                .collect(Collectors.groupingBy(Report::getReportType, Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
    }


    public List<Post> pagePostsByBoardType(BoardType boardType, PageRequest pageRequest) {
        Page<Post> pagePosts = postRepository.findAllByBoardType(boardType, pageRequest);
        return pagePosts.toList();
    }

    public List<Post> pagePostsByBoardTypeAndCategory(BoardType boardType, CategoryType categoryType, PageRequest pageRequest) {
        Page<Post> pagePosts = postRepository.findAllByBoardTypeAndCategoryType(boardType, categoryType, pageRequest);
        return pagePosts.toList();
    }

    public List<Post> pagePostsByBoardTypeAndMember(BoardType boardType, Member member, PageRequest pageRequest) {
        Page<Post> pagePosts = postRepository.findAllByBoardTypeAndMember(boardType, member, pageRequest);
        return pagePosts.toList();
    }

    public List<Post> pageCommentPostsByBoardTypeAndMember(BoardType boardType, Member member, PageRequest pageRequest) {
        Page<Post> pagePosts = postRepository.findAllByCommentMemberIdAndBoardType(member.getId(), boardType, pageRequest);
        return pagePosts.toList();
    }

    public List<Post> pageScrapPostsByBoardTypeAndMember(BoardType boardType, Member member, PageRequest pageRequest) {
        Page<Post> pagePosts = postRepository.findAllByScrapMemberIdAndBoardType(member.getId(), boardType, pageRequest);
        return pagePosts.toList();
    }

    public List<Post> pageLikePostsByBoardTypeAndMember(BoardType boardType, Member member, PageRequest pageRequest) {
        Page<Post> pagePosts = postRepository.findAllByLikeMemberIdAndBoardType(member.getId(), boardType, pageRequest);
        return pagePosts.toList();
    }


    @Transactional
    public Post updatePost(Long memberId, Long postId, String title, String content, Boolean isAnonymous) {

        Post post = getValidPost(postId);

        if (!Objects.equals(memberId, post.getMember().getId())) {
            throw new CustomException(ErrorCode.MODIFY_NOT_AUTHORIZED);
        }

        post.updatePost(title, content, isAnonymous);

        return post;
    }

    @Transactional
    public void deletePost(Long memberIdByJwt, Long postId) {

        Post post = getValidPost(postId);

        if (!Objects.equals(memberIdByJwt, post.getMember().getId())) {
            throw new CustomException(ErrorCode.MODIFY_NOT_AUTHORIZED);
        }

        postRepository.delete(post);
    }

    public List<CategoryType> getCategoryListByMemberRole(MemberRole memberRole) {
        return switch (memberRole) {
            case ADMIN -> Arrays.asList(CategoryType.FREE, CategoryType.QNA, CategoryType.KSA, CategoryType.MINGLE);
            case KSA -> Arrays.asList(CategoryType.FREE, CategoryType.QNA, CategoryType.KSA);
            default -> Arrays.asList(CategoryType.FREE, CategoryType.QNA);
        };
    }

    public PostStatusDto getPostStatus(Post post, Long memberIdByJwt) {
        boolean isMyPost = Objects.equals(post.getMember().getId(), memberIdByJwt);
        boolean isLiked = postLikeRepository.countByPostIdAndMemberId(post.getId(), memberIdByJwt) > 0;
        boolean isScraped = postScrapRepository.countByPostIdAndMemberId(post.getId(), memberIdByJwt) > 0;
        boolean isBlinded; //TODO
        return new PostStatusDto(isMyPost, isLiked, isScraped, false);
    }

    public String calculateNickname(Post post) {
        if (post.getAnonymous()) {
            return "익명";
        } else if (post.getMember().getRole() == MemberRole.FRESHMAN) {
            return "🐥" + post.getMember().getNickname();
        } else {
            return post.getMember().getNickname();
        }
    }

    public String titleByStatus(Post post) {
        return switch (post.getStatusType()) {
            case INACTIVE -> throw new CustomException(POST_NOT_EXIST);
            case REPORTED -> "다른 사용자들의 신고에 의해 삭제된 글 입니다.";
            case DELETED -> "운영규칙 위반에 따라 삭제된 글입니다.";
            default -> post.getTitle();
        };
    }

    public String contentByStatus(Post post) {
        return switch (post.getStatusType()) {
            case INACTIVE -> throw new CustomException(POST_NOT_EXIST);
            case REPORTED -> {
                ReportType reportType = getReportedPostReason(post.getId());
                yield "사유: " + reportType.getDescription();
            }
            case DELETED -> "사유: 이용약관 제 12조 위반";
            default -> post.getContent();
        };
    }

    public int calculateActiveCommentCount(Post post) {
        List<Comment> commentList = post.getCommentList();
        return (int) commentList.stream().filter(ac -> ac.getStatusType().equals(ContentStatusType.ACTIVE)).count();
    }

    public List<String> collectPostImageUrls(Post post) {
        if (post.getFileAttached()) {
            return post.getPostImageList().stream().map(PostImage::getUrl).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    public void updateView(Post post) {
        post.updateView();
    }

    public boolean isValidPost(Post post) {
        return !post.getStatusType().equals(ContentStatusType.DELETED) && !post.getStatusType().equals(REPORTED);
    }

    private Post getValidPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(POST_NOT_EXIST));

        if (post.getStatusType().equals(ContentStatusType.DELETED) || post.getStatusType().equals(REPORTED)) {
            throw new CustomException(ErrorCode.POST_DELETED_REPORTED);
        }
        return post;
    }


    public List<Post> getPostByKeyword(String keyword, Long viewerMemberId, PageRequest pageRequest) {
        Member viewerMember = memberRepository.findById(viewerMemberId).orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));
        return postQueryRepository.findSearchPosts(keyword, viewerMember, pageRequest).toList();
    }

}

