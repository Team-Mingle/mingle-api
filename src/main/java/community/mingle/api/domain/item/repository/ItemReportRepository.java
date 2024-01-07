package community.mingle.api.domain.item.repository;

import community.mingle.api.domain.report.entity.ItemReport;
import community.mingle.api.domain.report.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemReportRepository extends JpaRepository<ItemReport, Long> {

    List<ItemReport> findByItemId(Long itemId);
}
