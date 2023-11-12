package community.mingle.api.domain.post.service;

import community.mingle.api.domain.comment.entity.Comment;
import community.mingle.api.domain.member.entity.Member;
import community.mingle.api.domain.member.repository.MemberRepository;
import community.mingle.api.domain.post.entity.Post;
import community.mingle.api.domain.post.entity.PostImage;
import community.mingle.api.domain.post.repository.PostLikeRepository;
import community.mingle.api.domain.post.repository.PostRepository;
import community.mingle.api.domain.post.repository.PostScrapRepository;
import community.mingle.api.domain.post.repository.ReportRepository;
import community.mingle.api.domain.report.entity.Report;
import community.mingle.api.enums.*;
import community.mingle.api.global.exception.CustomException;
import community.mingle.api.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static community.mingle.api.enums.ContentStatusType.REPORTED;
import static community.mingle.api.global.exception.ErrorCode.POST_NOT_EXIST;


@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final PostScrapRepository postScrapRepository;
    private final ReportRepository reportRepository;
    private final MemberRepository memberRepository;

    public List<CategoryType> getCategoriesByMemberRole(MemberRole memberRole) {
        return switch (memberRole) {
            case ADMIN -> Arrays.asList(CategoryType.FREE, CategoryType.QNA, CategoryType.KSA, CategoryType.MINGLE);
            case KSA -> Arrays.asList(CategoryType.FREE, CategoryType.QNA, CategoryType.KSA);
            default -> Arrays.asList(CategoryType.FREE, CategoryType.QNA);
        };
    }


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
        Member member = memberRepository.findById(memberId).orElseThrow(()->new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        Post post = Post.builder()
                .title(title)
                .content(content)
                .boardType(boardType)
                .categoryType(categoryType)
                .anonymous(anonymous)
                .member(member)
                .statusType(ContentStatusType.ACTIVE)
                .fileAttached(fileAttached)
                .build();

        return postRepository.save(post);
    }

    public Post getPost(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(POST_NOT_EXIST));
    }

    public PostStatusDto getPostStatus(Post post, Long memberIdByJwt) {
        boolean isMyPost = Objects.equals(post.getMember().getId(), memberIdByJwt);
        boolean isLiked  = postLikeRepository.countByPostIdAndMemberId(post.getId(), memberIdByJwt) > 0;
        boolean isScraped = postScrapRepository.countByPostIdAndMemberId(post.getId(), memberIdByJwt) > 0;
        boolean isBlinded; //TODO
        return new PostStatusDto(isMyPost, isLiked, isScraped, false);
    }
    public record PostStatusDto(boolean isMyPost, boolean isLiked, boolean isScraped, boolean isBlinded)  {
    }

    public ReportType findReportedPostReason(Long postId, ContentType tableType) {
        List<Report> reportedPost = reportRepository.findAllByContentIdAndContentType(postId, tableType);

        if (reportedPost == null || reportedPost.isEmpty()) return null;

        return reportedPost.stream()
                .collect(Collectors.groupingBy(Report::getReportType, Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
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

    public int calculateActiveCommentCount(Post post) {
        List<Comment> commentList = post.getComments();
        return (int) commentList.stream().filter(ac -> ac.getStatusType().equals(ContentStatusType.ACTIVE)).count();
    }

    public List<String> collectPostImageUrls(Post post) {
        if (post.getFileAttached()) {
            return post.getPostImages().stream().map(PostImage::getUrl).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    public void updateView(Post post) {
        post.updateView();
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

    public Post getValidPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(POST_NOT_EXIST));

        if (post.getStatusType().equals(ContentStatusType.DELETED) || post.getStatusType().equals(REPORTED)) {
            throw new CustomException(ErrorCode.POST_DELETED_REPORTED);
        }
        return post;
    }

}

