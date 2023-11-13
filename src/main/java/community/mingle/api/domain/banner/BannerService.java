package community.mingle.api.domain.banner;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BannerService {

    private final BannerRepository bannerRepository;

    public List<Banner> findBanner() {
        return bannerRepository.findAll();
    }

    @Transactional
    public Banner saveBanner(Banner banner) {
        return bannerRepository.save(banner);
    }
}
