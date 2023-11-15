package community.mingle.api.domain.post.repository;

import community.mingle.api.domain.report.entity.Report;
import community.mingle.api.enums.ContentType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {

    List<Report> findAllByContentIdAndContentType(Long contentId, ContentType contentType);




}
