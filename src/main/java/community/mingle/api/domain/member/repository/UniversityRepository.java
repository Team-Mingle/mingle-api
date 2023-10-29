package community.mingle.api.domain.member.repository;


import community.mingle.api.domain.member.entity.University;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface UniversityRepository extends JpaRepository<University, Integer> {

}