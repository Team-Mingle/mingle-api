package community.mingle.api.domain.report.controller.request;

import community.mingle.api.enums.ContentType;
import community.mingle.api.enums.ReportType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

public record ReportRequest(
        @NotNull(message = "신고하고자 하는 컨텐츠의 타입을 입력해주세요. (POST, COMMENT)")
        ContentType contentType,
        @NotNull(message = "신고하고자 하는 컨텐츠의 ID를 입력해주세요.")
        Long contentId,
        @NotNull(message = "신고 사유 타입을 입력해주세요. (OBSCENE, AD, FRAUD, INAPPROPRIATE, SWEAR)")
        ReportType reportType
) {
}
