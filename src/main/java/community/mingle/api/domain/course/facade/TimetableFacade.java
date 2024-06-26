package community.mingle.api.domain.course.facade;

import community.mingle.api.domain.auth.service.TokenService;
import community.mingle.api.domain.course.controller.request.CreateTimetableRequest;
import community.mingle.api.domain.course.controller.request.UpdateTimetableCourseRequest;
import community.mingle.api.domain.course.controller.request.UpdateTimetableNameRequest;
import community.mingle.api.domain.course.controller.response.*;
import community.mingle.api.domain.course.entity.Course;
import community.mingle.api.domain.course.entity.CourseTime;
import community.mingle.api.domain.course.entity.CourseTimetable;
import community.mingle.api.domain.course.entity.Timetable;
import community.mingle.api.domain.course.service.CourseService;
import community.mingle.api.domain.course.service.TimetableService;
import community.mingle.api.domain.friend.entity.Friend;
import community.mingle.api.domain.friend.service.FriendService;
import community.mingle.api.domain.member.entity.Member;
import community.mingle.api.domain.member.service.MemberService;
import community.mingle.api.dto.course.CoursePreviewDto;
import community.mingle.api.dto.course.CourseTimeDto;
import community.mingle.api.enums.CourseType;
import community.mingle.api.enums.Semester;
import community.mingle.api.global.amplitude.AmplitudeService;
import community.mingle.api.global.exception.CustomException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static community.mingle.api.global.exception.ErrorCode.MEMBER_NOT_FRIEND;

@Service
@RequiredArgsConstructor
public class TimetableFacade {


    private final TimetableService timetableService;
    private final CourseService courseService;
    private final TokenService tokenService;
    private final MemberService memberService;
    private final FriendService friendService;
    private final AmplitudeService amplitudeService;

    @Transactional
    public CreateTimetableResponse createTimetable(CreateTimetableRequest request) {
        Long memberId = tokenService.getTokenInfo().getMemberId();
        Member member = memberService.getById(memberId);
        Timetable timetable = timetableService.createTimetable(member, request.year(), request.semester());

        amplitudeService.log(memberId, "createTimetable", Map.of("timetableId", timetable.getId().toString(), "timetableName", timetable.getName()));
        return new CreateTimetableResponse(
            timetable.getId(),
            timetable.getName(),
            timetable.getSemester()
        );
    }

    @Transactional
    public UpdateTimetableCourseResponse updateTimetableCourse(Long timetableId, UpdateTimetableCourseRequest request) {
        Long memberId = tokenService.getTokenInfo().getMemberId();
        Member member = memberService.getById(memberId);

        Timetable timetable = timetableService.getById(timetableId, member);

        Course course = courseService.getCourseById(request.courseId());
        List<CourseTimeDto> courseTimeDtoList = course.getCourseTimeList().stream().map(CourseTime::toDto).toList();

        timetableService.checkCourseAlreadyAdded(timetable, course);
        timetableService.deleteConflictCoursesByOverrideValidation(timetable, courseTimeDtoList, request.overrideValidation());

        CourseTimetable courseTimetable = timetableService.addCourse(timetable, course);

        amplitudeService.log(memberId, "updateTimetableCourse", Map.of("timetableId", timetable.getId().toString(), "timetableName", timetable.getName(), "courseId", course.getId().toString(), "courseName", course.getName()));
        return new UpdateTimetableCourseResponse(
            true,
            courseTimetable.getRgb()
        );
    }

    @Transactional
    public void deleteTimetableCourse(Long timetableId, Long courseId) {
        Long memberId = tokenService.getTokenInfo().getMemberId();
        Member member = memberService.getById(memberId);

        Timetable timetable = timetableService.getById(timetableId, member);
        Course course = courseService.getCourseById(courseId);
        timetableService.deleteCourse(timetable, course);

        if(course.getType() == CourseType.PERSONAL) {
            courseService.deletePersonalCourse(courseId, memberId);
        }

        amplitudeService.log(memberId, "deleteTimetableCourse", Map.of("timetableId", timetable.getId().toString(), "timetableName", timetable.getName(), "courseId", course.getId().toString(), "courseName", course.getName()));
    }

    @Transactional
    public void deleteTimetable(Long timetableId) {
        Long memberId = tokenService.getTokenInfo().getMemberId();
        Member member = memberService.getById(memberId);
        Timetable timetable = timetableService.getById(timetableId, member);
        timetableService.deleteTimetable(timetable, member);

        amplitudeService.log(memberId, "deleteTimetable", Map.of("timetableId", timetable.getId().toString(), "timetableName", timetable.getName()));
    }

