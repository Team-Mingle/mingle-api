package community.mingle.api.domain.course.controller.response;

import java.util.List;

public record FriendTimetableDetailResponse(
        List<TimetableDetailResponse> friendTimetableDetailList
) {

}
