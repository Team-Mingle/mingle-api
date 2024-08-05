package community.mingle.api.domain.course.controller.request;

import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
public class AddCrawledCourseRequest {
    String courseCode;
    String name;
    Integer semester;
    String venue;
    String professor;
    String subclass;
    String memo;
    String prerequisite;
    List<CourseTimeRequest> courseTimeList;
}

