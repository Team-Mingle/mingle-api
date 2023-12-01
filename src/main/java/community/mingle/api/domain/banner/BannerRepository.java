package community.mingle.api.domain.banner;

import community.mingle.api.domain.member.entity.University;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BannerRepository extends JpaRepository<Banner, Integer> {

    List<Banner> findByMemberUniversity(University university);
}
