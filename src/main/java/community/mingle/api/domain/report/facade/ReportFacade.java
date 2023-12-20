package community.mingle.api.domain.report.facade;

import community.mingle.api.domain.auth.service.TokenService;
import community.mingle.api.domain.comment.entity.Comment;
import community.mingle.api.domain.comment.service.CommentService;
import community.mingle.api.domain.member.entity.Member;
import community.mingle.api.domain.member.service.MemberService;
import community.mingle.api.domain.post.entity.Post;
import community.mingle.api.domain.post.service.PostService;
import community.mingle.api.domain.report.controller.request.ReportRequest;
import community.mingle.api.domain.report.entity.CommentReport;
import community.mingle.api.domain.report.entity.PostReport;
import community.mingle.api.domain.report.entity.Report;
import community.mingle.api.domain.report.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReportFacade {

    private final MemberService memberService;
    private final PostService postService;
    private final ReportService reportService;
    private final CommentService commentService;
    private final TokenService tokenService;


    @Transactional
    public void report(ReportRequest request) {
        Long reporterMemberId = tokenService.getTokenInfo().getMemberId();
        Member reporterMember = memberService.getById(reporterMemberId);

        switch (request.contentType()) {
            case POST -> {
                Post post = postService.getPost(request.contentId());
                PostReport postReport = Report.createPostReport(reporterMember, request.reportType(), post);
                reportService.savePostReport(postReport);
            }
            case COMMENT -> {
                Comment comment = commentService.getComment(request.contentId());
                CommentReport commentReport = Report.createCommentReport(reporterMember, request.reportType(), comment);
                reportService.saveCommentReport(commentReport);
            }
        }
    }
}
