package community.mingle.api.domain.report.repository;

import community.mingle.api.domain.member.entity.Member;
import community.mingle.api.domain.report.entity.PostReport;
import community.mingle.api.enums.ContentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostReportRepository extends JpaRepository<PostReport, Long> {
    List<PostReport> findAllByPostId(Long postId);

    Optional<PostReport> findByReporterMemberAndPostId(Member reporterMember, Long postId);

    Long countByPostId(Long postId);

}
