package community.mingle.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum PointEarningType {
    COURSE_EVALUATION(100L),
    ADD_FRIEND(30L);

    private final Long earningAmount;
}
