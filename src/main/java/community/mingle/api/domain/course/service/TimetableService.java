package community.mingle.api.domain.course.service;

import community.mingle.api.domain.course.entity.Timetable;
import community.mingle.api.domain.course.repository.TimetableRepository;
import community.mingle.api.domain.member.entity.Member;
import community.mingle.api.domain.member.repository.MemberRepository;
import community.mingle.api.enums.Semester;
import community.mingle.api.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static community.mingle.api.global.exception.ErrorCode.MEMBER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class TimetableService {

    private final TimetableRepository timetableRepository;
    private final MemberRepository memberRepository;

    public Timetable createTimetable(Long memberId, int year, int semester) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));
        Semester semesterEnum = Semester.findSemester(year, semester);
        List<Timetable> timetableList = timetableRepository.findAllByMemberOrderByOrderNumberDesc(member);
        int orderNumber;
        if(timetableList.isEmpty()) orderNumber = 1;
        else orderNumber = timetableList.get(0).getOrderNumber() + 1;

        Timetable timetable = Timetable.builder()
                .semester(semesterEnum)
                .orderNumber(orderNumber)
                .isPinned(false)
                .member(member)
                .build();

        return timetableRepository.save(timetable);
    }
}
