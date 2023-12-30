package community.mingle.api.domain.course.repository;

import community.mingle.api.domain.course.entity.Timetable;
import community.mingle.api.domain.member.entity.Member;
import community.mingle.api.enums.Semester;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TimetableRepository extends JpaRepository<Timetable, Long> {

    public List<Timetable> findAllByMemberAndSemesterOrderByOrderNumberDesc(Member member, Semester semester);
    public List<Timetable> findAllByMemberAndSemesterOrderByOrderNumberAsc(Member member, Semester semester);

    public List<Timetable> findAllByMember(Member member);
    public Optional<Timetable> findByMemberAndSemesterAndIsPinnedIsTrue(Member member, Semester semester);

    public List<Timetable> findAllByMemberAndIsPinnedTrue(Member member);
}
