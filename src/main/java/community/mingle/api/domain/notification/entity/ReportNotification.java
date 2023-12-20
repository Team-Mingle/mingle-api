package community.mingle.api.domain.notification.entity;

import community.mingle.api.domain.report.entity.Report;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@Entity
@SuperBuilder
@Table(name = "report_notification")
public class ReportNotification extends Notification implements NotificationContentProvider {

    @NotNull
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "report_id", nullable = false)
    private Report report;

    @Override
    public String getContent() {
        return "신고가 접수되었습니다.";
    }

    @Override
    public String getBoardType() {
        return null;
    }

    @Override
    public String getCategoryType() {
        return null;
    }
}
