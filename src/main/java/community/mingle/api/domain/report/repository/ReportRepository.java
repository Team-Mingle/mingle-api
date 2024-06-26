package community.mingle.api.domain.report.repository;

import community.mingle.api.domain.member.entity.Member;
import community.mingle.api.domain.report.entity.Report;
import community.mingle.api.enums.ContentType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReportRepository extends JpaRepository<Report, Long> {
}
