package community.mingle.api.domain.post.repository;

import community.mingle.api.domain.report.entity.PostReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostReportRepository extends JpaRepository<PostReport, Long> {
    List<PostReport> findAllByContentId(Long postId);
}
