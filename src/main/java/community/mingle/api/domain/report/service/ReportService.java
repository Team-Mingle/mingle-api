package community.mingle.api.domain.report.service;

import community.mingle.api.domain.item.repository.ItemReportRepository;
import community.mingle.api.domain.member.entity.Member;
import community.mingle.api.domain.report.entity.CommentReport;
import community.mingle.api.domain.report.entity.PostReport;
import community.mingle.api.domain.report.repository.CommentReportRepository;
import community.mingle.api.domain.report.repository.PostReportRepository;
import community.mingle.api.enums.ContentType;
import community.mingle.api.global.exception.CustomException;
import community.mingle.api.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static community.mingle.api.global.exception.ErrorCode.ALREADY_REPORTED;


@Service
@RequiredArgsConstructor
public class ReportService {

    private final PostReportRepository postReportRepository;
    private final CommentReportRepository commentReportRepository;
    private final ItemReportRepository itemReportRepository;


    @Transactional
    public PostReport savePostReport(PostReport report) {
        return postReportRepository.save(report);
    }

    @Transactional
    public CommentReport saveCommentReport(CommentReport report) {
        return commentReportRepository.save(report);
    }

    public void checkReportDuplicated(Member reporterMember, ContentType contentType, Long contentId) {
        switch (contentType) {
            case POST -> {
                postReportRepository.findByReporterMemberAndPostId(reporterMember, contentId)
                        .ifPresent(report -> {
                            throw new CustomException(ALREADY_REPORTED);
                        });
            }
            case COMMENT -> {
                commentReportRepository.findByReporterMemberAndCommentId(reporterMember, contentId)
                        .ifPresent(report -> {
                            throw new CustomException(ALREADY_REPORTED);
                        });
            }
            case ITEM -> {
                itemReportRepository.findByReporterMemberAndItemId(reporterMember, contentId)
                        .ifPresent(report -> {
                            throw new CustomException(ALREADY_REPORTED);
                        });
            }
            default -> throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        };
    }

    public Integer getReportCount(ContentType contentType, Long contentId) {
        return switch (contentType) {
            case POST -> postReportRepository.countByPostId(contentId).intValue();
            case COMMENT -> commentReportRepository.countByCommentId(contentId).intValue();
            case ITEM -> itemReportRepository.countByItemId(contentId).intValue();
            default -> throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        };
    }
}
