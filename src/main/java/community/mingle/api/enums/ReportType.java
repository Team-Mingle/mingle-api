package community.mingle.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ReportType {
    OBSCENE("음란/불건전한 대화"),
    AD("상업적 광고 및 판매"),
    FRAUD("유출/사칭/사기"),
    INAPPROPRIATE("게시판 성격에 부적절함"),
    SWEAR("욕설/인신공격/혐오/비하");

    private final String description;
}
