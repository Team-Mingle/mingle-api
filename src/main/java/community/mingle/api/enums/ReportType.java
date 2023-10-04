package community.mingle.api.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum ReportType {
    OBSCENE("음란"),
    AD("광고"),
    FRAUD("사기"),
    INAPPROPRIATE("부적절"),
    SWEAR("욕설");

    private final String description;
}
