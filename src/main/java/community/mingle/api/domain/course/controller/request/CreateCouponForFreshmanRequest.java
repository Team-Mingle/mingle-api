package community.mingle.api.domain.course.controller.request;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public record CreateCouponForFreshmanRequest(
        List<MultipartFile> multipartFile
) {
}
