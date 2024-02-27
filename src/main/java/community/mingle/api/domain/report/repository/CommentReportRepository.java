package community.mingle.api.domain.report.repository;

import community.mingle.api.domain.member.entity.Member;
import community.mingle.api.domain.report.entity.CommentReport;
import community.mingle.api.domain.report.entity.PostReport;
import community.mingle.api.enums.ContentType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentReportRepository extends JpaRepository<CommentReport, Long> {
    Optional<CommentReport> findByReporterMemberAndCommentId(Member reporterMember, Long commentId);

    Long countByCommentId(Long commentId);
}
