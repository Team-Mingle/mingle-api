package community.mingle.api.domain.banner;

import community.mingle.api.domain.auth.service.TokenService;
import community.mingle.api.domain.banner.response.BannerResponse;
import community.mingle.api.domain.banner.response.CreateBannerResponse;
import community.mingle.api.domain.member.entity.Member;
import community.mingle.api.domain.member.service.MemberService;
import community.mingle.api.global.s3.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static community.mingle.api.enums.ContentStatusType.ACTIVE;

@Service
@RequiredArgsConstructor
public class BannerFacade {

    private final BannerService bannerService;
    private final TokenService tokenService;
    private final MemberService memberService;
    private final S3Service s3Service;



    public List<BannerResponse> findBanner() {
        return bannerService.findBanner().stream()
                .map(b -> BannerResponse.builder()
                        .id(b.getId())
                        .imgUrl(b.getImgUrl())
                        .linkUrl(b.getLinkUrl())
                        .build()
                ).toList();
    }

    @Transactional
    public CreateBannerResponse createBanner(List<MultipartFile> multipartFile, String linkUrl) {
        Long memberId = tokenService.getTokenInfo().getMemberId();
        Member member = memberService.getMember(memberId);
        List<String> imgUrls = s3Service.uploadFile(multipartFile, "banner");
        imgUrls.stream()
                .map(imgUrl -> Banner.builder()
                        .member(member)
                        .imgUrl(imgUrl)
                        .linkUrl(linkUrl)
                        .statusType(ACTIVE)
                        .build()
                ).forEach(bannerService::saveBanner);
        return new CreateBannerResponse(imgUrls);

    }
}
