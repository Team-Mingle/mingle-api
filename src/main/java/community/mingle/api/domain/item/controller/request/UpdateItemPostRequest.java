package community.mingle.api.domain.item.controller.request;

import community.mingle.api.enums.CurrencyType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;



@Builder
public record UpdateItemPostRequest(
        @NotBlank
        String title,
        @NotBlank
        String content,
        @NotNull
        Long price,
        @NotNull
        CurrencyType currency,
        @NotBlank
        String location,
        @NotBlank(message = "오픈채팅방 링크를 입력해주세요.")
        @Pattern(regexp = "^https:\\/\\/open\\.kakao\\.com\\/.*$", message = "오픈채팅방 링크에 오류가 있습니다. 한번 더 확인해 주세요.")
        String chatUrl,
        @NotNull
        boolean isAnonymous,

        List<String> imageUrlsToDelete,
        List<MultipartFile> imagesToAdd
) {
}
