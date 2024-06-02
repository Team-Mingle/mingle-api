package community.mingle.api.domain.member.repository;

import community.mingle.api.domain.member.entity.MemberAuthPhoto;
import community.mingle.api.enums.MemberStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberAuthPhotoRepository extends JpaRepository<MemberAuthPhoto, Long> {

    public List<MemberAuthPhoto> findAllByMemberStatus(MemberStatus status);
}
