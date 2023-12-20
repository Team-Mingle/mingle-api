package community.mingle.api.domain.report.repository;

import community.mingle.api.domain.report.entity.CommentReport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentReportRepository extends JpaRepository<CommentReport, Long> {
}
