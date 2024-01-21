package community.mingle.api.domain.item.controller.request;

import community.mingle.api.enums.CurrencyType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.*;

import java.util.List;


public record CreateItemRequest(
        @NotBlank
        String title,
        @NotNull
        Long price,
        @NotNull
        CurrencyType currencyType,
        @NotBlank
        String content,
        @NotBlank
        String location,
        @NotBlank(message = "오픈채팅방 링크를 입력해주세요.")
        @Pattern(regexp = "^https:\\/\\/open\\.kakao\\.com\\/.*$", message = "오픈채팅방 링크에 오류가 있습니다. 한번 더 확인해 주세요.")
        String chatUrl,
        @NotNull
        Boolean isAnonymous,
        @NotEmpty(message = "최소 한 장 이상의 사진을 첨부해 주세요.")
        @Size(min = 1, max = 5, message = "1개 이상, 5개 이하의 사진을 선택해 주세요.")
        List<MultipartFile> multipartFile
) {
}
