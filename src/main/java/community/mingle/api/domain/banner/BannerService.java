package community.mingle.api.domain.banner;

import community.mingle.api.domain.member.entity.University;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BannerService {

    private final BannerRepository bannerRepository;

    public List<Banner> findBanner(University university) {
        return bannerRepository.findByUniversityId(university.getId());
    }

    @Transactional
    public Banner saveBanner(Banner banner) {
        return bannerRepository.save(banner);
    }
}
