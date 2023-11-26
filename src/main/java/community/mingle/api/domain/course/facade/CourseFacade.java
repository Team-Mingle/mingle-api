package community.mingle.api.domain.course.facade;

import community.mingle.api.domain.auth.service.TokenService;
import community.mingle.api.domain.course.controller.request.CreatePersonalCourseRequest;
import community.mingle.api.domain.course.controller.response.CreatePersonalCourseResponse;
import community.mingle.api.domain.course.service.CourseService;
import community.mingle.api.domain.member.entity.Member;
import community.mingle.api.domain.member.service.MemberService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CourseFacade {
    private final CourseService courseService;
    private final MemberService memberService;
    private final TokenService tokenService;

    @Transactional
    public CreatePersonalCourseResponse createPersonalCourse(CreatePersonalCourseRequest request) {

        Long memberId = tokenService.getTokenInfo().getMemberId();
        Member member = memberService.getById(memberId);
        request.courseTimeDtoList().forEach(courseTimeDto ->
                 courseService.createPersonalCourse(
                    request.courseCode(),
                    request.name(),
                    courseTimeDto.dayOfWeek(),
                    courseTimeDto.startTime(),
                    courseTimeDto.endTime(),
                    request.venue(),
                    request.professor(),
                    request.memo(),
                    member.getUniversity()
            )

        );
        return new CreatePersonalCourseResponse(
                request.name(),
                request.courseTimeDtoList(),
                request.courseCode(),
                request.venue()
        );
    }
}
