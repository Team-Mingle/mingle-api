package community.mingle.api.domain.member.repository;

import community.mingle.api.domain.member.entity.MemberAuthPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberAuthPhotoRepository extends JpaRepository<MemberAuthPhoto, Long> {
}
