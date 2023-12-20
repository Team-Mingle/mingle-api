package community.mingle.api.domain.report.service;

import community.mingle.api.domain.report.entity.CommentReport;
import community.mingle.api.domain.report.repository.CommentReportRepository;
import community.mingle.api.domain.report.repository.PostReportRepository;
import community.mingle.api.domain.report.entity.PostReport;
import community.mingle.api.domain.report.entity.Report;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final PostReportRepository postReportRepository;
    private final CommentReportRepository commentReportRepository;

    @Transactional
    public PostReport savePostReport(PostReport report) {
        return postReportRepository.save(report);
    }

    @Transactional
    public CommentReport saveCommentReport(CommentReport report) {
        return commentReportRepository.save(report);
    }

}
