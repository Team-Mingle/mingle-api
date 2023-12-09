package community.mingle.api.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BoardType {
    TOTAL("광장"),
    UNIV("잔디밭");

    private final String boardName;
}