    @Transactional
    public void updateTimetableName(Long timetableId, UpdateTimetableNameRequest request) {
        Long memberId = tokenService.getTokenInfo().getMemberId();
        Member member = memberService.getById(memberId);
        Timetable timetable = timetableService.getById(timetableId, member);
        timetableService.updateTimetableName(timetable, member, request.name());

        amplitudeService.log(memberId, "updateTimetableCourse", Map.of("timetableId", timetable.getId().toString(), "timetableName", timetable.getName()));
    }

    @Transactional
    public void changePinnedTimetable(Long timetableId) {
        Long memberId = tokenService.getTokenInfo().getMemberId();
        Member member = memberService.getById(memberId);
        Timetable timetable = timetableService.getById(timetableId, member);
        timetableService.changePinnedTimetable(timetable, member);

        amplitudeService.log(memberId, "changePinnedTimetable", Map.of("timetableId", timetable.getId().toString(), "timetableName", timetable.getName()));
    }

    public TimetableListResponse getTimetableList() {
        Long memberId = tokenService.getTokenInfo().getMemberId();
        Member member = memberService.getById(memberId);
        List<Timetable> timetableList = timetableService.getTimetableList(member);
        Map<Semester, List<TimetablePreviewResponse>> semesterListMap = timetableList.stream()
                .collect(Collectors.groupingBy(
                        Timetable::getSemester,
                        () -> new TreeMap<>(
                                Comparator.comparing(Semester::getYear)
                                        .thenComparing(Semester::getSemester).reversed()
                        ),
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                timetables -> timetableService.orderTimetableList(timetables)
                                        .stream()
                                        .map(timetable -> new TimetablePreviewResponse(
                                                timetable.getId(),
                                                timetable.getSemester(),
                                                timetable.getName(),
                                                timetable.getOrderNumber(),
                                                timetable.getIsPinned()
                                        ))
                                        .collect(Collectors.toList())
                        )
                        )
                );

        amplitudeService.log(memberId, "getTimetableList", null);

        return new TimetableListResponse(semesterListMap);
    }

    public TimetableDetailResponse getTimetableDetail(Long timetableId) {
        Long memberId = tokenService.getTokenInfo().getMemberId();
        Member member = memberService.getById(memberId);

        Timetable timetable = timetableService.getById(timetableId, member);

        amplitudeService.log(memberId, "getTimetableDetail", Map.of("timetableId", timetable.getId().toString(), "timetableName", timetable.getName()));
        return getTimetableDetailResponse(timetable);
    }

    public FriendTimetableDetailResponse getFriendTimetableList(Long friendId) {
        Long memberId = tokenService.getTokenInfo().getMemberId();
        Friend friendRelation = friendService.getById(friendId);
        Member friend = friendRelation.getFriend();
        boolean isMyFriend = friend.getFriendsOfMine().stream()
                .anyMatch(it -> it.getFriend().getId().equals(memberId));
        if (!isMyFriend) {
            throw new CustomException(MEMBER_NOT_FRIEND);
        }
        List<Timetable> pinnedTimetableList = timetableService.listByIdAndIsPinnedTrue(friend);
        List<TimetableDetailResponse> timetableDetailResponseList = pinnedTimetableList.stream()
                .map(this::getTimetableDetailResponse).toList();

        amplitudeService.log(memberId, "getFriendTimetableList", Map.of("friendId", friendId.toString()));

        return new FriendTimetableDetailResponse(timetableDetailResponseList);
    }

    public DefaultTimetableIdResponse getDefaultTimetableId() {
        Long memberId = tokenService.getTokenInfo().getMemberId();
        Member member = memberService.getById(memberId);
        Long defaultTimetableId = timetableService.listByIdAndIsPinnedTrue(member)
                .stream()
                .max(Comparator.comparing((Timetable t) -> t.getSemester().getYear())
                        .thenComparingInt(t -> t.getSemester().getSemester()))
                .get().getId();

        return new DefaultTimetableIdResponse(defaultTimetableId);
    }

    @NotNull
    private TimetableDetailResponse getTimetableDetailResponse(Timetable timetable) {
        List<CoursePreviewDto> coursePreviewResponseList = timetable.getCourseTimetableList().stream()
                .map(courseTimetable -> {
                    Course course = courseTimetable.getCourse();
                    return new CoursePreviewDto(
                            course.getId(),
                            course.getName(),
                            course.getCourseCode(),
                            course.getSemester(),
                            course.getProfessor(),
                            course.getSubclass(),
                            course.getCourseTimeList().stream()
                                    .map(CourseTime::toDto)
                                    .toList(),
                            course.getVenue(),
                            courseTimetable.getRgb(),
                            course.getType()
                    );
                })
                .toList();
        return new TimetableDetailResponse(
                timetable.getName(),
                timetable.getSemester(),
                coursePreviewResponseList
        );
    }
}
