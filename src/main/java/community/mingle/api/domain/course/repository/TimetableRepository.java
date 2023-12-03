package community.mingle.api.domain.course.repository;

import community.mingle.api.domain.course.entity.Timetable;
import community.mingle.api.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TimetableRepository extends JpaRepository<Timetable, Long> {

    public List<Timetable> findAllByMemberOrderByOrderNumberDesc(Member member);
}
