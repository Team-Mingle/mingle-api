package community.mingle.api.domain.item.repository;

import community.mingle.api.domain.member.entity.Member;
import community.mingle.api.domain.report.entity.ItemReport;
import community.mingle.api.domain.report.entity.PostReport;
import community.mingle.api.domain.report.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ItemReportRepository extends JpaRepository<ItemReport, Long> {

    List<ItemReport> findByItemId(Long itemId);

    Optional<ItemReport> findByReporterMemberAndItemId(Member reporterMember, Long itemId);

    Long countByItemId(Long itemId);
}
