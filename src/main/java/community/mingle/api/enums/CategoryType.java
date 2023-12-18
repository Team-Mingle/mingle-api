package community.mingle.api.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum CategoryType {
    FREE("자유"),
    KSA("한인회"),
    MINGLE("밍글소식"),
    QNA("질문");

    private final String categoryName;
}
